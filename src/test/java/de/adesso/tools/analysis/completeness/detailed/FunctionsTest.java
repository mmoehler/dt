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

package de.adesso.tools.analysis.completeness.detailed;

import de.adesso.tools.common.ListBuilder;
import de.adesso.tools.common.MatrixBuilder;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.BinaryOperator;

import static de.adesso.tools.analysis.completeness.detailed.Functions.*;
import static de.adesso.tools.functions.Adapters.Matrix.adapt;
import static de.adesso.tools.functions.MatrixFunctions.transpose;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by mmoehler on 25.03.16.
 */
public class FunctionsTest {
    @Test
    public void consolidateTest() {
        ObservableList<ObservableList<String>> conditions = MatrixBuilder.observable(MatrixBuilder.on("N,N,Y,N,Y,N,-,Y,-").dim(3, 3).build());
        List<List<String>> consolidated = Functions.consolidate().apply(adapt(conditions));
        dumpTableItems("CONS", consolidated);
    }


    @Test
    public void decisionMatrixTest() {
        List<List<String>> conditions = MatrixBuilder.on("Y,Y,N,N,-,Y,Y,-,Y,N,-,-").dim(3, 4).build();
        List<List<Integer>> decisionMatrix = makeDecisionMatrix(conditions);
        dumpTableItems("D",decisionMatrix);
    }

    @Test
    public void decisionMatrix2Test() {
        List<List<String>> conditions = MatrixBuilder.on("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();
        List<List<Integer>> decisionMatrix = makeDecisionMatrix(conditions);
        dumpTableItems("D",decisionMatrix);
    }

    @Test
    public void bothTest() {
        List<List<String>> conditions = MatrixBuilder.on("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();
        List<List<Integer>> decisionMatrix = makeDecisionMatrix(conditions);
        List<List<Integer>> maskMatrix = makeMaskMatrix(conditions);
        dumpTableItems("D",decisionMatrix);
        dumpTableItems("M",maskMatrix);
    }

    @Test
    public void bothTransformedTest() {
        List<List<String>> conditions = MatrixBuilder.on("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();
        List<List<Integer>> decisionMatrix = transpose(makeDecisionMatrix(conditions));
        List<List<Integer>> maskMatrix = transpose(makeMaskMatrix(conditions));
        dumpTableItems("D",decisionMatrix);
        dumpTableItems("M",maskMatrix);
    }


    @Test
    public void maskMatrixTest() {
        List<List<String>> conditions = MatrixBuilder.on("Y,Y,N,N,-,Y,Y,-,Y,N,-,-").dim(3, 4).build();
        List<List<Integer>> maskMatrix = makeMaskMatrix(conditions);
        dumpTableItems("M",maskMatrix);
    }

    @Test
    public void maskMatrix2Test() {
        List<List<String>> conditions = MatrixBuilder.on("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();
        List<List<Integer>> maskMatrix = makeMaskMatrix(conditions);
        dumpTableItems("M",maskMatrix);
    }

    @Test
    public void parameterBuilder2Test() {
        List<List<String>> lefts = MatrixBuilder.on("N,-,-,-").dim(3,1).transposed().build();
        List<String> right = ListBuilder.on("Y,Y,Y,N").build();

        Optional<List<List<String>>> actual = lefts.stream()
                .map(l -> columnDifference.apply(l, right)).peek(System.out::println)
                .reduce(merge);

        List<List<String>> ret = (actual.isPresent()) ? transpose(actual.get()) : Collections.emptyList();

        dumpTableItems("ACTUAL",ret);
    }

    @Test
    public void parameterBuilder4Test() {
        final List<List<String>> rights = MatrixBuilder.on("Y,Y,N,N,Y,Y,Y,N,N,-,N,Y").dim(3,4).transposed().build();
        List<List<String>> tmp = MatrixBuilder.on("-,-,-").dim(3,1).transposed().build();
        final List<List<String>>[] lefts = new List[]{tmp};
        for (List<String> right : rights) {
            lefts[0] = lefts[0].stream()
                    .map(l -> columnDifference.apply(l, right))
                    .reduce(new ArrayList<>(), merge);
            lefts[0] = transpose(consolidate().apply(transpose(lefts[0])));
        }
        List<List<String>> actual = transpose(lefts[0]);
        dumpTableItems("ACTUAL",actual);

        final List<List<String>> expected = MatrixBuilder.on("Y,N,N,N,N,Y,-,N,Y").dim(3,3).build();

        Iterator<List<String>> aIt = actual.iterator();
        Iterator<List<String>> eIt = expected.iterator();

        for(;aIt.hasNext() && eIt.hasNext();) {
            assertThat(aIt.next(), equalTo(eIt.next()));
        }
    }

    @Test
    public void parameterBuilder5Test() {
        final List<List<String>> rights = MatrixBuilder.on("Y,Y,Y,Y,Y,Y,Y,Y,Y,N,Y,Y,N,N,Y,Y,N,Y,N,Y").dim(4,5).transposed().build();
        List<List<String>> tmp = MatrixBuilder.on("-,-,-,-").dim(4,1).transposed().build();
        final List<List<String>>[] lefts = new List[]{tmp};
        for (List<String> right : rights) {
            lefts[0] = lefts[0].stream()
                    .map(l -> columnDifference.apply(l, right))
                    .reduce(new ArrayList<>(), merge);
            lefts[0] = transpose(consolidate().apply(transpose(lefts[0])));
        }
        List<List<String>> actual = transpose(lefts[0]);
        dumpTableItems("ACTUAL",actual);

    }


    BinaryOperator<List<List<String>>> merge = (l,r) -> {r.forEach(l::add);return l;};

    // ----------

    public static <T> void dumpTableItems(String msg, List<List<T>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpTableItems(String msg, ObservableList<ObservableList<String>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpMap(String msg, Map<?,?> map) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        map.forEach((k,v) -> System.out.println("\t" + k + " -> " + v));
        System.out.println("<<<<<<<<<<\n");
    }

    public static <T> void dumpList1DItems(String msg, List<T> list1D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list1D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }


}