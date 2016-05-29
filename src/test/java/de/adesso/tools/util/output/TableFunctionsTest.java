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

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Strings;
import de.adesso.tools.common.builder.List2DBuilder;
import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.print.AsciiRow;
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
        List<AsciiRow> lists = List2DBuilder.matrixOf("Y,Y,Y,Y,N,Y,Y,N,N,Y,Y,N,Y,N,Y").dim(5, 5).build()
                .stream().map(AsciiRow::new).collect(Collectors.toList());

        TableFormat.Builder builder = TableFormat.newBuilder();

        List<ColumnFormat> formats = IntStream.range(0, 5)
                .mapToObj(i -> ColumnFormat.newBuilder()
                        .width(1)
                        .align(Align.CENTER)
                        .build())
                .collect(Collectors.toList());

        builder.columnFormats(formats);

        builder.addColumnGroup().withFirstColumn(0).withLastColumn(5).withGroupWidth(3).done();

        List<AsciiRow> collect = Stream.of(lists)
                .map(TableFunctions.formatTable(builder.build()))

                .collect(MoreCollectors.toSingleObject());

        for (AsciiRow l : collect) {
            for(String s : l) {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testFormatTableWithDefHeader() throws Exception {
        String header0 = IntStream.range(0, 5).mapToObj(i -> (((i % 2)==0) && ((i / 2) > 0)) ? String.valueOf(i / 2) : ".").reduce("", (a, b) -> (Strings.isNullOrEmpty(a)) ? (a + b) : (a + "," + b));
        System.out.println("header0 = " + header0);
        String header1 = IntStream.range(0, 5).mapToObj(i -> String.valueOf(i % 2)).reduce("", (a, b) -> (Strings.isNullOrEmpty(a)) ? (a + b) : (a + "," + b));
        System.out.println("header1 = " + header1);
        List<List<String>> header_0 = List2DBuilder.matrixOf(header0).dim(1, 5).build();
        List<List<String>> header_1 = List2DBuilder.matrixOf(header1).dim(1, 5).build();

        List<List<String>> lists = List2DBuilder.matrixOf("Y,Y,Y,Y,N,Y,Y,N,N,Y,Y,N,Y,N,Y").dim(3, 5).build();
        List<List<String>> content = Lists.newLinkedList();
        content.addAll(header_0);
        content.addAll(header_1);
        content.addAll(lists);

        List<AsciiRow> asciiRows = content.stream().map(AsciiRow::new).collect(Collectors.toList());

        TableFormat.Builder builder = TableFormat.newBuilder();

        List<ColumnFormat> formats = IntStream.range(0, 5)
                .mapToObj(i -> ColumnFormat.newBuilder()
                        .width(1)
                        .align(Align.CENTER)
                        .build())
                .collect(Collectors.toList());

        builder.columnFormats(formats);

        List<AsciiRow> collect = Stream.of(asciiRows)
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());

        for (AsciiRow l : collect) {
            for(String s : l) {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testFormatTableWithDeclHeader() throws Exception {
        final String header0 = "#,Condition,Indicators";
        List<List<String>> header_0 = List2DBuilder.matrixOf(header0).dim(1, 3).build();

        List<List<String>> lists = List2DBuilder.matrixOf("C01,A>0,YN,C02,B=66,YN,C03,X<B,YN,C04,K*=1024,YN,C05,R=K,YN").dim(5, 3).build();
        List<List<String>> content = Lists.newLinkedList();
        content.addAll(header_0);
        //content.addAll(header_1);
        content.addAll(lists);

        List<AsciiRow> asciiRows = content.stream().map(AsciiRow::new).collect(Collectors.toList());

        TableFormat.Builder builder = TableFormat.newBuilder();

        builder.addColumnFormat().width(5).align(Align.CENTER).done()
                .addColumnFormat().width(20).align(Align.LEFT).done()
                .addColumnFormat().width(12).align(Align.CENTER).done();

        List<AsciiRow> collect = Stream.of(asciiRows)
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());

        for (AsciiRow l : collect) {
            for(String s : l) {
                System.out.println(s);
            }
        }
        System.out.println();
        for (AsciiRow l : collect) {
            for(String s : l) {
                System.out.println(s);
            }
        }

    }


    @Test
    public void testFormatTableWithColumnSeparator5() throws Exception {
        List<List<String>> lists = List2DBuilder.matrixOf("Y,Y,Y,Y,N,Y,Y,N,N,Y,Y,N,Y,N,Y").dim(5, 5).build();

        List<AsciiRow> asciiRows = lists.stream().map(AsciiRow::new).collect(Collectors.toList());

        TableFormat.Builder builder = TableFormat.newBuilder();

        List<ColumnFormat> formats = IntStream.range(0, 5)
                .mapToObj(i -> ColumnFormat.newBuilder()
                        .width(1)
                        .align(Align.CENTER)
                        .build())
                .collect(Collectors.toList());

        builder.columnFormats(formats);

        builder.columnSeparator().character(' ').length(5).done();

        builder.addColumnGroup().withFirstColumn(0).withLastColumn(5).withGroupWidth(3).done();

        List<AsciiRow> collect = Stream.of(asciiRows)
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());

        for (AsciiRow l : collect) {
            for(String s : l) {
                System.out.println(s);
            }
        }
    }
}