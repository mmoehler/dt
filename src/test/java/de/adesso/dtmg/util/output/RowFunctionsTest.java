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

package de.adesso.dtmg.util.output;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.adesso.dtmg.export.ascii.AsciiRow;
import de.svenjacobs.loremipsum.LoremIpsum;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static de.adesso.dtmg.functions.MoreCollectors.toSingleObject;

/**
 * Created by mmoehler on 26.05.16.
 */
public class RowFunctionsTest {

    final Map<Integer, String> cache = Maps.newLinkedHashMap();

    String getEmptyFieldData(int width) {
        if (!cache.containsKey(width)) {
            char[] c = new char[width];
            Arrays.fill(c, '.');
            cache.put(width, String.valueOf(c));
        }
        return cache.get(width);
    }


    @Test
    public void testNormalize() throws Exception {
        LoremIpsum gen = new LoremIpsum();
        final String v0 = "C01";
        final String v1 = gen.getParagraphs(1);
        //final String v1 = gen.getParagraphs(1);

        //final String v0 = "Chronische Krankheit diagnostiziert?";
        final String v2 = "Y,N";


        AsciiRow rawData = new AsciiRow(Lists.newArrayList(v0, v1, v2));


        TableFormat.Builder builder = TableFormat.newBuilder()
            .addColumnFormat().align(Align.CENTER).width(5).done()
            .addColumnFormat().align(Align.LEFT).width(40).done()
            .addColumnFormat().align(Align.CENTER).width(5).done();

        AsciiRow collect = Stream.of(rawData).map(RowFunctions.formatRow(builder.build())).collect(toSingleObject());

        /*

        List<List<String>> normalized = Stream.of(rawData).map(l -> RowFunctions.normalize(formats).apply(l)).collect(MoreCollectors.toSingleObject());

        final int maxSize = normalized.stream().mapToInt(l -> l.size()).max().getAsInt();


        List<String> collect = normalized.stream().map(l -> {
            while (l.size() < maxSize) {
                l.add(getEmptyFieldData(l.get(0).length()));
            }
            return l;
        }).reduce(new ArrayList<>(), (a, b) -> (a.isEmpty())
                ? b
                : StreamUtils.zip(a.stream(), b.stream(), (l, r) -> l + ' ' + r)
                .collect(Collectors.toList()));

        */

        //for (List<String> l : reduce) {
        System.out.println(Strings.repeat("-",50+2));
            for (String s : collect) {
                System.out.println(s);
            }
        System.out.println(Strings.repeat("-",50+2));
        //}


    }
}