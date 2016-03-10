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

import de.adesso.tools.ui.DeclarationsTableCell;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.annotation.Nonnull;

/**
 * Created by moehler on 10.03.2016.
 */

/**
 * {@code TableColumnBuilder} TableColumnBuilder static inner class.
 */
public class TableColumnBuilder<S,C> implements Builder<TableColumn> {
    private String columnName;
    private String propertyName;
    private int prefWidth;
    private int minWidth;
    private int maxWidth;
    private boolean resizable;
    private Pos alignment;
    private EventHandler<TableColumn.CellEditEvent<S, String>> value;
    private C caller;
    private Callback<TableColumnBuilder> callback;

    public TableColumnBuilder() {
    }

    public TableColumnBuilder(String columnName, C caller, Callback<TableColumnBuilder> callback) {
        this.columnName = columnName;
        this.caller = caller;
        this.callback = callback;
    }

    /**
     * Sets the {@code columnName} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code columnName} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder columnName(@Nonnull String val) {
        columnName = val;
        return this;
    }

    /**
     * Sets the {@code propertyName} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code propertyName} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder propertyName(@Nonnull String val) {
        propertyName = val;
        return this;
    }

    /**
     * Sets the {@code prefWidth} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code prefWidth} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder prefWidth(int val) {
        prefWidth = val;
        return this;
    }

    /**
     * Sets the {@code minWidth} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code minWidth} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder minWidth(int val) {
        minWidth = val;
        return this;
    }

    /**
     * Sets the {@code maxWidth} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code maxWidth} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder maxWidth(int val) {
        maxWidth = val;
        return this;
    }

    /**
     * Sets the {@code resizable} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code resizable} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder resizable(boolean val) {
        resizable = val;
        return this;
    }

    /**
     * Sets the {@code alignment} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code alignment} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder alignment(@Nonnull Pos val) {
        alignment = val;
        return this;
    }

    /**
     * Sets the {@code value} and returns a reference to this TableColumnBuilder so that the methods can be chained together.
     *
     * @param val the {@code value} to set
     * @return a reference to this TableColumnBuilder
     */
    @Nonnull
    public TableColumnBuilder value(@Nonnull EventHandler<TableColumn.CellEditEvent<S, String>> val) {
        value = val;
        return this;
    }

    /**
     * Returns a {@code TableColumnBuilder} built from the parameters previously set.
     *
     * @return a {@code TableColumnBuilder} built with parameters of this {@code TableColumnBuilder.TableColumnBuilder}
     */
    @Nonnull
    public TableColumn<S, String> build() {
        TableColumn<S, String> col = new TableColumn<>(columnName);
        col.setMinWidth(minWidth);
        col.setPrefWidth(prefWidth);
        col.setMaxWidth(maxWidth);
        col.setResizable(resizable);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(DeclarationsTableCell.forTableColumn(alignment));
        col.setOnEditCommit(value);
        return col;
    }
}
