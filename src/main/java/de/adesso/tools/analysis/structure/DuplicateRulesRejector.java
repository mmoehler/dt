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

import com.google.common.collect.Sets;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.functions.List2DFunctions.removeColumnsAt;

/**
 * Created by mmoehler ofList 30.04.16.
 */
public class DuplicateRulesRejector implements Function<Tuple2<List<List<String>>, List<List<String>>>, Tuple2<List<List<String>>, List<List<String>>>> {

    @Override
    public Tuple2<List<List<String>>, List<List<String>>> apply(Tuple2<List<List<String>>, List<List<String>>> dt) {
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

    private static <T> Set<Integer> indicesOfAllDuplicates(List<T> l) {
        final int sz = l.size() - 1;
        return IntStream.range(0, sz)
                .mapToObj(idx -> indicesOf(l.subList(idx + 1, sz), l.get(idx), idx + 1))
                .reduce(new TreeSet<Integer>((x,y)-> y - x), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
    }

    private static <T> Set<Integer> indicesOf(List<T> l, T t, int offset) {
        return IntStream.range(0,l.size())
                .filter(i -> (l.get(i).equals(t)))
                .mapToObj(i -> (i + offset))
                .collect(Collectors.toSet());
    }

}
