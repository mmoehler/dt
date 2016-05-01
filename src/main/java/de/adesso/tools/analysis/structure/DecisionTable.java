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

package de.adesso.tools.analysis.structure;

import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

/**
 * Created by mmoehler on 01.05.16.
 */
public class DecisionTable implements Consumer<Tuple2<ObservableList<ObservableList<String>>,ObservableList<ObservableList<String>>>> {

    private final Tuple2<ObservableList<ObservableList<String>>,ObservableList<ObservableList<String>>> oldData;
    private final int oldColumns;


    public DecisionTable(Tuple2<ObservableList<ObservableList<String>>,ObservableList<ObservableList<String>>> dtData) {
        this.oldData = dtData;
        this.oldColumns = (dtData._1().isEmpty()) ? 0 : dtData._1().get(0).size();
    }

    @Override
    public void accept(Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> newDtData) {

        if(newDtData._1().isEmpty()) return;

        final int newColumns = newDtData._1().get(0).size();
        if(newColumns == oldColumns) {
            oldData._1().clear();
            newDtData._1().forEach(i -> oldData._2().add(i));
            oldData._2().clear();
            newDtData._2().forEach(i -> oldData._2().add(i));
        } else if(newColumns < oldColumns) {

        } else { // newColumns < oldColumns

        }
    }


}
