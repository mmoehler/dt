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

import de.adesso.tools.common.ListBuilder;
import de.adesso.tools.common.MatrixBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.adesso.tools.common.MatrixBuilder.observable;
import static de.adesso.tools.common.MatrixBuilder.on;
import static de.adesso.tools.functions.MatrixFunctions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;


/**
 * Functionality Testds for the Matrix operations
 * Created by moehler on 09.02.2016.
 */
public class MatrixFunctionsTest {
    public final static String Y = "Y";
    public final static String N = "N";
    public static final String QMARK = "?";
    public static final Supplier<String> QMARKS = () -> "?";


    @org.testng.annotations.Test
    public void testCopyMatrix() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> actual = copy(original);
        assertThat(original, equalTo(actual));
    }

    @org.testng.annotations.Test
    public void testRemoveColumnsAtIndices() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = observable(on("Y,Y,Y,N,Y,Y").dim(3, 2).build());
        ObservableList<ObservableList<String>> actual = removeColumnsAt(original, Arrays.asList(1, 3));
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }

    }

    @Test
    public void testRemoveLastColumn() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("1,2,3,4,1,2,3,4,1,2,3,4").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = observable(on("1,2,3,1,2,3,1,2,3").dim(3, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.removeLastColumn(original);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }

    }

    @Test
    public void testInsertColumnsAt() throws Exception {
        ObservableList<ObservableList<String>> expected = observable(on("1,?,1,2,?,2,3,?,3").dim(3, 3).build());
        ObservableList<ObservableList<String>> original = observable(on("1,1,2,2,3,3").dim(3, 2).build());
        final Integer[] indices = {1};
        ObservableList<ObservableList<String>> actual = MatrixFunctions.insertColumnsAt(original, Arrays.asList(indices), () -> QMARK);

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testRemoveRowsAtIndices() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(on("1,1,1,2,2,2,3,3,3,4,4,4,5,5,5").dim(5, 3).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(on("2,2,2,4,4,4").dim(2, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.removeRowsAt(original, Arrays.asList(0, 2, 4));
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testRemoveLastRow() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(on("y,y,y,n,n,n,y,y,y").dim(3, 3).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(on("y,y,y,n,n,n").dim(2, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.removeLastRow(original);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testInsertRowsAt() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("1,2,3,4,5,6,7,8,9").dim(3, 3).build());
        ObservableList<ObservableList<String>> expected = observable(on("1,2,3,?,?,?,4,5,6,?,?,?,7,8,9").dim(5, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.insertRowsAt(original, Arrays.asList(1, 3), () -> QMARK);

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        assertEquals(actual.size(), expected.size());

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testTranspose() throws Exception {
        ObservableList<ObservableList<String>> expected = observable(on("1,2,3,1,2,3,1,2,3").dim(3, 3).build());
        ObservableList<ObservableList<String>> original = observable(on("1,2,3,?,?,?,1,2,3,?,?,?,1,2,3").dim(5, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.transpose(original);
        //assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        actual.remove(2);

        System.out.println("actual   = " + actual);

        actual = MatrixFunctions.transpose(actual);

        System.out.println("actual   = " + actual);
    }


    // TODO Move the following Tests to ListsTest !!

    @Test
    public void testCopyListRemovedElementsAtIndices() throws Exception {
        ObservableList<String> original = FXCollections.observableArrayList("0", "1", "2", "3", "4", "5");
        ObservableList<String> expected = FXCollections.observableArrayList("0", "2", "3", "5");
        ObservableList<String> actual = ListFunctions.removeElementsAt(original, Arrays.asList(1, 4));

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @org.junit.Test
    public void testCopyRow() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<String> actual = copyRow(original.get(2));
        ObservableList<String> expected = original.get(2);
        assertThat(actual, not(sameInstance(expected)));
        assertThat(actual, equalTo(expected));
    }

    @org.junit.Test
    public void testAddRow() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = observable(on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N,?,?,?,?").dim(4, 4).build());
        List<String> expectedAddedRow = ListBuilder.on("?,?,?,?").build();

        ObservableList<ObservableList<String>> actual = addRow(original, QMARKS);

        assertThat(actual, hasSize(4));
        assertThat(actual.get(3), equalTo(expectedAddedRow));
        assertThat(actual, equalTo(expected));

    }

    @org.junit.Test
    public void testAddColumn() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = observable(on("Y,Y,Y,Y,?,Y,Y,N,N,?,Y,N,Y,N,?").dim(3, 5).build());

        ObservableList<ObservableList<String>> actual = addColumn(original, QMARKS);

        final int expectedColSize = expected.get(0).size();
        final int actualColSize = actual.get(0).size();

        final ObservableList<String> actualAddedColumn = original.stream().map(a -> new LinkedList<>(a).getLast())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        final ObservableList<String> expectedAddedColumn = FXCollections.observableArrayList("?", "?", "?");


        assertThat(actualColSize, equalTo(expectedColSize));
        assertThat(actual.get(0), hasSize(5));
        assertThat(actualAddedColumn, equalTo(expectedAddedColumn));
        assertThat(actual, equalTo(expected));

    }

    @org.junit.Test
    public void testNewRow() throws Exception {
        List<String> expected = ListBuilder.on("?,?,?,?,?,?,?,?,?,?").build();
        ObservableList<String> actual = newRow(10, QMARKS);
        assertThat(actual, equalTo(expected));
    }

    @org.junit.Test
    public void testSwapRowsAt() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on("A,A,A,A,B,B,B,B,C,C,C,C").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = observable(on("A,A,A,A,C,C,C,C,B,B,B,B").dim(3, 4).build());

        ObservableList<ObservableList<String>> actual = swapRowsAt(original, 1, 2);

        assertThat(actual, equalTo(expected));
    }

    @org.junit.Test
    public void testSwapColumnsAt() throws Exception {
        ObservableList<ObservableList<String>> original = observable(on(
                        "A,B,C,D," +
                        "A,B,C,D," +
                        "A,B,C,D").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = observable(on(
                        "A,D,C,B," +
                        "A,D,C,B," +
                        "A,D,C,B").dim(3, 4).build());

        ObservableList<ObservableList<String>> actual = swapColumnsAt(original, 1, 3);

        assertThat(actual, equalTo(expected));
    }
}