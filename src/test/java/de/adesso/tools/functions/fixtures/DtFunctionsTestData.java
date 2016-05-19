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

package de.adesso.tools.functions.fixtures;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import de.adesso.tools.Dump;
import de.adesso.tools.analysis.structure.DefaultStructuralAnalysis;
import de.adesso.tools.analysis.structure.Indicator;
import de.adesso.tools.analysis.structure.StructuralAnalysisResultEmitter;
import de.adesso.tools.common.builder.List2DBuilder;
import de.adesso.tools.functions.List2DFunctions;
import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple3;
import javafx.collections.FXCollections;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.functions.DtFunctions.permutations;
import static de.adesso.tools.functions.List2DFunctions.removeColumnsAt;
import static de.adesso.tools.functions.List2DFunctions.transpose;

/**
 * Test fixtures of the DtFunctionsTest's
 * Created by moehler ofList 02.03.2016.
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

        final List2DBuilder conditions = List2DBuilder.matrixOf(conditionsData).dim(condRows, cols);
        final List2DBuilder actions = List2DBuilder.matrixOf(actionsData).dim(actRows, cols);


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
        final List<List<String>> actions = List2DBuilder.matrixOf(actionsData).dim(actRows, cols).build();

        Dump.dumpTableItems("CONDITIONS-BEFORE-REMOVE", conditions);
        Dump.dumpTableItems("ACTIONS-BEFORE-REMOVE", actions);

        // TODO do some reference magic in the 2statements below!!
        toDelete.forEach(i -> List2DFunctions.removeColumnsAt(conditions, i));
        toDelete.forEach(i -> List2DFunctions.removeColumnsAt(actions, i));

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
