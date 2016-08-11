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

package de.adesso.dtmg.analysis.structure;

import de.adesso.dtmg.util.Dump;
import de.adesso.dtmg.analysis.completeness.detailed.ConsolidateConditions;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.adesso.dtmg.util.tuple.Tuple4;
import javafx.collections.ObservableList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.dtmg.util.MoreCollectors.toObservableList;
import static de.adesso.dtmg.util.MoreCollectors.toSingleObject;
import static de.adesso.dtmg.util.ObservableList2DFunctions.*;

/**
 * Created by moehler ofList 07.04.2016.
 */
public class RuleConsolidator implements Function<Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>, Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>> {

    @Override
    public Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> apply(Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> decisionTable) {
        final ObservableList<ObservableList<String>> conditions = decisionTable._1();
        final ObservableList<ObservableList<String>> actions = decisionTable._2();
        final ObservableList<ObservableList<String>> transposedActions = transposeActions(actions);
        final ObservableList<ObservableList<String>> transposedConditions = transposeConditions(conditions);
        final List<List<Integer>> indices = detectIndicesOfDupplicateActionCombinations(transposedActions);
        final ObservableList<ObservableList<ObservableList<String>>> parts = determineConditions4Consolidation(transposedConditions, indices);
        final ObservableList<ObservableList<ObservableList<String>>> consolidated = consolidateConditions(parts);
        return mergeRules(conditions, actions, indices, consolidated);
    }

    Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> mergeRules(ObservableList<ObservableList<String>> conditions, ObservableList<ObservableList<String>> actions, List<List<Integer>> indices, ObservableList<ObservableList<ObservableList<String>>> consolidated) {
        // merge the original state with the consolidated state
        return Stream.of(Tuple.of(conditions, actions, consolidated, indices))
                .map(merge())
                .collect(toSingleObject());
    }

    ObservableList<ObservableList<ObservableList<String>>> consolidateConditions(ObservableList<ObservableList<ObservableList<String>>> parts) {
        // consolidate the mapped conditions for each block
        ObservableList<ObservableList<ObservableList<String>>> consolidated = parts.stream()
                .map(transpose())
                .map(ConsolidateConditions.consolidateConditions())
                .collect(toObservableList());

        for (ObservableList<ObservableList<String>> p : consolidated) {
            Dump.dumpTableItems("CONSOLIDATED PART", p);
        }
        return consolidated;
    }

    ObservableList<ObservableList<ObservableList<String>>> determineConditions4Consolidation(ObservableList<ObservableList<String>> transposedConditions, List<List<Integer>> indices) {
        // map indices of actions to their conditions
        ObservableList<ObservableList<ObservableList<String>>> parts = indices.stream()
                .map(c -> c.stream()
                        .map(transposedConditions::get)
                        .collect(toObservableList()))
                .collect(toObservableList());
        return parts;
    }

    List<List<Integer>> detectIndicesOfDupplicateActionCombinations(ObservableList<ObservableList<String>> transposedActions) {
        Map<ObservableList<String>, List<Integer>> collect = IntStream.range(0, transposedActions.size())
                .boxed()
                .collect(Collectors.groupingBy(transposedActions::get, Collectors.toList()));

        List<List<Integer>> indices = collect.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(l -> l.size() > 1)
                .collect(Collectors.toList());
        return indices;
    }

    ObservableList<ObservableList<String>> transposeConditions(ObservableList<ObservableList<String>> conditions) {
        final ObservableList<ObservableList<String>> transposedConditions = Stream.of(conditions)
                .map(transpose())
                .collect(toSingleObject());
        return transposedConditions;
    }

    ObservableList<ObservableList<String>> transposeActions(ObservableList<ObservableList<String>> actions) {
        final ObservableList<ObservableList<String>> transposedActions = Stream.of(actions)
                .map(transpose())
                .collect(toSingleObject());
        return transposedActions;
    }

    private static Function<Tuple4<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>,
            ObservableList<ObservableList<ObservableList<String>>>, List<List<Integer>>>,
            Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>> merge() {

        return (tuple) -> {
            ObservableList<ObservableList<String>> originalConditions = tuple._1();
            Dump.dumpTableItems("_1", originalConditions);
            ObservableList<ObservableList<String>> originalActions = tuple._2();
            Dump.dumpTableItems("_2", originalActions);
            ObservableList<ObservableList<ObservableList<String>>> consolidatedConditions = tuple._3();
            Dump.dumpTableItems("_3", consolidatedConditions);
            List<List<Integer>> indicesOfConsolidationsConditions = tuple._4();
            Dump.dumpTableItems("_4", indicesOfConsolidationsConditions);

            TreeSet<Integer> toDelete = new TreeSet<>((a, b) -> b - a);
            Iterator<ObservableList<ObservableList<String>>> consConIt = consolidatedConditions.iterator();
            Iterator<List<Integer>> inxConsConIt = indicesOfConsolidationsConditions.iterator();
            ObservableList<ObservableList<String>> _originalConditions[] = new ObservableList[]{originalConditions};
            ObservableList<ObservableList<String>> _originalActions[] = new ObservableList[]{originalActions};

            for (; consConIt.hasNext() && inxConsConIt.hasNext(); ) {

                //Iterator<ObservableList<String>> curConsConIt = transpose(consConIt.next()).iterator();
                Iterator<ObservableList<String>> curConsConIt = Stream.of(consConIt.next())
                        .map(transpose())
                        .collect(toSingleObject())
                        .iterator();

                Iterator<Integer> curInxConsConIt = inxConsConIt.next().iterator();

                for (; curInxConsConIt.hasNext(); ) {
                    Integer idx = curInxConsConIt.next();
                    if (curConsConIt.hasNext()) {
                        _originalConditions[0] = _originalConditions[0].stream()
                                .map(replaceColumn(curConsConIt.next(), idx))
                                .collect(toObservableList());
                    } else {
                        toDelete.add(idx);
                    }
                }
            }
            for (int idx : toDelete) {
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

            return Tuple.of(_originalConditions[0], _originalActions[0]);
        };
    }
}

