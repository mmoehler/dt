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

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import de.adesso.tools.Dump;
import de.adesso.tools.analysis.structure.DefaultStructuralAnalysis;
import de.adesso.tools.analysis.structure.Indicator;
import de.adesso.tools.analysis.structure.Operators;
import de.adesso.tools.analysis.structure.StructuralAnalysisResultEmitter;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple3;
import javafx.collections.FXCollections;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.functions.DtFunctions.permutations;
import static de.adesso.tools.functions.MatrixFunctions.removeColumnsAt;
import static de.adesso.tools.functions.MatrixFunctions.transpose;

/**
 * Test fixtures of the DtFunctionsTest's
 * Created by moehler on 02.03.2016.
 */
public class DtFunctionsTestData {

    public final static String ALPHAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public final static String ALPHA_NUMERICS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public final static String CONDITIONS = "YN-";
    public final static String ACTIONS = "X-";
    public final static List<String> IND = new ArrayList<>(Arrays.asList("Y", "N"));
    public static Supplier<String> RANDOM_NUMBER_STRING_000_999 =
            () -> ThreadLocalRandom.current().ints(3, 0, 9)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining());
    public static Supplier<String> RANDOM_ALPHA_5 =
            () -> ThreadLocalRandom.current().ints(5, 0, ALPHAS.length())
                    .mapToObj(i -> Character.toString(ALPHAS.charAt(i)))
                    .reduce("", (a, b) -> a + b);
    public static Supplier<String> RANDOM_ALPHA_NUMERIC_5 =
            () -> ThreadLocalRandom.current().ints(5, 0, ALPHA_NUMERICS.length())
                    .mapToObj(i -> Character.toString(ALPHA_NUMERICS.charAt(i)))
                    .reduce("", (a, b) -> a + b);
    public static Supplier<String> RANDOM_CONDITIONS =
            () -> ThreadLocalRandom.current().ints(5, 0, CONDITIONS.length())
                    .mapToObj(i -> Character.toString(CONDITIONS.charAt(i)))
                    .reduce("", (a, b) -> a + ("".equals(a) ? "" : ",") + b);

    public static String[] rndDefinitions(int inclRows, int inclCols, String possibilities) {
        final String[] strings = ThreadLocalRandom.current().ints((inclRows * inclCols), 0, possibilities.length())
                .mapToObj(i -> Character.toString(possibilities.charAt(i)))
                .toArray(size -> new String[size]);
        return strings;
    }

    private static String esc(String s) {
        return "\"" + s + "\"";
    }

    private static int rndInt(int inclFrom, int exclTo) {
        return ThreadLocalRandom.current().ints(1, inclFrom, exclTo).boxed().collect(MoreCollectors.toSingleObject());
    }

    private static List<Integer> rndInts(int inclFrom, int exclTo, int count) {
        return ThreadLocalRandom.current().ints(count, inclFrom, exclTo).boxed().collect(Collectors.toList());
    }

    public static Tuple2<List<List<String>>, List<List<String>>> rndDT() {

        final int condRows = rndInt(2, 6);
        final int actRows = rndInt(2, 6);
        final int cols = (int) Math.pow(2, condRows);

        final String[] conditionsData = rndDefinitions(condRows, cols, CONDITIONS);
        final String[] actionsData = rndDefinitions(actRows, cols, ACTIONS);

        final MatrixBuilder conditions = MatrixBuilder.on(conditionsData).dim(condRows, cols);
        final MatrixBuilder actions = MatrixBuilder.on(actionsData).dim(actRows, cols);


        final Tuple2<List<List<String>>, List<List<String>>> dt = Tuple.of(conditions.build(), actions.build());

        return dt;
    }

    public static Tuple2<List<List<String>>, List<List<String>>> rndDT2() {

        final int condRows = rndInt(2, 6);
        final int actRows = rndInt(2, 6);
        final int cols = (int) Math.pow(2, condRows);

        List<Integer> toDelete = rndInts(0, cols, (int) cols / 2);
        Collections.sort(toDelete, (a, b) -> b - a);

        System.err.println("toDelete = " + toDelete);

        final List<List<String>> conditions = fullExpandConditions(IND, condRows);
        final String[] actionsData = rndDefinitions(actRows, cols, ACTIONS);
        final List<List<String>> actions = MatrixBuilder.on(actionsData).dim(actRows, cols).build();

        Dump.dumpTableItems("CONDITIONS-BEFORE-REMOVE", conditions);
        Dump.dumpTableItems("ACTIONS-BEFORE-REMOVE", actions);

        // TODO do some reference magic in the 2statements below!!
        toDelete.forEach(i -> MatrixFunctions.removeColumnsAt(conditions, i));
        toDelete.forEach(i -> MatrixFunctions.removeColumnsAt(actions, i));

        final Tuple2<List<List<String>>, List<List<String>>> dt = Tuple.of(conditions, actions);

        Dump.dumpTableItems("CONDITIONS-AFTER-REMOVE", conditions);
        Dump.dumpTableItems("ACTIONS-AFTER-REMOVE", actions);

        return dt;
    }

    public static List<List<String>> fullExpandConditions(final List<String> indicators, int rows) {
        final List<List<String>> retList = FXCollections.observableArrayList();
        final List<List<String>> rawIndicators = IntStream.range(0, rows).mapToObj(i -> indicators).collect(Collectors.toList());
        final List<List<String>> permutations = permutations(rawIndicators);
        final List<List<String>> transposed = transpose(permutations);
        transposed.forEach(l -> retList.add(FXCollections.observableArrayList(l)));
        return retList;
    }


    public static Tuple2<List<List<String>>, List<List<String>>> rmDuplicateRules(Tuple2<List<List<String>>, List<List<String>>> dt) {
        List<List<String>> conditions = new ArrayList<>(dt._1());
        List<List<String>> actions = new ArrayList<>(dt._2());

        Set<Integer> condDups = indicesOfAllDuplicates(conditions);
        Set<Integer> actDups = indicesOfAllDuplicates(actions);

        Sets.intersection(condDups,actDups).forEach(i -> {
            removeColumnsAt(conditions,i);
            removeColumnsAt(actions,i);
        });

        return Tuple.of(conditions,actions);
    }

    static class P {
        Integer i;
        String s;

        public P(Integer i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            P p = (P) o;

            if (i != null ? !i.equals(p.i) : p.i != null) return false;
            return s != null ? s.equals(p.s) : p.s == null;

        }

        @Override
        public int hashCode() {
            int result = i != null ? i.hashCode() : 0;
            result = 31 * result + (s != null ? s.hashCode() : 0);
            return result;
        }
    }

    public static void main(String argsd[]) {
        P p0 = new P(null,null);
        P p1 = new P(null,null);
        final boolean b = p0.equals(p1);
        System.out.println("b = " + b);
    }

    public static void main1(String argsd[]) {

        final int cols = 8;
        final int crows = 2;

        final List<List<String>> conditions = MatrixBuilder.on(
                        "a,b,a,c,a,b,a,e," +
                        "a,b,a,c,a,b,a,e"
        ).dim(crows, cols).transposed().build();

        Dump.dumpTableItems("CONDITIONS (TRANSPOSED)", conditions);

        int sidx = 0;
        int ofs = sidx + 1;
        int eidx = conditions.size();

        final Set<Integer> indices = indicesOf(conditions.subList(ofs, eidx), conditions.get(sidx), ofs);

        Dump.dumpSet1DItems("INDICES OF [Y,Y]", indices);


        final Set<Integer> indicesOfAllDuplicates = indicesOfAllDuplicates(conditions);

        Dump.dumpSet1DItems("INDICES OF ALL DUPS", indicesOfAllDuplicates);

    }


    static <T> Set<Integer> indicesOfAllDuplicates(List<T> l) {
        final int sz = l.size() - 1;
        return IntStream.range(0, sz)
                .mapToObj(idx -> indicesOf(l.subList(idx + 1, sz), l.get(idx), idx + 1))
                .reduce(new TreeSet<Integer>((x,y)-> y - x), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
    }

    static <T> Set<Integer> indicesOf(List<T> l, T t, int offset) {
        return IntStream.range(0,l.size())
                .filter(i -> (l.get(i).equals(t)))
                .mapToObj(i -> (i + offset))
                .collect(Collectors.toSet());
    }


    public static void main66(String argsd[]) {
        final String separator = "---------------------------------------------";
        for (int i = 0; i < 5; i++) {
            Tuple2<List<List<String>>, List<List<String>>> dt = rndDT2();
            Dump.dumpTableItems(String.format("CONDITIONS-%02d", i), dt._1());
            Dump.dumpTableItems(String.format("ACTIONS-%02d", i), dt._2());

            dt = rmDuplicateRules(dt);

            Dump.dumpTableItems(String.format("CONDITIONS-%02d WITHOUT DUPS", i), dt._1());
            Dump.dumpTableItems(String.format("ACTIONS-%02d WITHOUT DUPS", i), dt._2());


            if (!doStructuralAnalysis(dt)) {
                System.out.println(">>>>>>>>>> NO CONSOLIDATION POSSIBLE! <<<<<<<<<<\n");
                System.out.println(separator);
                continue;
            }

            //--

            List<List<String>> conditions = dt._1();
            List<List<String>> actions = dt._2();

            Tuple2<List<List<String>>, List<List<String>>> consolidated = Stream.of(Tuple.of(conditions, actions))
                    .map(Operators.consolidateRules())
                    .collect(MoreCollectors.toSingleObject());

            List<List<String>> cconditions = consolidated._1().isEmpty() ? conditions : consolidated._1();
            List<List<String>> cactions = consolidated._2().isEmpty() ? actions : consolidated._2();

            Dump.dumpTableItems(String.format("CONSOLIDATED CONDITIONS-%02d", i), cconditions);
            Dump.dumpTableItems(String.format("CONSOLIDATED ACTIONS-%02d", i), cactions);

            doStructuralAnalysis(consolidated);

            System.out.println(separator);
        }

    }

    private static boolean doStructuralAnalysis(Tuple2<List<List<String>>, List<List<String>>> dt) {
        DefaultStructuralAnalysis analysis = null;
        List<Indicator> result;
        try {
            analysis = new DefaultStructuralAnalysis();
            analysis.postConstruct();
            result = analysis.apply(dt._1(), dt._2());
        } finally {
            if (null != analysis) {
                analysis.preDestroy();
            }
        }
        final Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>> status = new StructuralAnalysisResultEmitter().apply(result, dt._1().get(0).size());
        final String s = status._1();
        System.out.println(s);
        return !status._2().isEmpty();
    }


    public static ListOfIndicatorSuppliersBuilder listOfIndicatorSupliersBuilder() {
        return ListOfIndicatorSuppliersBuilder.newBuilder();
    }

    public static ConditionDeclTableViewBuilder conditionDeclTableViewBuilder() {
        return new ConditionDeclTableViewBuilder();
    }

    public static ActionDeclTableViewBuilder actionDeclTableViewBuilder() {
        return new ActionDeclTableViewBuilder();
    }


    public static ConditionDeclTableViewModelListBuilder conditionDeclTableViewModelListBuilder() {
        return new ConditionDeclTableViewModelListBuilder();
    }

    public static ActionDeclTableViewModelListBuilder actionDeclTableViewModelListBuilder() {
        return new ActionDeclTableViewModelListBuilder();
    }

    public static DefinitionsTableViewBuilder definitionsTableViewBuilder() {
        return new DefinitionsTableViewBuilder();
    }
}
