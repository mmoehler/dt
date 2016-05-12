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
import de.adesso.tools.Dump;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple4;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.functions.MoreCollectors.toObservableList;
import static de.adesso.tools.functions.MoreCollectors.toSingleObject;
import static de.adesso.tools.functions.ObservableList2DFunctions.*;

/**
 * Created by moehler ofList 07.04.2016.
 */
public class RuleConsolidator implements Function<Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>, Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>> {

    @Override
    public Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> apply(Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> decisionTable) {
        final ObservableList<ObservableList<String>> conditions = decisionTable._1();
        final ObservableList<ObservableList<String>> actions = decisionTable._2();

        final ObservableList<ObservableList<String>> transposedActions = Stream.of(actions)
                .map(transpose())
                .collect(toSingleObject());
        Dump.dumpTableItems("TRANSPOSED ACTIONS", transposedActions);

        final ObservableList<ObservableList<String>> transposedConditions = Stream.of(conditions)
                .map(transpose())
                .collect(toSingleObject());
        Dump.dumpTableItems("TRANSPOSED CONDITIONS", transposedConditions);


        // detecting indices of dupplicate action combination
        ObservableList<ObservableList<Integer>> indices = Stream.of(transposedActions)
                .map(indicesOfDuplicateActions())
                .collect(toSingleObject());

        Dump.dumpTableItems("INDICES", indices);


        // map indices of actions to their conditions
        ObservableList<ObservableList<ObservableList<String>>> parts = indices.stream()
                .map(c -> c.stream()
                        .map(i -> transposedConditions.get(i))
                        .collect(toObservableList()))
                .collect(toObservableList());

        for(ObservableList<ObservableList<String>> p : parts) {
            Dump.dumpTableItems("PART", p);
        }


        // consolidate the mapped conditions for each block
        ObservableList<ObservableList<ObservableList<String>>> consolidated = parts.stream()
                .map(transpose())
                //.map(Functions.consolidate())
                .collect(toObservableList());

        for(ObservableList<ObservableList<String>> p : consolidated) {
            Dump.dumpTableItems("CONSOLIDATED PART", p);
        }


        // merge the original state with the consolidated state
        return Stream.of(Tuple.of(conditions,actions,consolidated,indices))
                .map(merge())
                .collect(toSingleObject());

    }

    private static Function<Tuple4<ObservableList<ObservableList<String>>,ObservableList<ObservableList<String>>,
            ObservableList<ObservableList<ObservableList<String>>>,ObservableList<ObservableList<Integer>>>,
            Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>> merge() {

        return (tuple) -> {
                ObservableList<ObservableList<String>> originalConditions = tuple._1();
            Dump.dumpTableItems("_1", originalConditions);
                ObservableList<ObservableList<String>> originalActions = tuple._2();
            Dump.dumpTableItems("_2", originalActions);
                ObservableList<ObservableList<ObservableList<String>>> consolidatedConditions = tuple._3();
            Dump.dumpTableItems("_3", consolidatedConditions);
                ObservableList<ObservableList<Integer>> indicesOfConsolidationsConditions = tuple._4();
            Dump.dumpTableItems("_4", indicesOfConsolidationsConditions);

                TreeSet<Integer> toDelete = new TreeSet<>((a, b) -> b-a);
                Iterator<ObservableList<ObservableList<String>>> consConIt = consolidatedConditions.iterator();
                Iterator<ObservableList<Integer>> inxConsConIt = indicesOfConsolidationsConditions.iterator();
                ObservableList<ObservableList<String>> _originalConditions[] = new ObservableList[]{originalConditions};
                ObservableList<ObservableList<String>> _originalActions[] = new ObservableList[]{originalActions};

                for(;consConIt.hasNext() && inxConsConIt.hasNext();) {

                    //Iterator<ObservableList<String>> curConsConIt = transpose(consConIt.next()).iterator();
                    Iterator<ObservableList<String>> curConsConIt = Stream.of(consConIt.next())
                            .map(transpose())
                            .collect(toSingleObject())
                            .iterator();

                    Iterator<Integer> curInxConsConIt = inxConsConIt.next().iterator();

                    for(;curInxConsConIt.hasNext();) {
                        Integer idx = curInxConsConIt.next();
                        if(curConsConIt.hasNext()) {
                            _originalConditions[0] = _originalConditions[0].stream()
                                    .map(replaceColumn(curConsConIt.next(),idx))
                                    .collect(toObservableList());
                        } else {
                            toDelete.add(idx);
                        }
                    }
                }
                for(int idx : toDelete) {
                    _originalConditions[0] = _originalConditions[0].stream()
                            .map(removeColumn(idx))
                            .collect(toObservableList());

                    _originalActions[0] = _originalActions[0]
                            .stream().map(removeColumn(idx))
                            .collect(toObservableList());
                }

                originalConditions.clear();
                _originalConditions[0].forEach(originalConditions::add);
                originalActions.clear();
                _originalActions[0].forEach(originalActions::add);

            Dump.dumpTableItems("_10", originalConditions);
            Dump.dumpTableItems("_11", originalActions);

                return Tuple.of(_originalConditions[0],_originalActions[0]);
            };
    }



    private final static Function<ObservableList<ObservableList<String>>, ObservableList<ObservableList<Integer>>> indicesOfDuplicateActions() {
        return (actions) -> {
            final ListMultimap<ObservableList<String>, Integer> tmp =
                    Multimaps.newListMultimap(new HashMap<>(), () -> new LinkedList<Integer>());

            IntStream.range(0,actions.size()).forEach(i -> tmp.put(actions.get(i), i));
            tmp.asMap().entrySet().removeIf(entry -> entry.getValue().size()<2);

            return tmp.asMap().values().stream()
                    .map(o -> o.stream()
                            .collect(toObservableList()))
                    .collect(toObservableList());
        };
    }



}

