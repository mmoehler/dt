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

import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.functions.chained.first.AbstractSubBuilder;
import de.adesso.tools.functions.chained.first.Callback;
import javafx.collections.ObservableList;

import static de.adesso.tools.common.MatrixBuilder.observable;

/**
 * Created by mmoehler ofList 06.03.16.
 */
class DefinitionsTableViewDataBuilder<C> extends AbstractSubBuilder<ObservableList<ObservableList<String>>, C> {
    private final int rows;
    private final int cols;
    private String data;

    public DefinitionsTableViewDataBuilder(int rows, int cols, C caller, Callback<ObservableList<ObservableList<String>>> callback) {
        super(caller, callback);
        this.rows = rows;
        this.cols = cols;
    }

    public C data(String data) {
        this.data = data;
        getCallback().call(build());
        return this.caller;
    }

    @Override
    public ObservableList<ObservableList<String>> build() {
        ObservableList<ObservableList<String>> tableViewData = observable(MatrixBuilder.matrixOf(data).dim(rows, cols).build());
        return tableViewData;
    }
}
