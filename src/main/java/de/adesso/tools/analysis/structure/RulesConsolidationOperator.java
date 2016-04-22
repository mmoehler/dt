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

import com.google.common.collect.Multimap;
import de.adesso.tools.Dump;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.function.BiConsumer;

import static de.adesso.tools.functions.Adapters.Matrix.adapt;
import static de.adesso.tools.functions.MatrixFunctions.transpose;

/**
 * Created by moehler on 07.04.2016.
 */
public class RulesConsolidationOperator implements BiConsumer<Multimap<Integer, Integer>, Tuple2<TableView<ObservableList<String>>,TableView<ObservableList<String>>>> {
    @Override
    public void accept(Multimap<Integer, Integer> analysisResult, Tuple2<TableView<ObservableList<String>>,TableView<ObservableList<String>>> data) {

        Dump.dumpMap("ANALYSISRESULT", analysisResult.asMap());


        List<List<String>> conditionColumns = transpose(adapt(data._1().getItems()));

        // Transformation of the analysisresult Multimap into 2 Lists one with the indices of the columns

        /*
        given:  a) Conditiondefinitions initialized as follows:

                                          1 2 3 4 5 6
                                          Y Y Y Y Y Y
                                          Y Y Y Y N N
                                          Y Y N N Y Y
                                          Y N Y N Y N

                b) REsult of the Structural Analsysis is:

                                           1 2 3 4 5 6
                STRUCTURE ANALYSIS RULE  1 . - * - * -
                STRUCTURE ANALYSIS RULE  2 . . - * - *
                STRUCTURE ANALYSIS RULE  3 . . . - - -
                STRUCTURE ANALYSIS RULE  4 . . . . - -
                STRUCTURE ANALYSIS RULE  5 . . . . . -






        List<Integer> index = analysisResult.asMap().entrySet().stream()
                .flatMap(k -> Stream.concat(k.getValue().stream(), Stream.<Integer>builder().add(k.getKey()).build()))
                .collect(Collectors.toList());


        // { 1 = [3,5] } -> columns 1,3 and 5 can be consolidated.


        // transform { 1 = [3,5] } to a list

        Function<Map.Entry<Integer, Collection<Integer>>, List<Integer>> flatEntry = (e) ->
                Stream.concat(e.getValue().stream(), Stream.<Integer>builder().add(e.getKey())
                        .build())
                        .collect(Collectors.toList());

        // transform the indices to a list of columns with the given indices

        BiFunction<List<List<String>>, List<Integer>, List<List<String>>> mapToConditionDefinitions = (a,i,r) ->
                i.stream().map(i -> a.get(i)).collect(Collectors.toList());

        // consolidate it!
        final List<List<String>> consolidated = Functions.consolidate().apply(conditions2consolidate);

        // populate it


        // -------------------

        List<List<String>> columns = analysisResult.asMap().entrySet().stream()
                .map(flatEntry).flatMap(e -> e.stream()).map(conditionColumns::get).collect(Collectors.toList());

        List<List<String>> rows = transpose(columns);

        IntStream.range(0,rows.size()).mapToObj(i -> {
                    List<List<String>> curdata = removeRowsAt(rows, i);
                    List<List<String>> cols = transpose(curdata);
                    return cols;
                });





        // -------------------


        analysisResult.keySet().forEach(k ->{
            LinkedList<Integer> items2Change = new LinkedList<>();
            LinkedList<Integer> items2Remove = new LinkedList<>();
            items2Change.add(k);
            items2Remove.addAll(analysisResult.get(k));
            Collections.sort(items2Change, (a,b) -> b-a);
            List<List<String>> conditions2consolidate =  items2Change.stream().map(i -> conditionColumns.get(i)).collect(Collectors.toList());
            final List<List<String>> consolidated = Functions.consolidate().apply(conditions2consolidate);

            items2Change.forEach(i -> {
                if(i == items2Change.getLast()) {
                    DtFunctions.doReplaceRuleConditions(data._1().getItems(), OptionalInt.of(i), consolidated.get(0));
                } else {
                    DtFunctions.doRemoveColumns(data._1(), data._2(), OptionalInt.of(i));
                }
            });

        });
        */
    }
}
