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

import de.adesso.tools.Dump;
import de.svenjacobs.loremipsum.LoremIpsum;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.adesso.tools.functions.MoreCollectors.toSingleObject;
import static de.adesso.tools.util.StringFunctions.Align.*;
import static de.adesso.tools.util.StringFunctions.*;

/**
 * Created by moehler on 24.05.2016.
 */
public class StringFunctionsTest {

    @Test
    public void testSplit() throws Exception {
        String text = new LoremIpsum().getParagraphs(1);

        List<String> actual = Stream.of(text)
                .map(split())
                .collect(toSingleObject());

        Dump.dumpList1DItems("SPLIITED", actual);
    }

    @Test
    public void testNormalize() throws Exception {
        String text = new LoremIpsum().getParagraphs(1);

        List<String> actual = Stream.of(text)
                .map(split())
                .collect(toSingleObject());

        actual = Stream.of(actual).map(normalize(40)).collect(toSingleObject());

        Dump.dumpList1DItems("NORMALIZED", actual);
    }

    /**
     * <div stle="width:300px;">
     * <h3>Usage;</h3>
     *
     *      <div style="border: 1px solid red; padding: 5px;">
     *      <pre>actual = normalizedList.stream().map(justify(40, CENTER)).collect(Collectors.toList());</pre>
     *      </div>
     * </div>
     *
     * @throws Exception
     */

    @Test
    public void testJustifyCENTER() throws Exception {
        String text = new LoremIpsum().getParagraphs(1);

        List<String> actual = Stream.of(text)
                .map(split())
                .collect(toSingleObject());

        actual = Stream.of(actual).map(normalize(40)).collect(toSingleObject());

        actual = actual.stream().map(justify(40, CENTER)).collect(Collectors.toList());

        //actual = actual.stream().map(s -> s.replaceAll(" ","_")).collect(Collectors.toList());
        Dump.dumpList1DItems("CENTERED", actual);

    }

    @Test
    public void testJustifyLEFT() throws Exception {
        String text = new LoremIpsum().getParagraphs(1);

        List<String> actual = Stream.of(text)
                .map(split())
                .collect(toSingleObject());

        actual = Stream.of(actual).map(normalize(40)).collect(toSingleObject());

        actual = actual.stream().map(justify(40, LEFT)).collect(Collectors.toList());

        //actual = actual.stream().map(s -> s.replaceAll(" ","_")).collect(Collectors.toList());
        Dump.dumpList1DItems("LEFT-JUSTIFIED", actual);

    }

    @Test
    public void testJustifyRIGHT() throws Exception {
        String text = new LoremIpsum().getParagraphs(1);

        List<String> actual = Stream.of(text)
                .map(split())
                .collect(toSingleObject());

        actual = Stream.of(actual).map(normalize(40)).collect(toSingleObject());

        actual = actual.stream().map(justify(40, RIGHT)).collect(Collectors.toList());

        //actual = actual.stream().map(s -> s.replaceAll(" ","_")).collect(Collectors.toList());
        Dump.dumpList1DItems("RIGHT-JUSTIFIED", actual);

    }

}