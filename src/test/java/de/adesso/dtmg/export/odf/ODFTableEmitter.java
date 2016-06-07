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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by moehler on 07.06.2016.
 */
public class ODFTableEmitter implements BiFunction<TableContainer, List<List<String>>, Table> {

    private Function<List<List<String>>, String[][]> internal = lists ->
            lists.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);

    public static Table emit(TableContainer tableContainer, List<List<String>> data) {
        final ODFTableEmitter emitter = new ODFTableEmitter();
        return emitter.apply(tableContainer, data);
    }

    @Override
    public Table apply(TableContainer tableContainer, List<List<String>> lists) {
        String[][] data = internal.apply(lists);
        String[] colheader = IntStream.range(0, data[0].length).mapToObj(i -> String.format("R%02d", i)).toArray(String[]::new);
        String[] rowheader = IntStream.range(0, data.length).mapToObj(i -> String.format("C%02d", i)).toArray(String[]::new);
        return Table.newTable(tableContainer, rowheader, colheader, data);
    }
}
