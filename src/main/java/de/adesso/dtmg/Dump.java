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

package de.adesso.dtmg;

import com.codepoetics.protonpack.Indexed;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.adesso.dtmg.analysis.structure.Indicator;
import de.adesso.dtmg.util.tuple.Tuple;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mmoehler ofList 27.03.16.
 */
public class Dump {
    public static <T> void dumpTableItems(String msg, List<List<T>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + String.valueOf(i)));
        System.out.println("<<<<<<<<<<\n");
    }


    public static <T> void dumpTableItems(String msg, ObservableList<ObservableList<T>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpTuple(String msg, Tuple tuple) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        Preconditions.checkNotNull(tuple, "Tuple is NULL!!").asList().forEach(k -> System.out.println("\t" + k));
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

    public static <T> void dumpSet1DItems(String msg, Set<T> set1D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        set1D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }


    public static <T> void indexed(String msg, Indexed<T> i) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
            System.out.println(String.format("<%d:%s>",i.getIndex(), String.valueOf(i.getValue())));
        System.out.println("<<<<<<<<<<\n");
    }

    public static <E> void arry2DItems(String msg, E[][] a) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        Arrays.stream(a).forEach(i -> System.out.println("\t" + Arrays.toString(i)));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpStructuralAnalysisResult(String msg, List<Indicator> r) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        Lists.partition(r,5).forEach(System.out::println);
        System.out.println("<<<<<<<<<<\n");
    }

    public static <T> void dumListWithIndexedValues(String msg, List<Indexed<T>> v) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        v.forEach(i -> System.out.println(String.format("(%02d) => %s", i.getIndex(), i.getValue())));
        System.out.println("<<<<<<<<<<\n");

    }

}
