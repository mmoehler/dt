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

package de.adesso.dtmg.ui.main;

import de.adesso.dtmg.functions.DtFunctions;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.saxsys.mvvmfx.FxmlView;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 05.06.16.
 */
public abstract class DtView implements FxmlView<MainViewModel> {

    private final DefinitionsColumnConsumer definitionsColumnConsumer = new DefinitionsColumnConsumer();

    private final DefinitionsRowConsumer conditionDefinitinosRowConsumer = new ConditionDefinitionsRowConsumer();
    private final DefinitionsRowConsumer actionDefinitionsRowConsumer = new ActionDefinitionsRowConsumer();

    public abstract TableView<ActionDeclTableViewModel> getActionDeclarationsTable();

    public abstract TableView<ObservableList<String>> getActionDefinitionsTable();

    public abstract TableView<ConditionDeclTableViewModel> getConditionDeclarationsTable();

    public abstract TableView<ObservableList<String>> getConditionDefinitionsTable();

    protected DefinitionsColumnConsumer getDefinitionsColumnConsumer() {
        return definitionsColumnConsumer;
    }

    protected void updateDefinitions(ObservableList<ObservableList<String>> conditionDefinitions, ObservableList<ObservableList<String>> actionDefinitions) {
        final int size = conditionDefinitions.get(0).size();
        Stream.of(getConditionDefinitionsTable(), getActionDefinitionsTable()).forEach(t -> t.getItems().clear());
        getDefinitionsColumnConsumer().accept(size);
        conditionDefinitions.forEach(conditionDefinitinosRowConsumer);
        actionDefinitions.forEach(actionDefinitionsRowConsumer);
    }

    abstract class DefinitionsRowConsumer implements Consumer<ObservableList<String>> {
        @Override
        public void accept(ObservableList<String> definitions) {
            TableView<ObservableList<String>> table = getDefinitionsTable();
            table.getItems().add(definitions);
            table.refresh();
        }

        protected abstract TableView<ObservableList<String>> getDefinitionsTable();

    }

    class ConditionDefinitionsRowConsumer extends DefinitionsRowConsumer {
        @Override
        protected TableView<ObservableList<String>> getDefinitionsTable() {
            return getConditionDefinitionsTable();
        }
    }

    class ActionDefinitionsRowConsumer extends DefinitionsRowConsumer {
        @Override
        protected TableView<ObservableList<String>> getDefinitionsTable() {
            return getActionDefinitionsTable();
        }
    }

    class DefinitionsColumnConsumer implements IntConsumer {
        @Override
        public void accept(int size) {
            Stream.of(getConditionDefinitionsTable(), getActionDefinitionsTable()).forEach(t -> updateColumns(t, size));
        }

        private void updateColumns(TableView<ObservableList<String>> table, int size) {
            table.getColumns().clear();
            IntStream.range(0, size)
                    .mapToObj(DtFunctions::createTableColumn)
                    .forEach(table.getColumns()::add);
        }
    }

}
