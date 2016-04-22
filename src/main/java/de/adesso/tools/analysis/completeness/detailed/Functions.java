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
import com.google.common.collect.LinkedListMultimap;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.functions.MatrixFunctions;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.analysis.completeness.detailed.Actions.*;
import static de.adesso.tools.analysis.completeness.detailed.Conditions.*;
import static de.adesso.tools.analysis.completeness.detailed.Functions.columnDifference;
import static de.adesso.tools.analysis.completeness.detailed.Functions.consolidate;
import static de.adesso.tools.common.Reserved.*;
import static de.adesso.tools.functions.MatrixFunctions.*;
import static java.util.stream.Collectors.toList;

/**
 * Created by mmoehler on 20.03.16.
 */
public class Functions {
    // @formatter::off
    private static final BiFunction<List<String>, List<String>, List<List<String>>>[] ACTIONS = new BiFunction[]{A1, A2, A3, A4, A5};
    private static final Function<List<Tuple2<String, String>>, Integer>[] CONDITIONS = new Function[]{B1, B2, B3, B4};
    private static final List<List<String>> INTERNAL = MatrixBuilder.on("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();

    private static Function<String, Integer> maskMatrixMapper = s -> (isDASH(s)) ? 0 : 1;
    private static final List<? extends List<Integer>> M = transpose(makeMaskMatrix(INTERNAL));
    private static Function<String, Integer> decisionMatrixMapper = s -> (isYES(s)) ? 1 : 0;
    private static final List<? extends List<Integer>> D = transpose(makeDecisionMatrix(INTERNAL));
    // @formatter::on



    public static BiFunction<List<String>, List<String>, List<List<String>>> columnDifference = (left, right) -> {


        List<Tuple2<String, String>> prototype = StreamUtils
                .zip(left.stream(), right.stream(), (x, y) -> Tuple.of(x, y))
                .collect(toList());

        final List<Integer> mask = Arrays.stream(CONDITIONS)
                .map(c -> c.apply(prototype))
                .collect(Collectors.toList());

        List<List<Integer>> multiplied = M.stream().map(m -> logicalAnd(m, mask)).collect(toList());

        List<Integer> indices = StreamUtils
                .zip(multiplied.stream(), StreamUtils.zipWithIndex(D.stream()), (aa, bb) -> {
                    if (aa.equals(bb.getValue())) {
                        return bb.getIndex();
                    }
                    return -1;
                })
                // .peek(xyz -> System.out.println(xyz))
                .map(qq -> qq.intValue()).filter(vv -> vv >= 0).collect(Collectors.toList());

        //dumpList1DItems("IDX",indices);

        if (indices.size() != 1) {
            throw new IllegalStateException("Used DT is ambigous!");
        }

        // perform the action at the determined index and ...

        List<List<String>> applied = ACTIONS[indices.get(0).intValue()].apply(left, right);

        return transpose(applied);

    };

    public static List<List<Integer>> makeMaskMatrix(List<List<String>> conditions) {
        List<List<Integer>> M = conditions.stream().map(a -> a.stream().map(maskMatrixMapper).collect(toList())).collect(toList());
        return M;
    }

    public static List<List<Integer>> makeDecisionMatrix(List<List<String>> conditions) {
        List<List<Integer>> M = conditions.stream().map(a -> a.stream().map(decisionMatrixMapper).collect(toList())).collect(toList());
        return M;
    }

    public static Function<List<List<String>>, List<List<String>>> difference() {
        return new RulesDifferenceOperator();
    }

    public static Function<List<List<String>>, List<List<String>>> consolidate() {
        return new ConditionsConsolidateOperator0();
    }

    private static List<Integer> logicalAnd(List<Integer> a, List<Integer> b) {
        List<Integer> condition = StreamUtils.zip(a.stream(), b.stream(), (x, y) -> x * y)
                .collect(toList());
        return condition;
    }

    // -- Pactories ----------------------------------------------------------------------------------------------------


}

class RulesDifferenceOperator implements Function<List<List<String>>, List<List<String>>> {

    private final BinaryOperator<List<List<String>>> merge = (l, r) -> {
        r.forEach(l::add);
        return l;
    };

    public RulesDifferenceOperator() {
    }

    @Override
    public List<List<String>> apply(List<List<String>> conditions) {
        //final List<List<String>> rights = MatrixBuilder.on("Y,Y,N,N,Y,Y,Y,N,N,-,N,Y").dim(3,4).transposed().build();
        List<List<String>> rights = transpose(conditions);
        String s[] = new String[conditions.size()];
        Arrays.fill(s, "-");
        String joined = String.join(",", s);
        List<List<String>> tmp = MatrixBuilder.on(joined).dim(rights.size(), 1).transposed().build();
        final List<List<String>>[] lefts = new List[]{tmp};
        for (List<String> right : rights) {
            lefts[0] = lefts[0].stream()
                    .map(l -> columnDifference.apply(l, right))
                    .reduce(new ArrayList<>(), merge);
            lefts[0] = transpose(consolidate().apply(transpose(lefts[0])));
        }
        return transpose(lefts[0]);
    }
}

class ConditionsConsolidateOperator0 implements Function<List<List<String>>, List<List<String>>> {
    public static final List<String> POSSIBLE_INDICATORS = Arrays.asList("Y", "N");
    public ConditionsConsolidateOperator0() {
    }

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

