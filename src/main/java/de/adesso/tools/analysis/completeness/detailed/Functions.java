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
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private static final BiFunction<List<String>, List<String>, List<List<String>>>[] ACTIONS = new BiFunction[]{A1, A2, A3, A4, A5};
    private static final Function<List<Tuple2<String, String>>, Integer>[] CONDITIONS = new Function[]{B1, B2, B3, B4};
    private static final List<List<String>> INTERNAL = MatrixBuilder.on("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();
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
    private static Function<String, Integer> maskMatrixMapper = s -> (isDASH(s)) ? 0 : 1;
    private static final List<? extends List<Integer>> M = transpose(makeMaskMatrix(INTERNAL));
    private static Function<String, Integer> decisionMatrixMapper = s -> (isYES(s)) ? 1 : 0;
    private static final List<? extends List<Integer>> D = transpose(makeDecisionMatrix(INTERNAL));

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
        return new ConditionsConsolidateOperator();
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


class ConditionsConsolidateOperator implements Function<List<List<String>>, List<List<String>>> {
    public ConditionsConsolidateOperator() {
    }

    @Override
    public List<List<String>> apply(List<List<String>> conditions) {

        List<List<String>>[] observables = new List[]{copy(conditions)};
        for (int i = 0; i < conditions.size(); i++) {
            List<List<String>> tmp = removeRowsAt(observables[0], i);
            List<List<String>> cur = transpose(tmp);
            Map<List<String>, List<Integer>> map = new HashMap<>();
            IntStream.range(0, cur.size()).forEach(j -> map
                    .compute(cur.get(j), (k, v) -> (v == null) ? newWithValue(j) : addValue(v, j)));
            final int row = i;
            map.entrySet().stream().filter(e -> e.getValue().size() > 1)
                    .forEach(f -> {
                        List<Integer> indices = f.getValue();
                        Collections.sort(indices, (a, b) -> b - a);
                        for (int r = 0; r < indices.size(); r++) {
                            if (r == indices.size() - 1) {
                                observables[0].get(row).set(indices.get(r), DASH);
                            } else {
                                observables[0] = removeColumnsAt(observables[0], indices.get(r));
                            }
                        }
                    });
        }
        return observables[0];

    }

    private List<Integer> newWithValue(int value) {
        List<Integer> result = new ArrayList<>();
        result.add(value);
        return result;
    }

    private List<Integer> addValue(List<Integer> l, int value) {
        l.add(value);
        return l;
    }
}