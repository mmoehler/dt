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

package de.adesso.dtmg.export.odf;

import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableContainer;

import java.util.function.BiFunction;

/**
 * Created by moehler on 07.06.2016.
 */
public class OdtTableEmitter implements BiFunction<TableContainer, OdtDecisionTableData, Table> {

    public static Table emit(TableContainer tableContainer, OdtDecisionTableData data) {
        final OdtTableEmitter emitter = new OdtTableEmitter();
        return emitter.apply(tableContainer, data);
    }

    @Override
    public Table apply(TableContainer tableContainer, OdtDecisionTableData data) {
        String[] colheader = null;
        String[] rowheader = null;
        String[][] tableData = data.getData();
        return Table.newTable(tableContainer, rowheader, colheader, tableData);
    }
}
