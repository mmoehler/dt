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

package de.adesso.tools.functions.fixtures;

import de.adesso.tools.functions.chained.first.AbstractSubBuilder;
import de.adesso.tools.functions.chained.first.Callback;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.annotation.Nonnull;

/**
 * Created by moehler ofList 10.03.2016.
 */

/**
 * {@code TableColumnBuilder} TableColumnBuilder static inner class.
 */
public class TableColumnBuilder<C> extends AbstractSubBuilder<TableColumn, C> {
    private String columnName;
    private String propertyName;
    private C caller;
    private Callback<TableColumnBuilder> callback;


    public TableColumnBuilder(String columnName, C caller, Callback<TableColumn> callback) {
        super(caller, callback);
        this.columnName = columnName;
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
    public C propertyName(@Nonnull String val) {
        propertyName = val;
        getCallback().call(build());
        return getCaller();

    }


    /**
     * Returns a {@code TableColumnBuilder} built from the parameters previously set.
     *
     * @return a {@code TableColumnBuilder} built with parameters of this {@code TableColumnBuilder.TableColumnBuilder}
     */
    @Nonnull
    public TableColumn build() {
        TableColumn col = new TableColumn<>(columnName);
        if (null != propertyName) col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return col;
    }
}
