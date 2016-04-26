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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import de.adesso.tools.analysis.completeness.detailed.Functions;
import de.adesso.tools.functions.MatrixFunctions;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple4;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.functions.MatrixFunctions.*;
import static de.adesso.tools.functions.MoreCollectors.*;

/**
 * Created by moehler on 07.04.2016.
 */
public class RuleConsolidator implements Function<Tuple2<List<List<String>>, List<List<String>>>, Tuple2<List<List<String>>, List<List<String>>>> {

    @Override
    public Tuple2<List<List<String>>, List<List<String>>> apply(Tuple2<List<List<String>>, List<List<String>>> decisionTable) {
        final List<List<String>> conditions = decisionTable._1();
        final List<List<String>> actions = decisionTable._2();
        final List<List<String>> transposedActions = transpose(actions);
        final List<List<String>> transposedConditions = transpose(conditions);

        // detecting indices of dupplicate action combination
        List<List<Integer>> indices = Stream.of(transposedActions)
                .map(indicesOfDuplicateActions())
                .collect(toSingleObject());

        // map indices of actions to their conditions
        List<List<List<String>>> parts = indices.stream()
                .map(c -> c.stream()
                        .map(i -> transposedConditions.get(i))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        // consolidate the mapped conditions for each block
        List<List<List<String>>> consolidated = parts.stream()
                .map(MatrixFunctions::transpose)
                .map(Functions.consolidate())
                .collect(Collectors.toList());

        // merge the original state with the consolidated state
        return Stream.of(Tuple.of(conditions,actions,consolidated,indices))
                .map(merge())
                .collect(toSingleObject());

    }

    private static Function<Tuple4<List<List<String>>,List<List<String>>,List<List<List<String>>>,List<List<Integer>>>, Tuple2<List<List<String>>, List<List<String>>>> merge() {
            return (tuple) -> {
                List<List<String>> originalConditions = tuple._1();
                List<List<String>> originalActions = tuple._2();
                List<List<List<String>>> consolidatedConditions = tuple._3();
                List<List<Integer>> indicesOfConsolidationsConditions = tuple._4();

                TreeSet<Integer> toDelete = new TreeSet<>((a, b) -> b-a);
                Iterator<List<List<String>>> consConIt = consolidatedConditions.iterator();
                Iterator<List<Integer>> inxConsConIt = indicesOfConsolidationsConditions.iterator();
                List<List<String>> _originalConditions[] = new List[]{originalConditions};
                List<List<String>> _originalActions[] = new List[]{originalActions};

                for(;consConIt.hasNext() && inxConsConIt.hasNext();) {
                    Iterator<List<String>> curConsConIt = transpose(consConIt.next()).iterator();
                    Iterator<Integer> curInxConsConIt = inxConsConIt.next().iterator();

                    for(;curInxConsConIt.hasNext();) {
                        Integer idx = curInxConsConIt.next();
                        if(curConsConIt.hasNext()) {
                            _originalConditions[0] = replaceColumnsAt(_originalConditions[0],idx,curConsConIt.next());
                        } else {
                            toDelete.add(idx);
                        }
                    }
                }
                for(int idx : toDelete) {
                    _originalConditions[0] = removeColumnsAt(_originalConditions[0], idx);
                    _originalActions[0] = removeColumnsAt(_originalActions[0], idx);
                }

                originalConditions.clear();
                _originalConditions[0].forEach(originalConditions::add);
                originalActions.clear();
                _originalActions[0].forEach(originalActions::add);
                return Tuple.of(_originalConditions[0],_originalActions[0]);
            };
    }



    private final static Function<List<List<String>>, List<List<Integer>>> indicesOfDuplicateActions() {
        return (actions) -> {
            final ListMultimap<List<String>, Integer> tmp =
                    Multimaps.newListMultimap(new HashMap<List<String>, Collection<Integer>>(), () -> new LinkedList<Integer>());

            IntStream.range(0,actions.size()).forEach(i -> tmp.put(actions.get(i), i));
            tmp.asMap().entrySet().removeIf(entry -> entry.getValue().size()<2);

            return tmp.asMap().values().stream()
                    .map(o -> o.stream()
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
        };
    }



}

