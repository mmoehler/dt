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

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.adesso.tools.analysis.completeness.detailed.Actions.*;
import static de.adesso.tools.analysis.completeness.detailed.Conditions.*;
import static de.adesso.tools.common.Reserved.isDASH;
import static de.adesso.tools.common.Reserved.isYES;
import static de.adesso.tools.functions.List2DFunctions.transpose;
import static java.util.stream.Collectors.toList;

/**
 * Created by mmoehler on 15.05.16.
 */
@SuppressWarnings("unchecked")
public class ColumnDifference implements Function<Tuple2<List<String>, List<String>>, List<List<String>>> {

    private static final List<List<String>> INTERNAL = List2DBuilder.matrixOf("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();

    private static final BiFunction<List<String>, List<String>, List<List<String>>>[] ACTIONS = new BiFunction[]{A1, A2, A3, A4, A5};
    private static final Function<List<Tuple2<String, String>>, Integer>[] CONDITIONS = new Function[]{B1, B2, B3, B4};

    private static Function<String, Integer> maskMatrixMapper = s -> (isDASH(s)) ? 0 : 1;
    private static Function<String, Integer> decisionMatrixMapper = s -> (isYES(s)) ? 1 : 0;

    private final List<List<Integer>> M = transpose(makeMaskMatrix(INTERNAL));
    private final List<List<Integer>> D = transpose(makeDecisionMatrix(INTERNAL));

    @Override
    public List<List<String>> apply(Tuple2<List<String>, List<String>> conditionPair) {

        final List<String> left = conditionPair._1();
        final List<String> right = conditionPair._2();

        List<Tuple2<String, String>> prototype = StreamUtils
                .zip(left.stream(), right.stream(), Tuple::of)
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
                .map(Number::intValue).filter(vv -> vv >= 0).collect(Collectors.toList());

        //dumpList1DItems("IDX",indices);

        if (indices.size() != 1) {
            throw new IllegalStateException("Used DT is ambigous!");
        }

        // perform the action at the determined index and ...

        List<List<String>> applied = ACTIONS[indices.get(0)].apply(left, right);

        return transpose(applied);

    }

    List<List<Integer>> makeMaskMatrix(List<List<String>> conditions) {
        List<List<Integer>> M = conditions.stream().map(a -> a.stream().map(maskMatrixMapper).collect(toList())).collect(toList());
        return M;
    }

    List<List<Integer>> makeDecisionMatrix(List<List<String>> conditions) {
        List<List<Integer>> D = conditions.stream().map(a -> a.stream().map(decisionMatrixMapper).collect(toList())).collect(toList());
        return D;
    }

    List<Integer> logicalAnd(List<Integer> a, List<Integer> b) {
        List<Integer> condition = StreamUtils.zip(a.stream(), b.stream(), (x, y) -> x * y)
                .collect(toList());
        return condition;
    }


}
