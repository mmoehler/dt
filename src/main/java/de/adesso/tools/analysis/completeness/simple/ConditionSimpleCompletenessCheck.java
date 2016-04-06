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

package de.adesso.tools.analysis.completeness.simple;

import de.adesso.tools.functions.MatrixFunctions;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple3;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.functions.DtFunctions.determineCountIndicatorsPerRow;
import static de.adesso.tools.functions.DtFunctions.determineMaxColumns;

/**
 * Created by mohler on 24.01.16.
 */
public final class ConditionSimpleCompletenessCheck {

    public static final String IRRELEVANT = "-";

    private ConditionSimpleCompletenessCheck() {
        super();
    }

    /**
     * Cmpleteness I -
     *
     * @param decls
     * @param defns
     * @return
     */
    public static Tuple3<Boolean, Integer, Integer> isFormalComplete(ObservableList<ConditionDeclTableViewModel> decls, ObservableList<ObservableList<String>> defns) {
        final List<Integer> indicatorsPerRow = determineCountIndicatorsPerRow(decls);
        final List<List<Integer>> list = IntStream.range(0, defns.size()).mapToObj(i -> defns.get(i).stream().map(j -> {
            if (IRRELEVANT.equals(j)) {
                return indicatorsPerRow.get(i);
            }
            return 1;
        }).collect(Collectors.toList())).collect(Collectors.toList());
        List<List<Integer>> transposed = MatrixFunctions.transpose(list);
        final Integer reduced = transposed.stream().map(l -> l.stream().reduce(1, (a, b) -> a * b)).reduce(0, (c, d) -> c + d);
        final Integer all = determineMaxColumns(decls);
        return Tuple.of(all == reduced, all, reduced);
    }


}
