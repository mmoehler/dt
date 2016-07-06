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

package de.adesso.dtmg.functions;

import de.adesso.dtmg.Dump;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.dtmg.common.builder.ObservableList2DBuilder.observable2DOf;
import static de.adesso.dtmg.functions.MoreCollectors.toObservableList;
import static de.adesso.dtmg.functions.MoreCollectors.toSingleObject;
import static java.util.stream.IntStream.range;

/**
 * Created by mmoehler ofList 05.05.16.
 */
public class ObservableList2DFunctions {

    // -- consumer stuff

    // -- row stuff

    public static Function<ObservableList<String>, ObservableList<String>> addRow(Supplier<List<String>> rowSupplier) {
        return null;
    }

    // -- column stuff

    public static Function<ObservableList<String>, ObservableList<String>> replaceColumn(List<String> newData, int pos) {
        return new ReplaceColumn(newData.iterator(), pos);
    }

    public static Function<ObservableList<String>, ObservableList<String>> insertColumn(String defaultData, int pos) {
        return new InsertColumn(defaultData, pos);
    }

    public static Function<ObservableList<String>, ObservableList<String>> removeColumn(int pos) {
        return new RemoveColumn(pos);
    }

    public static Function<ObservableList<String>, ObservableList<String>> moveColumn(int oldPos, int newPos) {
        return new MoveColumn(oldPos, newPos);
    }

    public static Function<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> transpose() {
        return m -> {
            if(m.isEmpty()) return m;
            return range(0, m.get(0).size())
                    .mapToObj(r ->range(0, m.size())
                            .mapToObj(c -> m.get(c).get(r))
                            .collect(toObservableList()))
                    .collect(toObservableList());
        };
    }


    // -----------------

    // -----------------

    static class AddColumn extends AbstractObservableList2DOperator {

        public AddColumn() {
            super(0);
        }

        @Override
        public ObservableList<String> apply(ObservableList<String> strings) {
            ObservableList<String> intern = FXCollections.observableArrayList(strings);
            intern.add("?");
            return intern;
        }
    }


    static class MoveColumn extends AbstractObservableList2DOperator {

        private final int newPos;

        public MoveColumn(int oldPos, int newPos) {
            super(oldPos);
            this.newPos = newPos;
        }

        @Override
        public ObservableList<String> apply(ObservableList<String> strings) {
            ObservableList<String> intern = FXCollections.observableArrayList(strings);
            intern.add(newPos, intern.remove(positions[0]));
            return intern;
        }
    }

    static class RemoveColumn extends AbstractObservableList2DOperator {

        public RemoveColumn(int... morePos) {
            super(morePos);
        }

        @Override
        public ObservableList<String> apply(ObservableList<String> strings) {
            Set<Integer> set = Arrays.stream(positions).boxed().collect(Collectors.toSet());
            return IntStream.range(0, strings.size())
                    .filter(x -> !set.contains(x))
                    .mapToObj(strings::get)
                    .collect(toObservableList());
        }


    }


    static class InsertColumn extends AbstractObservableList2DOperator {

        private final String defaultData;

        public InsertColumn(String defaultData, int pos) {
            super(pos);
            this.defaultData = defaultData;
        }

        @Override
        public ObservableList<String> apply(ObservableList<String> strings) {
            ObservableList<String> intern = FXCollections.observableArrayList(strings);
            intern.add(positions[0], defaultData);
            return intern;
        }
    }

    static class ReplaceColumn extends AbstractObservableList2DOperator {

        private final Iterator<String> newData;

        public ReplaceColumn(Iterator<String> newData, int pos) {
            super(pos);
            this.newData = newData;
        }

        @Override
        public ObservableList<String> apply(ObservableList<String> strings) {
            ObservableList<String> intern = FXCollections.observableArrayList(strings);
            intern.remove(positions[0]);
            intern.add(positions[0], newData.next());
            return intern;
        }
    }


    public static void main(String[] args) {
        ObservableList<ObservableList<String>> M0 = observable2DOf("A,A,A,B,B,B,C,C,C").dim(3, 3).build();

        Dump.dumpTableItems("ORIGINAL", M0);

        ObservableList<ObservableList<String>> transposed = Stream.of(M0).map(transpose()).collect(toSingleObject());

        Dump.dumpTableItems("GROUP BY", transposed);


    }
}
