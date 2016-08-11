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

package de.adesso.dtmg.analysis;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.stream.IntStream;

/**
 * Created by moehler on 09.08.2016.
 */
public class TestObservableList2DValueChanged {

    @Test
    public void valueChangedTest() {

        ListChangeListener<String> inner = new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                System.out.println("inner = " + c);
            }
        };

        ListChangeListener<ObservableList<String>> outer = new ListChangeListener<ObservableList<String>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<String>> c) {
                System.out.println("outer = " + c);
            }
        };


        ObservableList<ObservableList<String>> l = FXCollections.observableArrayList();
        IntStream.range(0,10).forEach(i -> {

            ObservableList<String> ii = FXCollections.observableArrayList();
            ii.addListener(inner);
            l.add(ii);

        });
        l.addListener(outer);


        for (int i = 0; i < 10; i++) {
            ObservableList<String> ol = l.get(i);
            for (int j = 0; j < 10; j++) {
                ol.add("0");
            }
        }

        for (int i = 0; i < 10; i++) {
            l.get(i).set(i,"1");
        }

    }


}
