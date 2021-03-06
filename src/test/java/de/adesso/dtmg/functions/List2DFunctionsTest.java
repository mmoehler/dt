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

package de.adesso.dtmg.functions;

import de.adesso.dtmg.util.List2DBuilder;
import de.adesso.dtmg.util.List2DFunctions;
import de.adesso.dtmg.util.ListBuilder;
import de.adesso.dtmg.util.ListFunctions;
import javafx.collections.FXCollections;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.adesso.dtmg.util.List2DFunctions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;


/**
 * Functionality Tests for the Matrix operations
 * Created by moehler ofList 09.02.2016.
 */
public class List2DFunctionsTest {
    public final static String Y = "Y";
    public final static String N = "N";
    public static final String QMARK = "?";
    public static final Supplier<String> QMARKS = () -> "?";


    @org.testng.annotations.Test
    public void testCopyMatrix() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        List<List<String>> actual = copy(original);
        assertThat(original, equalTo(actual));
    }

    @org.testng.annotations.Test
    public void testRemoveColumnsAtIndices() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("Y,Y,Y,N,Y,Y").dim(3, 2).build());
        List<List<String>> actual = removeColumnsAt(original, 3);
        actual = removeColumnsAt(actual, 1);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<List<String>> itA = actual.iterator();
        Iterator<List<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }

    }

    @Test
    public void testRemoveLastColumn() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("1,2,3,4,1,2,3,4,1,2,3,4").dim(3, 4).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("1,2,3,1,2,3,1,2,3").dim(3, 3).build());
        List<List<String>> actual = List2DFunctions.removeLastColumn(original);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<List<String>> itA = actual.iterator();
        Iterator<List<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }

    }

    @Test
    public void testInsertColumnsAt() throws Exception {
        List<List<String>> expected = (List2DBuilder.matrixOf("1,?,1,2,?,2,3,?,3").dim(3, 3).build());
        List<List<String>> original = (List2DBuilder.matrixOf("1,1,2,2,3,3").dim(3, 2).build());

        List<List<String>> actual = List2DFunctions.insertColumnsAt(original, 1, () -> QMARK);

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<List<String>> itA = actual.iterator();
        Iterator<List<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testRemoveRowsAtIndices() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("1,1,1,2,2,2,3,3,3,4,4,4,5,5,5").dim(5, 3).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("2,2,2,4,4,4").dim(2, 3).build());
        List<List<String>> actual = List2DFunctions.removeRowsAt(original, 4);
        actual = List2DFunctions.removeRowsAt(actual, 2);
        actual = List2DFunctions.removeRowsAt(actual, 0);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<List<String>> itA = actual.iterator();
        Iterator<List<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testRemoveLastRow() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("y,y,y,n,n,n,y,y,y").dim(3, 3).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("y,y,y,n,n,n").dim(2, 3).build());
        List<List<String>> actual = List2DFunctions.removeLastRow(original);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<List<String>> itA = actual.iterator();
        Iterator<List<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testInsertRowsAt() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("1,2,3,4,5,6,7,8,9").dim(3, 3).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("1,2,3,?,?,?,4,5,6,?,?,?,7,8,9").dim(5, 3).build());
        List<List<String>> actual = List2DFunctions.insertRowsAt(original, 1, () -> QMARK);
        actual = List2DFunctions.insertRowsAt(actual, 3, () -> QMARK);

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        assertEquals(actual.size(), expected.size());

        Iterator<List<String>> itA = actual.iterator();
        Iterator<List<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testTranspose() throws Exception {
        List<List<String>> expected = (List2DBuilder.matrixOf("1,2,3,1,2,3,1,2,3").dim(3, 3).build());
        List<List<String>> original = (List2DBuilder.matrixOf("1,2,3,?,?,?,1,2,3,?,?,?,1,2,3").dim(5, 3).build());
        List<List<String>> actual = List2DFunctions.transpose(original);
        //assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        actual.remove(2);

        System.out.println("actual   = " + actual);

        actual = List2DFunctions.transpose(actual);

        System.out.println("actual   = " + actual);
    }


    // TODO Move the following Tests to ListsTest !!

    @Test
    public void testCopyListRemovedElementsAtIndices() throws Exception {
        List<String> original = FXCollections.observableArrayList("0", "1", "2", "3", "4", "5");
        List<String> expected = FXCollections.observableArrayList("0", "2", "3", "5");
        List<String> actual = ListFunctions.removeElementsAt(original, 4);
        actual = ListFunctions.removeElementsAt(actual, 1);

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testCopyRow() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        List<String> actual = copyRow(original.get(2));
        List<String> expected = original.get(2);
        assertThat(actual, not(sameInstance(expected)));
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testAddRow() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N,?,?,?,?").dim(4, 4).build());
        List<String> expectedAddedRow = ListBuilder.ofList("?,?,?,?").build();

        List<List<String>> actual = addRow(original, QMARKS);

        assertThat(actual, hasSize(4));
        assertThat(actual.get(3), equalTo(expectedAddedRow));
        assertThat(actual, equalTo(expected));

    }

    @Test
    public void testAddColumn() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("Y,Y,Y,Y,?,Y,Y,N,N,?,Y,N,Y,N,?").dim(3, 5).build());

        List<List<String>> actual = addColumn(original, QMARKS);

        final int expectedColSize = expected.get(0).size();
        final int actualColSize = actual.get(0).size();

        final List<String> actualAddedColumn = original.stream().map(a -> new LinkedList<>(a).getLast())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        final List<String> expectedAddedColumn = FXCollections.observableArrayList("?", "?", "?");


        assertThat(actualColSize, equalTo(expectedColSize));
        assertThat(actual.get(0), hasSize(5));
        assertThat(actualAddedColumn, equalTo(expectedAddedColumn));
        assertThat(actual, equalTo(expected));

    }

    @Test
    public void testNewRow() throws Exception {
        List<String> expected = ListBuilder.ofList("?,?,?,?,?,?,?,?,?,?").build();
        List<String> actual = newRow(10, QMARKS);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testSwapRowsAt() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf("A,A,A,A,B,B,B,B,C,C,C,C").dim(3, 4).build());
        List<List<String>> expected = (List2DBuilder.matrixOf("A,A,A,A,C,C,C,C,B,B,B,B").dim(3, 4).build());

        List<List<String>> actual = swapRowsAt(original, 1, 2);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testSwapColumnsAt() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf(
                "A,B,C,D," +
                        "A,B,C,D," +
                        "A,B,C,D").dim(3, 4).build());
        List<List<String>> expected = (List2DBuilder.matrixOf(
                "A,D,C,B," +
                        "A,D,C,B," +
                        "A,D,C,B").dim(3, 4).build());

        List<List<String>> actual = swapColumnsAt(original, 1, 3);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testCopy() throws Exception {

    }

    @Test
    public void testRemoveRowsAt() throws Exception {

    }

    @Test
    public void testRemoveColumnsAt() throws Exception {

    }

    @Test
    public void testReplaceColumnsAt() throws Exception {
        List<List<String>> original = (List2DBuilder.matrixOf(
                "A,B,C,D," +
                        "A,B,C,D," +
                        "A,B,C,D").dim(3, 4).build());
        List<List<String>> expected = (List2DBuilder.matrixOf(
                "A,E,C,D," +
                        "A,E,C,D," +
                        "A,E,C,D").dim(3, 4).build());

        List<String> newColumn = ListBuilder.ofList("E,E,E").build();


        List<List<String>> actual = replaceColumnsAt(original, 1, newColumn);

        assertThat(actual, equalTo(expected));
    }
}