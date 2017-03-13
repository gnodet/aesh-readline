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
package org.aesh.util;

import org.jline.utils.WCWidth;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class WcWidthTest {

    @Test
    public void testWidth() {
        assertEquals(1, WCWidth.wcwidth('æ'));
        assertEquals(1, WCWidth.wcwidth('s'));
        assertEquals(1, WCWidth.wcwidth('h'));
        assertEquals(0, WCWidth.wcwidth('\0'));
        assertEquals(-1, WCWidth.wcwidth('\n'));
        assertEquals(-1, WCWidth.wcwidth('\r'));
        assertEquals(-1, WCWidth.wcwidth('\t'));
        assertEquals(-1, WCWidth.wcwidth('\u001B'));
    }

}
