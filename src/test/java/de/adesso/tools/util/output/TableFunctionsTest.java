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

package de.adesso.tools.util.output;

import de.adesso.tools.common.builder.List2DBuilder;
import de.adesso.tools.functions.MoreCollectors;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 26.05.16.
 */
public class TableFunctionsTest {

    @Test
    public void testFormatTable() throws Exception {
        List<List<String>> lists = List2DBuilder.matrixOf("Y,Y,Y,Y,N,Y,Y,N,N,Y,Y,N,Y,N,Y").dim(5, 5).build();

        TableFormat.Builder builder = TableFormat.newBuilder();

        List<ColumnFormat> formats = IntStream.range(0, 5)
                .mapToObj(i -> ColumnFormat.newBuilder()
                        .withWidth(1)
                        .withAlign(Align.CENTER)
                        .build())
                .collect(Collectors.toList());

        builder.columnFormats(formats);

        builder.addColumnGroup().withFirstColumn(0).withLastColumn(5).withGroupWidth(3).done();

        List<List<String>> collect = Stream.of(lists)
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());

        for (List<String> l : collect) {
            for(String s : l) {
                System.out.println(s);
            }
        }
    }
}