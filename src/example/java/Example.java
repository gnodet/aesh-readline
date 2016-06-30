/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.jboss.aesh.readline.Prompt;
import org.jboss.aesh.readline.Readline;
import org.jboss.aesh.readline.completion.Completion;
import org.jboss.aesh.terminal.formatting.CharacterType;
import org.jboss.aesh.terminal.formatting.Color;
import org.jboss.aesh.terminal.formatting.TerminalCharacter;
import org.jboss.aesh.terminal.formatting.TerminalColor;
import org.jboss.aesh.tty.Connection;
import org.jboss.aesh.tty.Signal;
import org.jboss.aesh.tty.terminal.TerminalConnection;
import org.jboss.aesh.util.LoggerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class Example {

    private static final Logger LOGGER = LoggerUtil.getLogger(Example.class.getName());
    private static boolean masking = false;
    private static Prompt defaultPrompt;
    private static Thread sleeperThread;

    public static void main(String[] args) throws IOException {
        LoggerUtil.doLog();

        defaultPrompt = createDefaultPrompt();
        Readline readline = new Readline();
        TerminalConnection connection = new TerminalConnection();
        connection.setSignalHandler( signal -> {
            if(signal == Signal.INT) {
                connection.write("we're catching ctrl-c, just continuing.\n");
                if(sleeperThread != null)
                sleeperThread.interrupt();
            }
        });

        connection.setCloseHandler(close -> {
            connection.write("we're shutting down, do something...!");
        });

        //readInput(connection, readline, defaultPrompt);
        readInput(connection, readline, new Prompt("[aesh@rules]$ "));
        connection.startBlockingReader();
    }

    public static void readInput(Connection connection, Readline readline, Prompt prompt) {
        readline.readline(connection, prompt, line -> {
            connection.write("=====> "+line+"\n");
            if(line == null) {
                connection.write("got eof, lets quit.\n");
                connection.close();
            }
            else if(masking) {
                connection.write("got password: "+line+", stopping masking\n");
                masking = false;
                readInput(connection, readline, defaultPrompt);
            }
            else if(line.equals("quit") || line.equals("exit")) {
                connection.write("we're quitting...\n");
                connection.close();
                return;
            }
            else if(line.equals("sleep")) {
                LOGGER.info("got sleep");
                try {
                    connection.write("we're going to sleep for 5 seconds, you can interrupt me if you want.\n");
                    sleeperThread = Thread.currentThread();
                    Thread.sleep(5000);
                    connection.write("done sleeping, returning.\n");
                    readInput(connection, readline, defaultPrompt);
                }
                catch (InterruptedException e) {
                    connection.write("we got interrupted, lets continue...\n");
                    readInput(connection, readline, defaultPrompt);
                }
            }
            else if(line.equals("login")) {
                masking = true;
                readInput(connection, readline, new Prompt("password: ", (char) 0));
            }
            else if(line.startsWith("man")) {
                connection.write("trying to wait for input:\n");
                Readline inputLine = new Readline();
                inputLine.readline(connection, "write something: ", newLine -> {
                    connection.write("we got: "+newLine+"\n");
                    readInput(connection, readline, prompt);
                });
            }
            else {
                readInput(connection, readline, prompt);
            }
        }, getCompletions());
    }

    private static List<Completion> getCompletions() {
        List<Completion> completions = new ArrayList<>();
        completions.add( completeOperation -> {
            if("exit".startsWith(completeOperation.getBuffer()))
                completeOperation.addCompletionCandidate("exit");
            if("quit".startsWith(completeOperation.getBuffer()))
                completeOperation.addCompletionCandidate("quit");
            if("sleep".startsWith(completeOperation.getBuffer()))
                completeOperation.addCompletionCandidate("sleep");
            if("login".startsWith(completeOperation.getBuffer()))
                completeOperation.addCompletionCandidate("login");
            if("man".startsWith(completeOperation.getBuffer()))
                completeOperation.addCompletionCandidate("man");

        });
       return completions;
    }

    private static Prompt createDefaultPrompt() {
            List<TerminalCharacter> chars = new ArrayList<>();
        chars.add(new TerminalCharacter('[', new TerminalColor(Color.BLUE, Color.DEFAULT)));
        chars.add(new TerminalCharacter('t', new TerminalColor(Color.RED, Color.DEFAULT),
                CharacterType.ITALIC));
        chars.add(new TerminalCharacter('e', new TerminalColor(Color.RED, Color.DEFAULT),
                CharacterType.INVERT));
        chars.add(new TerminalCharacter('s', new TerminalColor(Color.RED, Color.DEFAULT),
                CharacterType.CROSSED_OUT));
        chars.add(new TerminalCharacter('t', new TerminalColor(Color.RED, Color.DEFAULT),
                CharacterType.BOLD));
        chars.add(new TerminalCharacter(']', new TerminalColor(Color.BLUE, Color.DEFAULT),
                CharacterType.FAINT));
        chars.add(new TerminalCharacter('$', new TerminalColor(Color.GREEN, Color.DEFAULT),
                CharacterType.UNDERLINE));
        chars.add(new TerminalCharacter(' ', new TerminalColor(Color.DEFAULT, Color.DEFAULT)));

        return new Prompt(chars);

    }

}