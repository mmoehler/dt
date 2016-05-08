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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by mmoehler ofList 27.03.16.
 */
public class Adapters {

    public static class Matrix {

        public static Function<ObservableList<ObservableList<String>>, List<List<String>>> adaptObservableList2List =
                (observableLists) -> observableLists.stream()
                        .map(l -> l.stream()
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());
        public static Function<List<List<String>>, ObservableList<ObservableList<String>>> adaptList2ObservableList =
                (lists) -> lists.stream()
                        .map(l -> l.stream()
                                .collect(Collectors.toCollection(FXCollections::observableArrayList)))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

        public static List<List<String>> adapt(ObservableList<ObservableList<String>> adaptee) {
            return adaptObservableList2List.apply(adaptee);
        }

        public static ObservableList<ObservableList<String>> adapt(List<List<String>> adaptee) {
            return adaptList2ObservableList.apply(adaptee);
        }
    }

    public static class Lists {

        public static <T> List<T> adapt(ObservableList<T> adaptee) {
            return adaptee.stream().collect(Collectors.toList());
        }

        public static <T> ObservableList<T> adapt(List<T> adaptee) {
            return adaptee.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

    }


}
