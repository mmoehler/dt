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

package de.adesso.dtmg.export.java.ifthen;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import de.adesso.dtmg.Dump;
import de.adesso.dtmg.common.builder.List2DBuilder;
import de.adesso.dtmg.functions.List2DFunctions;
import de.adesso.dtmg.util.RandomDefinitions;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by moehler on 08.06.2016.
 */
public class EmitterTest {



    static class IndexedToStringDecorator{
        final Indexed indexed;

        public static IndexedToStringDecorator decorate(Indexed<?> indexed) {
            return new IndexedToStringDecorator(indexed);
        }

        private IndexedToStringDecorator(Indexed<?> indexed) {
            this.indexed = indexed;
        }

        @Override
        public String toString() {
            return String.format("([%02d] => %s)", indexed.getIndex(), indexed.getValue());
        }
    }


    static <T> void dumListWithIndexedValues(String msg, List<Indexed<T>> v) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        v.forEach(i -> System.out.println(String.format("(%02d) => %s", i.getIndex(), i.getValue())));
        System.out.println("<<<<<<<<<<\n");

    }

    private static <T> List<List<T>> splitBySeparator(List<T> list, Predicate<? super T> predicate) {
        final List<List<T>> finalList = new ArrayList<>();
        int fromIndex = 0;
        int toIndex = 0;
        for (T elem : list) {
            if (predicate.test(elem)) {
                finalList.add(list.subList(fromIndex, toIndex));
                fromIndex = toIndex + 1;
            }
            toIndex++;
        }
        if (fromIndex != toIndex) {
            finalList.add(list.subList(fromIndex, toIndex));
        }
        return finalList;
    }

    @Test
    public void testEmit() throws Exception {
        RandomDefinitions.Builder builder = RandomDefinitions.newBuilder().cols(5).rows(3).indicators(() -> new String[]{"Y", "N", "-"}).transpose();
        List<List<String>> definitions = builder.build().get();

        Dump.dumpTableItems("ORIGINAL DT", List2DFunctions.transpose(definitions));

        List<List<C>> dt = definitions.stream().map(l -> l.stream().map(C::from).collect(Collectors.toList())).collect(Collectors.toList());
        Dump.dumpTableItems("RANDOM DT", dt);

        final List<Indexed<List<C>>> collect = StreamUtils.zipWithIndex(dt.stream()).collect(Collectors.toList());

        final List<Indexed<List<C>>> collect1 = collect.stream().sorted((l, r) -> l.getValue().get(0).code - r.getValue().get(0).code).collect(Collectors.toList());

        collect1.forEach(e -> System.out.println(String.format("(%02d) -> %s", e.getIndex(), e.getValue())));
    }


    @Test
    public void testSplit() throws Exception {
        final List<List<String>> dt = List2DBuilder.matrixOf("-,Y,Y,N,N,Y,Y,Y,Y,Y,N,N").dim(3, 4).build();


        final List<String> list = dt.get(0);
        final List<Indexed<String>> indexeds = StreamUtils.zipWithIndex(list.stream()).collect(Collectors.toList());

        final List<Indexed<String>> theTrues = indexeds.stream()
                .filter(i -> Pattern.matches("Y|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        final List<Indexed<String>> theOthers = indexeds.stream()
                .filter(i -> Pattern.matches("N|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        dumListWithIndexedValues("LEFT", theTrues);
        dumListWithIndexedValues("RIGHT", theOthers);


    }

    @Test
    public void testCreateDecisionTree() throws Exception {
        final List<List<String>> dt = List2DBuilder.matrixOf("-,Y,Y,N,N,Y,Y,Y,Y,Y,N,N").dim(3, 4).build();

        Dump.dumpTableItems("DT", dt);

        final List<String> list = dt.get(0);
        final List<Indexed<String>> indexeds = StreamUtils.zipWithIndex(list.stream()).collect(Collectors.toList());
        final DtNode top = DtNode.newBuilder().index(0).data(indexeds).build();
        createDecisionTree(dt,top);
        System.out.println("top = " + top);
    }




    enum C implements Identifiable {
        Y(0, "Y"), N(1, "N"), H(2, "-");
        final int code;
        final String id;

        C(int code, String id) {
            this.code = code;
            this.id = id;
        }

        static C from(String s) {
            final Optional<C> i1 = Arrays.stream(values()).filter((i) -> s.equals(i.id)).findFirst();
            return i1.orElseThrow(() -> new IllegalStateException(s + "??"));
        }

        public int code() {
            return code;
        }

        public String id() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }

    }

    enum A implements Identifiable {
        X("X"), H("-");
        final String id;

        A(String id) {
            this.id = id;
        }

        static A from(String s) {
            final Optional<A> i1 = Arrays.stream(values()).filter((i) -> s.equals(i.id)).findFirst();
            return i1.orElseThrow(() -> new IllegalStateException(s + "??"));
        }

        public String id() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }

    }


    interface Identifiable {
        String id();
    }

    static void createDecisionTree(List<List<String>> dt, DtNode top) {

        System.out.println(String.format("(%02d) => %s",
                top.index,
                top.data.stream()
                        .map(i -> IndexedToStringDecorator.decorate(i).toString())
                        .collect(Collectors.toList())));

        if (top.index >= dt.size()-1) return;

        final List<Indexed<String>> theTrues = top.data.stream()
                .filter(i -> Pattern.matches("Y|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(top.index+1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        DtNode ifTrue = DtNode.newBuilder().data(theTrues).index(top.index + 1).build();
        top.yes = ifTrue;
        createDecisionTree(dt, ifTrue);

        final List<Indexed<String>> theOthers = top.data.stream()
                .filter(i -> Pattern.matches("N|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(top.index+1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        DtNode ifFalse = DtNode.newBuilder().data(theOthers).index(top.index + 1).build();
        top.no = ifFalse;
        createDecisionTree(dt, ifFalse);

    }


}