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

import de.adesso.tools.common.builder.List2DBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.adesso.tools.analysis.completeness.detailed.Functions.columnDifference;
import static de.adesso.tools.analysis.completeness.detailed.Functions.consolidate;
import static de.adesso.tools.functions.List2DFunctions.transpose;

/**
 * Created by mmoehler on 14.05.16.
 */
public class RulesDifferenceOperator implements Function<List<List<String>>, List<List<String>>> {

    private final BinaryOperator<List<List<String>>> merge = (l, r) -> {
        r.forEach(l::add);
        return l;
    };

    // TODO check this .. !!
    private final BinaryOperator<List<List<String>>> merge0 = (l, r) -> Stream.concat(l.stream(), r.stream()).collect(Collectors.toList());



    public RulesDifferenceOperator() {
    }

    @Override
    public List<List<String>> apply(List<List<String>> conditions) {
        //final List<List<String>> rights = List2DBuilder.ofList("Y,Y,N,N,Y,Y,Y,N,N,-,N,Y").dim(3,4).transposed().build();
        List<List<String>> rights = transpose(conditions);
        String s[] = new String[conditions.size()];
        Arrays.fill(s, "-");
        String joined = String.join(",", s);
        List<List<String>> tmp = List2DBuilder.matrixOf(joined).dim(rights.size(), 1).transposed().build();
        @SuppressWarnings("unchecked") final List<List<String>>[] lefts = new List[]{tmp};
        for (List<String> right : rights) {
            lefts[0] = lefts[0].stream()
                    .map(l -> columnDifference.apply(l, right))
                    .reduce(new ArrayList<>(), merge);
            lefts[0] = transpose(consolidate().apply(transpose(lefts[0])));
        }
        return transpose(lefts[0]);
    }



}
