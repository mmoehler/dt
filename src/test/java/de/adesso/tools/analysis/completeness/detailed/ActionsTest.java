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

import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.functions.MatrixFunctions;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.IntStream;

import static de.adesso.tools.common.Reserved.*;

/**
 * Created by mmoehler on 19.03.16.
 */
public class ActionsTest {
    @Test
    public void testA1OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,N,Y,Y,N,Y").build();
        List<String> ri = StringListBuilder.on("N,N,Y,Y,N,N").build();

        List<List<String>> actual = Actions.A1.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));

    }

    @Test
    public void testA2OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,N,Y,Y,N,Y").build();
        List<String> ri = StringListBuilder.on("N,N,Y,Y,N,Y").build();

        List<List<String>> actual = Actions.A2.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));

    }

    @Test
    public void testA3OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,N,Y,Y,N,Y").build();
        List<String> ri = StringListBuilder.on("N,N,Y,Y,N,Y").build();

        List<List<String>> actual = Actions.A3.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));

    }

    @Test
    public void testA4OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,-,Y,Y,-,Y").build();
        List<String> ri = StringListBuilder.on("N,N,N,Y,Y,Y").build();

        List<List<String>> actual = Actions.A4.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));
    }

    @Test
    public void testA5OK() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("N,-,-").build();
        List<String> ri = StringListBuilder.on("N,Y,N").build();

        List<List<String>> actual = Actions.A5.apply(rf, ri);

        dumpTableItems("ACTUAL", actual);

        //assertThat(actual, equalTo(expected));
    }

    static List<Integer> newWithValue(int value) {
        List<Integer> result = new ArrayList<>();
        result.add(value);
        return result;
    }

    static List<Integer> addValue(List<Integer> l, int value) {
        l.add(value);
        return l;
    }


    @Test
    public void testA5Consolidate() {
        List<List<String>> conditions = MatrixBuilder.on("N,N,Y,N,Y,N,-,Y,-").dim(3, 3).build();
        ObservableList<ObservableList<String>>[] observables = new ObservableList[]{MatrixBuilder.observable(conditions)};
        for (int i = 0; i < conditions.size(); i++) {
            ObservableList<ObservableList<String>> tmp = MatrixFunctions.removeRowsAt(observables[0],i);
            ObservableList<ObservableList<String>> cur = MatrixFunctions.transpose(tmp);
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
                                observables[0] = MatrixFunctions.removeColumnsAt(observables[0], indices.get(r));
                            }
                        }
                    });
        }
        dumpTableItems("RESULT-1", observables[0]);
    }

    @Test
    public void testA5Design() {
        int expected = 0;
        List<String> rf = StringListBuilder.on("-,N,-").build();
        List<String> ri = StringListBuilder.on("N,N,Y").build();

        // counting dashes
        long dashCount = rf.stream().filter(s -> isDASH(s)).count();

        List<List<String>> result = MatrixBuilder.empty().dim(rf.size(), (int) dashCount).build();

        int countDashesSeen = 0;

        for (int i = 0; i < rf.size(); i++) {
            String l = rf.get(i);
            String r = ri.get(i);
            if (isDASH(l)) {
                String s = "??";
                if (countDashesSeen > 0) {
                    for (int j = 0; j < countDashesSeen; j++) {
                        result.get(i).add(DASH);
                    }
                    for (int j = 0; j < i; j++) {
                        result.get(j).add(ri.get(j));
                    }
                }
                switch (r) {
                    case YES:
                        s = NO;
                        break;
                    case NO:
                        s = YES;
                        break;
                    case DASH:
                        s = DASH;
                        break;
                    default:
                        ;
                }
                result.get(i).add(s);
                countDashesSeen++;
            } else {
                result.get(i).add(l);
            }
        }

        dumpTableItems("RESULT", result);
    }


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

    public static void dumpMap(String msg, Map<?,?> map) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        map.forEach((k,v) -> System.out.println("\t" + k + " -> " + v));
        System.out.println("<<<<<<<<<<\n");
    }

}