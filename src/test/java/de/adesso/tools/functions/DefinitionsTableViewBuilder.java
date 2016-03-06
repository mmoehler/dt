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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

import static de.adesso.tools.common.MatrixBuilder.*;

/**
* Created by mmoehler on 06.03.16.
*/
class DefinitionsTableViewBuilder extends TableViewBuilder<ObservableList<String>> {

    public static DefinitionsTableViewBuilder newBuilder() {
        return new DefinitionsTableViewBuilder();
    }

    private DefinitionsTableViewBuilder() {
    }

    public DefinitionsTableViewDataBuilder<DefinitionsTableViewBuilder> dim(int rows, int cols) {
        return new DefinitionsTableViewDataBuilder<>(rows, cols, this,
                this::buildResult);
    }

    @Override
    public TableViewBuilder withSelectionAt(int row, int col) {
        return super.withSelectionAt(row, col);
    }

    @Nonnull
    @Override
    public TableView<ObservableList<String>> build() {
        return super.build();
    }

    @Override
    protected void buildResult(int r, int c, String d) {
        ObservableList<ObservableList<String>> tableViewData = observable(on(d).dim(r, c).build());
        tableView = new TableView<>(tableViewData);
        IntStream.rangeClosed(1, c).forEach(i -> {
            TableColumn<ObservableList<String>, String> tc = new TableColumn<>(String.format("R%02d", i));
            tableView.getColumns().add(tc);
        });
    }
}




