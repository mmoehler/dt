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

import de.adesso.dtmg.util.tuple.Tuple2;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Created by moehler ofList 31.03.2016.
 */
public final class Operators {
    private Operators() {
    }

    public static BinaryOperator<Indicator> actionComparison() {
        return new ActionComparisonOperator();
    }

    public static BinaryOperator<Indicator> conditionComparison() {
        return new ConditionComparisonOperator();
    }

    public static Function<Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>,
            Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>> consolidateRules() {
        return new RuleConsolidator();
    }

    public static Function<Tuple2<List<List<String>>, List<List<String>>>, Tuple2<List<List<String>>, List<List<String>>>> rejectDupplicateRules() {
        return new DuplicateRulesRejector();
    }

}

