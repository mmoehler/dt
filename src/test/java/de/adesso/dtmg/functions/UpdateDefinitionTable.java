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

package de.adesso.dtmg.functions;

import de.adesso.dtmg.Dump;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Created by mmoehler ofList 06.05.16.
 */
public class UpdateDefinitionTable implements Consumer<ObservableList<ObservableList<String>>> {

    private final TableView<ObservableList<String>> table;

    public UpdateDefinitionTable(TableView<ObservableList<String>> table) {
        this.table = table;
    }

    @Override
    public void accept(ObservableList<ObservableList<String>> newItems) {
        ObservableList<ObservableList<String>> oldItems = table.getItems();
        table.getItems().clear();

        if (null == oldItems) {
            table.setItems(FXCollections.observableArrayList());
        }

        //noinspection ConstantConditions
        final int oldLen = oldItems.size();
        final int newLen = newItems.size();

        if (newLen != oldLen) {
            table.getColumns().clear();
            IntStream.range(0, newItems.get(0).size())
                    .mapToObj(DtFunctions::createTableColumn)
                    .forEach(table.getColumns()::add);
        }

        Dump.dumpTableItems("NEW ITEMS", newItems);

        newItems.forEach(table.getItems()::add);
        table.refresh();
    }

}
