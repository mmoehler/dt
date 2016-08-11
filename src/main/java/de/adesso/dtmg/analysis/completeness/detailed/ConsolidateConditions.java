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

package de.adesso.dtmg.analysis.completeness.detailed;

import com.google.common.collect.LinkedListMultimap;
import de.adesso.dtmg.util.Dump;
import de.adesso.dtmg.util.ObservableList2DFunctions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.dtmg.util.MoreCollectors.toObservableList;
import static de.adesso.dtmg.util.MoreCollectors.toSingleObject;

/**
 * Created by mmoehler on 08.05.16.
 */
public class ConsolidateConditions implements Function<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> {
    public static final List<String> POSSIBLE_INDICATORS = Arrays.asList("Y", "N");

    public static ConsolidateConditions consolidateConditions() {
        return new ConsolidateConditions();
    }


    private ConsolidateConditions() {
    }

    @Override
    public ObservableList<ObservableList<String>> apply(ObservableList<ObservableList<String>> conditions) {

        Dump.dumpTableItems("001", conditions);


        List<Boolean> indicatorsComplete = rowsWithAllPossibleIndicators(conditions);

        System.out.println("indicatorsComplete = " + indicatorsComplete);

        ObservableList<ObservableList<String>> copy = conditions.stream()
                .map(l -> l.stream()
                        .collect(toObservableList()))
                .collect(toObservableList());
        @SuppressWarnings("unchecked") ObservableList<ObservableList<String>> _copy[] = new ObservableList[]{copy};

        Dump.dumpTableItems("002", _copy[0]);

        for (int currentRow = 0; currentRow < conditions.size(); currentRow++) {

            List<Integer> indicesOfDashedIndicators = determineIndicesOfDashedIndicators(_copy[0], currentRow);

            if (indicatorsComplete.get(currentRow)) {
                ObservableList<ObservableList<String>> conditionRows = cleanupConditions(_copy[0], currentRow, indicesOfDashedIndicators);

                Dump.dumpTableItems("003 (conditionRows)", conditionRows);

                ObservableList<ObservableList<String>> conditionColumns = Stream.of(conditionRows).map(ObservableList2DFunctions.transpose()).collect(toSingleObject());

                Dump.dumpTableItems("004 (conditionColumns)", conditionRows);

                LinkedListMultimap<List<String>, Integer> duplicateRules = determineCountOfDuplicateRules(conditionColumns);

                if (!duplicateRules.isEmpty()) {
                    List<Integer> toDelete = new ArrayList<>();
                    Map<Boolean, List<Integer>> partitioned = groupDuplicatesAccordingToTheirIndices(duplicateRules, conditionColumns);

                    final int cr = currentRow;
                    partitioned.forEach((k, v) -> v.forEach(c -> {
                        if (k) {
                            _copy[0].get(cr).set(c, "-");
                        } else {
                            toDelete.add(c);
                        }
                    }));

                    toDelete.stream()
                            .sorted((a, b) -> b - a)
                            .forEach(i -> _copy[0] = _copy[0].stream()
                                    .map(ObservableList2DFunctions.removeColumn(i))
                                    .collect(toObservableList()));

                    updateRowsWithAllPossibleIndicators(currentRow, indicatorsComplete, _copy[0]);
                }
            }
        }
        Dump.dumpTableItems("004 (consolidated)", _copy[0]);
        return (_copy[0]);
    }

    public ObservableList<ObservableList<String>> cleanupConditions(ObservableList<ObservableList<String>> original, int currentRow, List<Integer> indicesOfDashedIndicators) {
        //ObservableList<ObservableList<String>> conditionRows = List2DFunctions.removeRowsAt(original, currentRow);
        ObservableList<ObservableList<String>> r = IntStream.range(0, original.size())
                .filter(i -> i != currentRow)
                .mapToObj(j -> FXCollections.observableList(original.get(j)))
                .collect(toObservableList());
        //noinspection unchecked
        final ObservableList<ObservableList<String>>[] conditionRows = new ObservableList[]{r};

        if(!indicesOfDashedIndicators.isEmpty()) {

            conditionRows[0] = indicesOfDashedIndicators.stream()
                    .sorted((a, b) -> b - a)
                    .map(i -> conditionRows[0].stream()
                            .filter(k -> !k.equals(conditionRows[0].get(i)))
                            .collect(toSingleObject()))
                    .collect(toObservableList());

        }
/*
        for (int ii : indicesOfDashedIndicators) {
            conditionRows = removeColumnsAt(conditionRows, ii);
        }
*/
        return conditionRows[0];
    }

    private Map<Boolean, List<Integer>> groupDuplicatesAccordingToTheirIndices(LinkedListMultimap<List<String>, Integer> dupplicateRules, ObservableList<ObservableList<String>> step01Cols) {
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

    protected static LinkedListMultimap<List<String>, Integer> determineCountOfDuplicateRules(ObservableList<ObservableList<String>> step01Cols) {
        LinkedListMultimap<List<String>, Integer> counter = LinkedListMultimap.create();
        IntStream.range(0, step01Cols.size()).forEach(j -> counter.put(step01Cols.get(j), j));
        // remove all entries with values size is 1
        for (Iterator<Map.Entry<List<String>, Collection<Integer>>> it = counter.asMap().entrySet().iterator(); it.hasNext(); )
            if (it.next().getValue().size() == 1)
                it.remove();
        return counter;
    }

    protected static List<Integer> determineIndicesOfDashedIndicators(ObservableList<ObservableList<String>> conditions, int row) {
        return IntStream.range(0, conditions.get(row).size())
                .filter(i -> conditions.get(row).get(i).equals("-"))
                .boxed()
                .collect(Collectors.toList());
    }

    protected static List<Boolean> rowsWithAllPossibleIndicators(ObservableList<ObservableList<String>> conditions) {
        return IntStream.range(0, conditions.size())
                .mapToObj(i -> (conditions.get(i).containsAll(POSSIBLE_INDICATORS)))
                .collect(Collectors.toList());
    }

    protected void updateRowsWithAllPossibleIndicators(int row, List<Boolean> completeness, ObservableList<ObservableList<String>> conditions) {
        completeness.set(row, conditions.get(row).containsAll(POSSIBLE_INDICATORS));
    }

}

