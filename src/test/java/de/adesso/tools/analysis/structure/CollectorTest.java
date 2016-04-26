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

package de.adesso.tools.analysis.structure;

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.adesso.tools.Dump;
import de.adesso.tools.analysis.completeness.detailed.Functions;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.functions.MatrixFunctions.*;

/**
 * Created by moehler on 31.03.2016.
 */
public class CollectorTest {

    public static final List<String> POSSIBLE_INDICATORS = Arrays.asList("Y", "N");

    private static void dumpResult(List<Indicator> result) {
        Iterator<Indicator> indics = result.iterator();
        System.out.println("                           1 2 3 4");
        for (int i = 0; i < 3; i++) {
            System.out.print(String.format("STRUCTURE ANALYSIS RULE %2s ", i + 1));
            for (int j = 0; j < 4; j++) {
                if (j > i) {
                    System.out.print(indics.next().getCode() + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------");
        System.out.println("R   Redundancy");
        System.out.println("C   Contradiction");
        System.out.println("<   Inclusion");
        System.out.println(">   Inclusion");
        System.out.println("-   Exclusion");
        System.out.println("X   Clash");
        System.out.println("*   Compression Note");
        System.out.println("-----------------------------------------------");

    }

    @Test
    public void comparisonCollectorAlgoTest() {

        // -- Condition comparison ---------------------------------

        final List<List<String>> inConditions = MatrixBuilder.on("Y,Y,Y,-,-,N,N,N,-,-,-,N,Y,Y,N,N").dim(4, 4).transposed().build();

        Dump.dumpTableItems("conditions = ", inConditions);

        final List<List<Indicator>> outConditions = new ArrayList<>();

        for (int i = 0; i < inConditions.size() - 1; i++) {
            for (int j = 1; j < inConditions.size(); j++) {
                if (j > i) {
                    final Stream<Indicator> leftStream = inConditions.get(i).stream().map(a -> Indicators.lookup(a));
                    final Stream<Indicator> rightStream = inConditions.get(j).stream().map(a -> Indicators.lookup(a));

                    final List<Indicator> collected = StreamUtils.zip(leftStream, rightStream, Operators.conditionComparison()).collect(Collectors.toList());
                    outConditions.add(collected);
                }
            }
        }

        Dump.dumpTableItems("collected = ", transpose(outConditions));

        // -- Action comparison ---------------------------------

        final List<List<String>> inActions = MatrixBuilder.on("-,-,-,-,X,X,X,-,X,-,-,X,-,X,X,-").dim(4, 4).transposed().build();
        final List<List<Indicator>> outActions = new ArrayList<>();
        for (int i = 0; i < inActions.size() - 1; i++) {
            for (int j = 1; j < inActions.size(); j++) {
                if (j > i) {
                    final Stream<Indicator> leftStream = inActions.get(i).stream().map(Indicators::lookup);
                    final Stream<Indicator> rightStream = inActions.get(j).stream().map(Indicators::lookup);

                    final List<Indicator> collected = StreamUtils.zip(leftStream, rightStream, Operators.actionComparison()).collect(Collectors.toList());
                    outActions.add(collected);
                }
            }
        }


        Dump.dumpTableItems("collected = ", transpose(outActions));


        // first for the last comparison result do the reduction

        final Optional<Indicator> reduced = outConditions.get(outConditions.size() - 1).stream().reduce(Combiners.conditionComparisonResult());

        if (reduced.isPresent()) {
            System.out.println("reduced.get().getCode() = " + reduced.get().getCode());
        } else {
            System.out.println("reduced.get().getCode() = NULL");
        }

        // now for each result do the reduction

        final List<Indicator> reducedConditionIndicators = outConditions.stream()
                .map(c -> c.stream()
                        .reduce(Combiners.conditionComparisonResult()))
                .map(r -> r.get())
                .collect(Collectors.toList());

        Dump.dumpList1DItems("001", reducedConditionIndicators);

        // the same procedure for the actions

        final List<Indicator> reducedActionIndicators = outActions.stream()
                .map(c -> c.stream()
                        .reduce(Combiners.actionComparisonResult()))
                .map(r -> r.get())
                .collect(Collectors.toList());

        Dump.dumpList1DItems("002", reducedActionIndicators);


        // last zip both resultstogether -> reduce the data to the final per rule result

        final List<Indicator> result = StreamUtils
                .zip(reducedActionIndicators.stream(), reducedConditionIndicators.stream(), Accumulators.combinationResult())
                .collect(Collectors.toList());

        Dump.dumpList1DItems("RESULT", result);
        dumpResult(result);
    }

    @Test
    public void comparisonCollectorTest() {
        final List<List<String>> inConditions = MatrixBuilder
                .on("Y,Y,Y,-,-,N,N,N,-,-,-,N,Y,Y,N,N")
                .dim(4, 4)
                .build();

        final List<List<String>> inActions = MatrixBuilder
                .on("-,-,-,-,X,X,X,-,X,-,-,X,-,X,X,-")
                .dim(4, 4)
                .build();

        //final List<Indicator> result = DefaultStructuralAnalysis.INSTANCE.apply(inConditions, inActions);

        //dumpResult(result);

    }

    @Test
    public void consolidateTest() {
        final ObservableList<ObservableList<String>> _conditions = MatrixBuilder.observable(MatrixBuilder.on(
                "Y,Y,Y,Y,N,N,N,N,"
                        + "Y,Y,N,N,Y,Y,N,N,"
                        + "Y,N,Y,N,Y,N,Y,N,").dim(3, 8).build());

        final List<List<String>> conditions = MatrixBuilder.on(
                "Y,Y,N,N,"
                        + "Y,N,Y,N").dim(2, 4).build();

        final List<List<String>> __conditions = MatrixBuilder.on(
                "-,-,"
                        + "Y,N").dim(2, 2).build();

        List<List<String>>[] R = new List[]{new ArrayList(conditions)};

        IntStream.range(0, R[0].size()).forEach(row -> {

            List<List<String>> copy = new ArrayList<>(R[0]);

            copy.remove(row);

            List<List<String>> C = transpose(copy);

            dumpTableItems(String.format("Prepared for %d. loop", row), C);


            List<Integer> D = new ArrayList<>();
            List<Integer> U = new ArrayList<>();
            Multimap<List<String>, Integer> _M = Multimaps.newListMultimap(new HashMap<>(), LinkedList::new);
            IntStream.range(0, C.size()).forEach(l -> {
                if (_M.containsKey(C.get(l))) {
                    _M.get(C.get(l)).add(l);
                    D.add(l);
                } else {
                    _M.put(C.get(l), l);
                    U.add(l);
                }
            });
            LinkedListMultimap<List<String>, Integer> M = LinkedListMultimap.create(_M);

            System.out.println("M = " + M);
            System.out.println("U = " + U);
            Collections.sort(D, (a, b) -> (-1));
            System.out.println("D = " + D);

//            final List<List<String>> R[] = new List[]{conditions};
            U.forEach(i -> conditions.get(row).set(i, "-"));
            D.forEach(i -> {
                R[0] = removeColumnsAt(R[0], i);
            });

            //List<List<String>> consolidated = Functions.consolidate().apply(adapt(conditions));

            dumpTableItems("Result", R[0]);
        });


    }

    static Tuple2<List<Integer>, List<Integer>> split(Collection<Integer> data, BiPredicate<Collection<Integer>, Integer> rule) {
        Tuple2<List<Integer>, List<Integer>> ret = Tuple.of(new ArrayList<>(), new ArrayList<>());
        data.forEach(i -> {
            ((rule.test(data, i)) ? (ret._1()) : (ret._2())).add(i);
        });
        return ret;


    }


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
    public void testGroupActions(){
        final List<List<String>> actions = MatrixBuilder.on(
                         "-,-,X,X,X,-,-,-,-,"
                        +"X,X,X,-,-,-,-,-,-,"
                        +"X,-,X,X,X,X,X,X,X").dim(3, 9).build();

        final List<List<String>> conditions = MatrixBuilder.on(
                         "Y,Y,Y,Y,Y,N,N,N,N,"
                        +"Y,Y,N,-,-,-,-,-,-,"
                        +"Y,N,-,Y,N,Y,Y,N,N,"
                        +"Y,Y,Y,N,N,Y,N,Y,N").dim(4, 9).build();


        Dump.dumpTableItems("OLD CODITIONS", conditions);
        Dump.dumpTableItems("OLD ACTIONS", actions);

        Tuple2<List<List<String>>, List<List<String>>> consolidated = Stream.of(Tuple.of(conditions, actions))
                .map(Operators.consolidateRules())
                .collect(MoreCollectors.toSingleObject());

        Dump.dumpTableItems("NEW CODITIONS", consolidated._1());
        Dump.dumpTableItems("NEW ACTIONS", consolidated._2());


    }

    @Test
    public void testGroupActions0(){
        final List<List<String>> actions = MatrixBuilder.on(
                "X,X,X,X,X,X,-," +
                        "X,-,X,X,-,-,X," +
                        "-,-,-,-,X,-,-," +
                        "X,X,X,X,X,X,X").dim(4, 7).build();

        final List<List<String>> conditions = MatrixBuilder.on(
                "Y,Y,Y,Y,N,N,N," +
                        "Y,Y,N,N,Y,Y,N," +
                        "Y,N,Y,N,Y,N,Y"
        ).dim(3, 7).build();


        Dump.dumpTableItems("OLD CODITIONS", conditions);
        Dump.dumpTableItems("OLD ACTIONS", actions);

        Tuple2<List<List<String>>, List<List<String>>> consolidated = Stream.of(Tuple.of(conditions, actions))
                .map(Operators.consolidateRules())
                .collect(MoreCollectors.toSingleObject());

        Dump.dumpTableItems("NEW CODITIONS", consolidated._1());
        Dump.dumpTableItems("NEW ACTIONS", consolidated._2());
    }


    @Test
    public void testConsolidateStrunz() {
/*
        final List<List<String>> conditions = MatrixBuilder.on(
                 "Y,Y,Y,Y,Y,Y,N,"
                +"Y,Y,Y,Y,N,N,Y,"
                +"Y,Y,N,N,Y,N,N,"
                +"Y,N,Y,N,Y,Y,Y").dim(4, 7).build();
*/

        final List<List<String>> conditions = MatrixBuilder.on(
                          "Y,Y,Y,"
                        + "Y,N,N,"
                        + "Y,Y,N").dim(3, 3).build();

        Dump.dumpTableItems("OLD CODITIONS", conditions);

        final List<List<String>> consolidated = Stream.of(conditions)
                .map(Functions.consolidate())
                .collect(MoreCollectors.toSingleObject());

        Dump.dumpTableItems("NEW CODITIONS", consolidated);
    }

}
