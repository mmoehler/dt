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
import de.adesso.tools.analysis.completeness.detailed.Functions;
import de.adesso.tools.functions.DtFunctions;
import de.adesso.tools.io.DTDataPacket;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static de.adesso.tools.functions.Adapters.Matrix.adapt;
import static de.adesso.tools.functions.MatrixFunctions.transpose;

/**
 * Created by moehler on 07.04.2016.
 */
public class RulesConsolidationOperator implements BiConsumer<Multimap<Integer, Integer>, Tuple2<TableView<ObservableList<String>>,TableView<ObservableList<String>>>> {
    @Override
    public void accept(Multimap<Integer, Integer> analysisResult, Tuple2<TableView<ObservableList<String>>,TableView<ObservableList<String>>> data) {
        List<List<String>> condCols = transpose(adapt(data._1().getItems()));

        analysisResult.keySet().forEach(k ->{
            LinkedList<Integer> controlItems = new LinkedList<>();
            controlItems.add(k);
            controlItems.addAll(analysisResult.get(k));
            Collections.sort(controlItems, (a,b) -> b-a);
            List<List<String>> conditions2consolidate =  controlItems.stream().map(i -> condCols.get(i)).collect(Collectors.toList());
            final List<List<String>> consolidated = Functions.consolidate().apply(conditions2consolidate);

            controlItems.forEach(i -> {
                if(i == controlItems.getLast()) {
                    DtFunctions.doReplaceRuleConditions(data._1(), OptionalInt.of(i), consolidated.get(0));
                } else {
                    DtFunctions.doRemoveColumns(data._1(), data._2(), OptionalInt.of(i));
                }
            });

        });
    }
}
