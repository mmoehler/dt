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

package de.adesso.dtmg.export.java.treeMethod;

import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by moehler on 06.07.2016.
 */
public class DtEntityStub extends DtEntity {

    public static ObservableList<ObservableList<String>> EMPTY_DEFS = FXCollections.emptyObservableList();
    public static ObservableList<ActionDeclTableViewModel> EMPTY_ADECL = FXCollections.emptyObservableList();
    public static ObservableList<ConditionDeclTableViewModel> EMPTY_CDECL = FXCollections.emptyObservableList();

    public static DtEntity createForActionDefinitions(ObservableList<ObservableList<String>> actionDefinitions) {
        return new DtEntityStub(EMPTY_CDECL,EMPTY_DEFS,EMPTY_ADECL,actionDefinitions);
    }

    public static DtEntity createForConditionDefinitions(ObservableList<ObservableList<String>> conditionDefinitions) {
        return new DtEntityStub(EMPTY_CDECL,conditionDefinitions,EMPTY_ADECL,EMPTY_DEFS);
    }


    public static DtEntity createFor(ObservableList<ActionDeclTableViewModel> actionDeclarations,ObservableList<ObservableList<String>> actionDefinitions) {
        return new DtEntityStub(EMPTY_CDECL,EMPTY_DEFS,actionDeclarations,actionDefinitions);
    }

    public DtEntityStub(ObservableList<ConditionDeclTableViewModel> conditionDeclarations, ObservableList<ObservableList<String>> conditionDefinitions, ObservableList<ActionDeclTableViewModel> actionDeclarations, ObservableList<ObservableList<String>> actionDefinitions) {
        super(conditionDeclarations, conditionDefinitions, actionDeclarations, actionDefinitions);
    }
}
