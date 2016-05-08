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

package de.adesso.tools.model;

import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.util.tuple.Tuple4;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by mmoehler ofList 04.05.16.
 */
public class DecisionTableModel extends Tuple4<
        ObservableList<ConditionDeclTableViewModel>,
        ObservableList<ObservableList<String>>,
        ObservableList<ActionDeclTableViewModel>,
        ObservableList<ObservableList<String>>> {


    /**
     * Constructs a tuple of 4 elements and initializes them with 4 new ObservableList's
     */
    public DecisionTableModel() {
        super(
                FXCollections.observableArrayList(),
                FXCollections.observableArrayList(),
                FXCollections.observableArrayList(),
                FXCollections.observableArrayList());
    }

    /**
     * Constructs a tuple of 4 elements.
     *
     * @param conditionDecls the 1st element
     * @param conditionDefns the 2nd element
     * @param actionDecls    the 3rd element
     * @param actionsDefns   the 4th element
     */
    public DecisionTableModel(
            ObservableList<ConditionDeclTableViewModel> conditionDecls,
            ObservableList<ObservableList<String>> conditionDefns,
            ObservableList<ActionDeclTableViewModel> actionDecls,
            ObservableList<ObservableList<String>> actionsDefns) {
        super(conditionDecls, conditionDefns, actionDecls, actionsDefns);
    }
}
