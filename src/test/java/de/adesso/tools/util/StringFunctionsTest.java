/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.adesso.tools.util;

import de.svenjacobs.loremipsum.LoremIpsum;
import org.testng.annotations.Test;

import static de.adesso.tools.util.StringFunctions.Align.LEFT;

/**
 * Created by moehler on 24.05.2016.
 */
public class StringFunctionsTest {

    @Test
    public void testWrapAlignCenter() throws Exception {
        String s = new LoremIpsum().getWords(30);
        System.out.println("\ns = " + s + '\n');
        s = StringFunctions.wrap(35, LEFT).apply(s);
        System.out.println(s);
    }
}