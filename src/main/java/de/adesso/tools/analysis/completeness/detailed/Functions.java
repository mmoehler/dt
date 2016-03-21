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

import de.adesso.tools.functions.MatrixFunctions;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;

import static de.adesso.tools.common.Reserved.*;
import static java.util.stream.Collectors.toList;

/**
 * Created by mmoehler on 20.03.16.
 */
public class Functions {

    public static Function<String, Integer> maskMatrixMapper = s -> {
        if (isDASH(s)) {
            return 0;
        } else if (isYES(s) || isNO(s)) {
            return 1;
        }
        throw new IllegalStateException("Illegal code: " + s + "!");
    };

    public static Function<String, Integer> decisionMatrixMapper = s -> {
        if (isDASH(s) || isNO(s)) {
            return 0;
        } else if (isYES(s)) {
            return 1;
        }
        throw new IllegalStateException("Illegal code: " + s + "!");
    };

    public static BinaryOperator<List<List<String>>> difference() {
        return new RulesDifferenceOperator();
    }

    public static Function<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> consolidate() {
        return new ConditionsConsolidateOperator();
    }

    // -- Pactories ----------------------------------------------------------------------------------------------------

    public static List<List<Integer>> makeMaskMatrix(List<List<String>> conditions) {
        List<List<Integer>> M = conditions.stream().map(a -> a.stream().map(maskMatrixMapper).collect(toList())).collect(toList());
        return M;
    }

    public static List<List<Integer>> makeDecisionMatrix(List<List<String>> conditions) {
        List<List<Integer>> M = conditions.stream().map(a -> a.stream().map(decisionMatrixMapper).collect(toList())).collect(toList());
        return M;
    }


}

class RulesDifferenceOperator implements BinaryOperator<List<List<String>>> {
    public RulesDifferenceOperator() {
    }

    @Override
    public List<List<String>> apply(List<List<String>> rf, List<List<String>> ri) {
        return null;
    }
}


class ConditionsConsolidateOperator implements Function<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> {
    public ConditionsConsolidateOperator() {
    }

    @Override
    public ObservableList<ObservableList<String>> apply(ObservableList<ObservableList<String>> conditions) {

        ObservableList<ObservableList<String>>[] observables = new ObservableList[]{MatrixFunctions.copy(conditions)};
        for (int i = 0; i < conditions.size(); i++) {
            ObservableList<ObservableList<String>> tmp = MatrixFunctions.removeRowsAt(observables[0], i);
            ObservableList<ObservableList<String>> cur = MatrixFunctions.transpose(tmp);
            Map<ObservableList<String>, List<Integer>> map = new HashMap<>();
            IntStream.range(0, cur.size()).forEach(j -> map
                    .compute(cur.get(j), (k, v) -> (v == null) ? newWithValue(j) : addValue(v, j)));
            final int row = i;
            map.entrySet().stream().filter(e -> e.getValue().size() > 1)
                    .forEach(f -> {
                        List<Integer> indices = f.getValue();
                        Collections.sort(indices, (a, b) -> (-1));
                        for (int r = 0; r < indices.size(); r++) {
                            if (r == indices.size() - 1) {
                                observables[0].get(row).set(indices.get(r), DASH);
                            } else {
                                observables[0] = MatrixFunctions.removeColumnsAt(observables[0], indices.get(r));
                            }
                        }
                    });
        }
        return observables[0];

    }

    private List<Integer> newWithValue(int value) {
        List<Integer> result = new ArrayList<>();
        result.add(value);
        return result;
    }

    private List<Integer> addValue(List<Integer> l, int value) {
        l.add(value);
        return l;
    }
}