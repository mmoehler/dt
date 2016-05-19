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

package de.adesso.tools.functions;

import com.google.common.collect.Lists;
import de.adesso.tools.Dump;
import de.adesso.tools.common.builder.List2DBuilder;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Stream;

import static de.adesso.tools.common.builder.List2DBuilder.observable;
import static de.adesso.tools.functions.MoreCollectors.toObservableList;
import static de.adesso.tools.functions.MoreCollectors.toSingleObject;
import static de.adesso.tools.functions.ObservableList2DFunctions.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by mmoehler ofList 05.05.16.
 */
public class ObservableList2DFunctionsTest {

    @Test
    public void testReplaceColumn() throws Exception {
        ObservableList<ObservableList<String>> input = observable(List2DBuilder.matrixOf("A,B,?,C,D,E,A,B,?,C,D,E,A,B,?,C,D,E").dim(3, 6).build());
        ObservableList<ObservableList<String>> expected = observable(List2DBuilder.matrixOf("A,B,X,C,D,E,A,B,Y,C,D,E,A,B,Z,C,D,E").dim(3, 6).build());

        List<String> columnData = Lists.newArrayList("X", "Y", "Z");
        ObservableList<ObservableList<String>> actual = input.stream().map(replaceColumn(columnData,2)).collect(toObservableList());

        assertEquals(expected, actual);
    }

    @Test
    public void testInsertColumn() throws Exception {
        ObservableList<ObservableList<String>> input = observable(List2DBuilder.matrixOf("A,B,C,D,E,A,B,C,D,E,A,B,C,D,E").dim(3, 5).build());
        ObservableList<ObservableList<String>> expected = observable(List2DBuilder.matrixOf("A,B,?,C,D,E,A,B,?,C,D,E,A,B,?,C,D,E").dim(3, 6).build());

        ObservableList<ObservableList<String>> actual = input.stream().map(insertColumn("?",2)).collect(toObservableList());

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveColumn() throws Exception {
        ObservableList<ObservableList<String>> input = observable(List2DBuilder.matrixOf("A,B,C,D,E,A,B,C,D,E,A,B,C,D,E").dim(3, 5).build());
        ObservableList<ObservableList<String>> expected = observable(List2DBuilder.matrixOf("A,B,D,E,A,B,D,E,A,B,D,E").dim(3, 4).build());

        ObservableList<ObservableList<String>> actual = input.stream().map(removeColumn(2)).collect(toObservableList());

        assertEquals(actual, expected);
    }

    @Test
    public void testTranspose() throws Exception {
        ObservableList<ObservableList<String>> input = observable(List2DBuilder.matrixOf("A,B,C,D,E,A,B,C,D,E,A,B,C,D,E").dim(3, 5).build());
        ObservableList<ObservableList<String>> expected = observable(List2DBuilder.matrixOf("A,A,A,B,B,B,C,C,C,D,D,D,E,E,E").dim(5, 3).build());

        ObservableList<ObservableList<String>> actual = Stream.of(input).map(transpose()).collect(toSingleObject());

        assertEquals(expected, actual);
    }

        @Test
    public void testMoveColumnOldPosLWNewPos() throws Exception {
        ObservableList<ObservableList<String>> input = observable(List2DBuilder.matrixOf("A,B,C,D,E,A,B,C,D,E,A,B,C,D,E").dim(3, 5).build());
        ObservableList<ObservableList<String>> expected = observable(List2DBuilder.matrixOf("A,C,D,B,E,A,C,D,B,E,A,C,D,B,E").dim(3, 5).build());

        ObservableList<ObservableList<String>> actual = input.stream().map(moveColumn(1,3)).collect(toObservableList());

        assertEquals(expected, actual);
    }

    @Test
    public void testMoveColumnOldPosGTNewPos() throws Exception {
        ObservableList<ObservableList<String>> expected = observable(List2DBuilder.matrixOf("A,B,C,D,E,A,B,C,D,E,A,B,C,D,E").dim(3, 5).build());
        ObservableList<ObservableList<String>> input = observable(List2DBuilder.matrixOf("A,C,D,B,E,A,C,D,B,E,A,C,D,B,E").dim(3, 5).build());

        ObservableList<ObservableList<String>> actual = input.stream().map(moveColumn(3,1)).collect(toObservableList());

        assertEquals(expected, actual);
    }

    @Test
    public void testAddRow() throws Exception {
        ObservableList<ObservableList<String>> input = observable(List2DBuilder.matrixOf("A,B,C,D,E,A,B,C,D,E,A,B,C,D,E").dim(3, 5).build());
        ObservableList<ObservableList<String>> expected = observable(List2DBuilder.matrixOf("A,B,C,D,E,A,B,C,D,E,A,B,C,D,E,?,?,?,?,?").dim(4, 5).build());

        Dump.dumpTableItems("input", input);

        ObservableList<ObservableList<String>> transposed = Stream.of(input).map(transpose()).collect(toSingleObject());
        ObservableList<ObservableList<String>> modified = transposed.stream().map(new AddColumn()).collect(toObservableList());
        final ObservableList<ObservableList<String>> actual = Stream.of(modified).map(transpose()).collect(toSingleObject());


        Dump.dumpTableItems("ouput", actual);

        assertEquals(expected, actual);
    }
}