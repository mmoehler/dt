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

package de.adesso.tools.functions;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import javax.annotation.Nonnull;

/**
* Created by mmoehler on 06.03.16.
*/
class DefinitionsTableViewBuilder extends TableViewBuilder<ObservableList<String>> {

    public DefinitionsTableViewBuilder() {
    }

    public DefinitionsTableViewDataBuilder<DefinitionsTableViewBuilder> dim(int rows, int cols) {
        return new DefinitionsTableViewDataBuilder<>(rows, cols, this, (c) -> {
            tableView.getItems().clear();
            c.forEach(tableView.getItems()::add);
        });
    }

    @Nonnull
    @Override
    public TableView<ObservableList<String>> build() {
        return super.build();
    }

    @Override
    protected int getColCount() {
        return tableView.getItems().get(0).size();
    }
}




