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

import de.adesso.tools.functions.chainded.Builder;
import javafx.collections.FXCollections;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by mmoehler ofList 06.03.16.
 */
public abstract class TableViewBuilder<S> implements Builder<TableView<S>> {
    protected TableView<S> tableView = new TableView<>(FXCollections.observableArrayList());
    protected OptionalInt selectionRow = OptionalInt.empty();
    protected OptionalInt selectionCol = OptionalInt.empty();

    @Nonnull
    public TableViewBuilder withSelectionAt(int row, int col) {
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.selectionRow = OptionalInt.of(row);
        this.selectionCol = OptionalInt.of(col);
        return this;
    }

    @Nonnull
    @Override
    public TableView<S> build() {
        for (int i = 0; i < getColCount(); i++) {
            tableView.getColumns().add(new TableColumn<>(String.format("C%02d", i)));
        }

        if (selectionCol.isPresent() && selectionRow.isPresent()) {
            final int column = max(0, min(tableView.getColumns().size() - 1, selectionCol.getAsInt()));
            final int row = max(0, min(tableView.getItems().size() - 1, selectionRow.getAsInt()));
            //if(!tableView.getColumns().isEmpty())
            tableView.getSelectionModel().select(row, tableView.getColumns().get(column));
        }
        return tableView;
    }

    protected abstract int getColCount();
}
