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


import com.codepoetics.protonpack.StreamUtils;
import de.adesso.tools.common.builder.List2DBuilder;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.adesso.tools.analysis.completeness.detailed.Actions.*;
import static de.adesso.tools.analysis.completeness.detailed.Conditions.*;
import static de.adesso.tools.functions.Adapters.Matrix.adapt;
import static java.util.stream.Collectors.toList;

/**
 * Created by mmoehler ofList 19.03.16.
 */
public class ActionsTest {
    public static void dumpTableItems(String msg, List<List<String>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpTableItems(String msg, ObservableList<ObservableList<String>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpMap(String msg, Map<?, ?> map) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        map.forEach((k, v) -> System.out.println("\t" + k + " -> " + v));
        System.out.println("<<<<<<<<<<\n");
    }

    public static <T> void dumpList1DItems(String msg, List<T> list1D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list1D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    @Test
    public void testA1OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,N,Y,Y,N,Y").build();
        List<String> ri = StringListBuilder.on("N,N,Y,Y,N,N").build();

        List<List<String>> actual = A1.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));

    }

    /*
    static List<Integer> newWithValue(int value) {
        List<Integer> result = new ArrayList<>();
        result.add(value);
        return result;
    }

    static List<Integer> addValue(List<Integer> l, int value) {
        l.add(value);
        return l;
    }
    */

    @Test
    public void testA2OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,N,Y,Y,N,Y").build();
        List<String> ri = StringListBuilder.on("N,N,Y,Y,N,Y").build();

        List<List<String>> actual = A2.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));

    }

    @Test
    public void testA3OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,N,Y,-,N,Y").build();
        List<String> ri = StringListBuilder.on("N,N,Y,Y,N,Y").build();

        List<List<String>> actual = A3.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));

    }

    @Test
    public void testA4OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,N,Y,-,N,Y").build();
        List<String> ri = StringListBuilder.on("N,N,Y,Y,N,Y").build();

        List<List<String>> actual = A4.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));
    }

    @Test
    public void testA5OK() {
        int expected = 0;
//        List<String> rf = StringListBuilder.on("-,-,-").build();
//        List<String> ri = StringListBuilder.on("Y,Y,N").build();

//        List<String> rf = StringListBuilder.on("N,-,-").build();
//        List<String> ri = StringListBuilder.on("N,Y,N").build();

//        List<String> rf = StringListBuilder.on("-,N,-").build();
//        List<String> ri = StringListBuilder.on("N,N,Y").build();


//        List<String> rf = StringListBuilder.on("-,-,-").build();
//        List<String> ri = StringListBuilder.on("Y,Y,N").build();


        List<String> rf = StringListBuilder.on("-,Y,-,Y").build();
        List<String> ri = StringListBuilder.on("N,-,Y,Y").build();


        List<List<String>> actual = __A5.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));
    }

    @Test
    public void testCreateMask() {
        List<String> rf = StringListBuilder.on("Y,N,-").build();
        List<String> ri = StringListBuilder.on("N,Y,N").build();

        //noinspection unchecked
        Function<List<Tuple2<String, String>>, Integer>[] conditions = new Function[]{
                B1, B2, B3, B4
        };

        List<Tuple2<String, String>> prototype = StreamUtils
                .zip(rf.stream(), ri.stream(), (x, y) -> Tuple.of(x, y))
                .collect(toList());


        final List<Integer> mask = Arrays.stream(conditions)
                .map(c -> c.apply(prototype))
                .collect(Collectors.toList());

        dumpList1DItems("MASK", mask);


    }

    @Test
    public void testA5Consolidate() {
        ObservableList<ObservableList<String>> conditions = adapt(List2DBuilder.matrixOf("N,N,Y,N,Y,N,-,Y,-").dim(3, 3).build());
        @SuppressWarnings("unchecked") ObservableList<ObservableList<String>>[] observables = new ObservableList[]{conditions};
        /*
        for (int i = 0; i < conditions.size(); i++) {
            ObservableList<ObservableList<String>> tmp = List2DFunctions.removeRowsAt(observables[0],i);
            ObservableList<ObservableList<String>> cur = List2DFunctions.transpose(tmp);
            Map<ObservableList<String>, List<Integer>> map = new HashMap<>();
            IntStream.range(0,cur.size()).forEach(j -> map
                    .compute(cur.get(j), (k, v) -> (v == null) ? newWithValue(j) : addValue(v,j)));
            final int row = i;
            map.entrySet().stream().filter(e -> e.getValue().size() > 1)
                    .forEach(f -> {
                        List<Integer> indices = f.getValue();
                        Collections.sort(indices, (a,b) -> (-1));
                        for (int r = 0; r<indices.size() ; r++) {
                            if(r==indices.size()-1) {
                                observables[0].get(row).set(indices.get(r), DASH);
                            } else {
                                observables[0] = List2DFunctions.removeColumnsAt(observables[0], indices.get(r));
                            }
                        }
                    });
        }*/

        ObservableList<ObservableList<String>> observableLists = observables[0];
        List<List<String>> lists = adapt(observableLists);
        List<List<String>> actual = Functions.consolidate().apply(lists);


        dumpTableItems("RESULT-1", observables[0]);
    }


}