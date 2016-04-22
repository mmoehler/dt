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

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.adesso.tools.Dump;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.functions.MatrixFunctions;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.functions.MatrixFunctions.removeColumnsAt;
import static de.adesso.tools.functions.MatrixFunctions.transpose;

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

    static class ConsolidateConditions implements Function<List<List<String>>, List<List<String>>> {
        @Override
        public List<List<String>> apply(List<List<String>> conditions) {
            List<Boolean> indicatorsComplete = rowsWithAllPossibleIndicators(conditions);
            List<List<String>> copy = MatrixFunctions.copy(conditions);
            List<List<String>> _copy[] = new List[]{copy};
            for (int currentRow = 0; currentRow < conditions.size(); currentRow++) {
                List<Integer> indicesOfDashedIndicators = determineIndicesOfDashedIndicators(_copy[0],currentRow);
                if (indicatorsComplete.get(currentRow)) {
                    List<List<String>> conditionRows = cleanupConditions(_copy[0], currentRow, indicesOfDashedIndicators);
                    List<List<String>> conditionColumns = transpose(conditionRows);
                    LinkedListMultimap<List<String>, Integer> duplicateRules = determineCountOfDuplicateRules(conditionColumns);

                    if (!duplicateRules.isEmpty()) {
                        Map<Boolean, List<Integer>> partitioned = groupDuplicatesAccordingToTheirIndices(duplicateRules, conditionColumns);
                        final int cr = currentRow;
                        partitioned.forEach((k, v) -> {
                            v.forEach(c -> {
                                if (k) {
                                    _copy[0].get(cr).set(c, "-");
                                } else {
                                    _copy[0] = removeColumnsAt(_copy[0], c);
                                }
                            });
                        });
                        updateRowsWithAllPossibleIndicators(currentRow, indicatorsComplete, _copy[0]);
                    }
                }
            }
            return(_copy[0]);
        }

        private List<List<String>> cleanupConditions(List<List<String>> original, int currentRow, List<Integer> indicesOfDashedIndicators) {
            List<List<String>> conditionRows = MatrixFunctions.removeRowsAt(original, currentRow);
            for (int ii : indicesOfDashedIndicators) {
                conditionRows = removeColumnsAt(conditionRows, ii);
            }
            return conditionRows;
        }

        private Map<Boolean, List<Integer>> groupDuplicatesAccordingToTheirIndices(LinkedListMultimap<List<String>, Integer> dupplicateRules, List<List<String>> step01Cols) {
            return step01Cols.stream().map(e -> {
                List<Integer> l = dupplicateRules.get(e);
                Map<Boolean, List<Integer>> partitions = l.stream().collect(Collectors.partitioningBy(k -> l.indexOf(k) == 0));
                return partitions;
            }).reduce(Collections.emptyMap(), (m1, m2) -> Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> {
                                List<Integer> v = new ArrayList<>();
                                if (!v.containsAll(e.getValue()))
                                    v.addAll(e.getValue());
                                Collections.sort(v, (l, r) -> r - l);
                                return v;
                            },
                            (a, b) -> {
                                List<Integer> merged = new ArrayList<>(a);
                                if (!a.containsAll(b))
                                    merged.addAll(b);
                                return merged;
                            })));
        }

        private LinkedListMultimap<List<String>, Integer> determineCountOfDuplicateRules(List<List<String>> step01Cols) {
            LinkedListMultimap<List<String>, Integer> counter = LinkedListMultimap.create();
            IntStream.range(0, step01Cols.size()).forEach(j -> counter.put(step01Cols.get(j), j));
            // remove all entries with values size is 1
            for (Iterator<Map.Entry<List<String>,Collection<Integer>>> it = counter.asMap().entrySet().iterator(); it.hasNext();)
                if(it.next().getValue().size()==1)
                    it.remove();
            return counter;
        }

        private List<Integer> determineIndicesOfDashedIndicators(List<List<String>> conditions, int row) {
            return IntStream.range(0, conditions.get(row).size())
                    .filter(i -> conditions.get(row).get(i).equals("-"))
                    .boxed()
                    .collect(Collectors.toList());
        }

        private List<Boolean> rowsWithAllPossibleIndicators(List<List<String>> conditions) {
            return IntStream.range(0, conditions.size())
                    .mapToObj(i -> (conditions.get(i).containsAll(POSSIBLE_INDICATORS)))
                    .collect(Collectors.toList());
        }

        private void updateRowsWithAllPossibleIndicators(int row, List<Boolean> completeness, List<List<String>> conditions) {
            completeness.set(row, conditions.get(row).containsAll(POSSIBLE_INDICATORS));
        }
    }


    static Function<List<List<String>>, List<List<Integer>>> indicesOfDuplicateActions() {
        return (t) -> {
            final ListMultimap<List<String>, Integer> tmp =
                    Multimaps.newListMultimap(new HashMap<List<String>, Collection<Integer>>(), () -> new LinkedList<Integer>());

            IntStream.range(0,t.size()).forEach(i -> tmp.put(t.get(i), i));
            tmp.asMap().entrySet().removeIf(entry -> entry.getValue().size()<2);

            return tmp.asMap().values().stream()
                    .map(o -> o.stream().collect(Collectors.toList()))
                    .collect(Collectors.toList());
        };
    }

    static <T> /*Multimap<Integer,Integer>*/ void indicesOfDupplicates(List<T> l) {
        final ListMultimap<Integer, Integer> result =
                Multimaps.newListMultimap(new HashMap<Integer, Collection<Integer>>(), () -> new LinkedList<Integer>());

        final ListMultimap<T, Integer> tmp =
                Multimaps.newListMultimap(new HashMap<T, Collection<Integer>>(), () -> new LinkedList<Integer>());

        IntStream.range(0,l.size()).forEach(i -> tmp.put(l.get(i), i));
        tmp.asMap().entrySet().removeIf(entry -> entry.getValue().size()<2);

        final Map<T, List<Indexed<T>>> map = tmp.asMap().entrySet().stream()
                .collect(Collectors
                        .toMap(e -> e.getKey(), e -> e.getValue().stream()
                                        .map(v -> Indexed.index(v, e.getKey())).collect(Collectors.toList())
                        ));


        Dump.dumpMap("INDICES", tmp.asMap());

        map.entrySet().forEach(e ->{
            for(Indexed<T> i : e.getValue()) {
                System.out.println(String.format("K: %s - V: %s", e.getKey(), ""+i.getIndex()+":"+i.getValue()));
            }
        });

        //

        //return result
    };



    @Test
    public void testGroupActions(){
        final List<List<String>> conditions = MatrixBuilder.on(
                         "X,X,X,X,X,X,-,"
                        +"X,-,X,X,-,-,X,"
                        +"-,-,-,-,X,-,-,"
                        +"X,X,X,X,X,X,X").dim(4, 7).build();

        final List<List<String>> transposed = transpose(conditions);

        final List<List<Integer>> collect = Stream.of(transposed).map(indicesOfDuplicateActions()).reduce(new LinkedList<>(), (a,b) -> {
            a.addAll(b);
            return a;
        });

        dumpTableItems("INDICES", collect);

        //indicesOfDupplicates(transposed);



        //final Stream<Indexed<List<String>>> transposeIndexed = StreamUtils.zipWithIndex(transposed.stream());


        /*
        final Map<List<String>, List<Integer>> map = transposed.stream().collect(Collectors.toMap(Function.identity(), p -> {
            List<Integer> indices = new ArrayList<>();
            indices.add(transposed.indexOf(p));
            return indices;
        }, (l, r) -> {
            l.addAll(r);
            return l;
        }));
        */

//        dumpMap("INDICES", map);
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

        // Explizite Präsenz der Bedingungsanzeiger
        // In jeder Zeile können jeweils alle Bedingungsanzeiger ermittelt werden.
        // Alle Bedingungszeilen werden gekennzeichnet.

        List<Boolean> indicatorsComplete = rowsWithAllPossibleIndicators(conditions);
        dumpList1DItems("INDICATOR-COMPLETENESS", indicatorsComplete);

        List<List<String>> copy = MatrixFunctions.copy(conditions);
        List<List<String>> _copy[] = new List[]{copy};

        for (int i = 0; i < conditions.size(); i++) {

            int ROW = i;
            System.out.println("ROW = " + ROW);
            dumpTableItems("INPUT", _copy[0]);

            List<Integer> indicesOfDashedIndicators = determineIndicesOfDashedIndicators(_copy[0],ROW);
            dumpList1DItems("INDICES-OF-DASHED-CONDITIONS", indicesOfDashedIndicators);

            // Spalten mit DASH werden nicht berücksichtigt!
            if (indicatorsComplete.get(ROW)) {

                List<List<String>> conditionRows = cleanupConditions(_copy[0], ROW, indicesOfDashedIndicators);

                dumpTableItems("INPUT-WITHOUT-ROW", conditionRows);

                List<List<String>> conditionColumns = transpose(conditionRows);

                dumpTableItems("INPUT-WITHOUT-ROW_TRANSPOSED", conditionColumns);

                LinkedListMultimap<List<String>, Integer> dupplicateRules = determineCountOfDuplicateRules(conditionColumns);


                dumpMap("COUNT OF DUPS", dupplicateRules.asMap());

                if(dupplicateRules.isEmpty())
                    continue;

                Map<Boolean, List<Integer>> partitioned = groupDuplicatesAccordingToTheirIndices(dupplicateRules, conditionColumns);

                dumpMap("PARTITIONED DUPS", partitioned);

                partitioned.forEach((k, v) -> {
                    v.forEach(c -> {
                        if (k) {
                            _copy[0].get(ROW).set(c, "-");
                        } else {
                            _copy[0] = removeColumnsAt(_copy[0], c);
                        }
                    });
                });

                controlRowsWithAllPossibleIndicators(ROW, indicatorsComplete, _copy[0]);

                dumpList1DItems("INDICATOR-COMPLETENESS", indicatorsComplete);

                dumpTableItems("RESULT LOOP", _copy[0]);
            }
        }

        dumpTableItems("---> RESULT", _copy[0]);
    }

    private List<List<String>> cleanupConditions(List<List<String>> original, int currentRow, List<Integer> indicesOfDashedIndicators) {
        List<List<String>> conditionRows = MatrixFunctions.removeRowsAt(original, currentRow);
        for (int ii : indicesOfDashedIndicators) {
            conditionRows = removeColumnsAt(conditionRows, ii);
        }
        return conditionRows;
    }

    private Map<Boolean, List<Integer>> groupDuplicatesAccordingToTheirIndices(LinkedListMultimap<List<String>, Integer> dupplicateRules, List<List<String>> step01Cols) {
        return step01Cols.stream().map(e -> {
                        List<Integer> l = dupplicateRules.get(e);
                        Map<Boolean, List<Integer>> partitions = l.stream().collect(Collectors.partitioningBy(k -> l.indexOf(k) == 0));
                        return partitions;
                    }).reduce(Collections.emptyMap(), (m1, m2) -> Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    e -> {
                                        List<Integer> v = new ArrayList<>();
                                        if (!v.containsAll(e.getValue()))
                                            v.addAll(e.getValue());
                                        Collections.sort(v, (l, r) -> r - l);
                                        return v;
                                    },
                                    (a, b) -> {
                                        List<Integer> merged = new ArrayList<>(a);
                                        if (!a.containsAll(b))
                                            merged.addAll(b);
                                        return merged;
                                    })));
    }

    private LinkedListMultimap<List<String>, Integer> determineCountOfDuplicateRules(List<List<String>> step01Cols) {
        LinkedListMultimap<List<String>, Integer> counter = LinkedListMultimap.create();
        IntStream.range(0, step01Cols.size()).forEach(j -> counter.put(step01Cols.get(j), j));
        // remove all entries with values size is 1
        for (Iterator<Map.Entry<List<String>,Collection<Integer>>> it = counter.asMap().entrySet().iterator(); it.hasNext();)
            if(it.next().getValue().size()==1)
                it.remove();
        return counter;
    }

    private List<Integer> determineIndicesOfDashedIndicators(List<List<String>> conditions, int row) {
        return IntStream.range(0, conditions.get(row).size())
                .filter(i -> conditions.get(row).get(i).equals("-"))
                .boxed()
                .collect(Collectors.toList());
    }

    private List<Boolean> rowsWithAllPossibleIndicators(List<List<String>> conditions) {
        return IntStream.range(0, conditions.size())
                .mapToObj(i -> (conditions.get(i).containsAll(POSSIBLE_INDICATORS)))
                .collect(Collectors.toList());
    }

    private void controlRowsWithAllPossibleIndicators(int row, List<Boolean> completeness, List<List<String>> conditions) {
        completeness.set(row, conditions.get(row).containsAll(POSSIBLE_INDICATORS));
    }
}
