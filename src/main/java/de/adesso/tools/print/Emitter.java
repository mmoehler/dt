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

package de.adesso.tools.print;

import com.codepoetics.protonpack.StreamUtils;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.model.Declaration;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple4;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.row.ContentRow;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 21.05.16.
 */
public class Emitter implements Function<Tuple4<
        ObservableList<ConditionDecl>,
        ObservableList<ActionDecl>,
        ObservableList<ObservableList<String>>,
        ObservableList<ObservableList<String>>
        >
        , V2_AsciiTable> {

    private final static Object[] RULE_DESCRIPTOR = {};

    @Override
    public V2_AsciiTable apply(Tuple4<ObservableList<ConditionDecl>, ObservableList<ActionDecl>,
            ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> dt) {

        Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>> conData = Tuple.of(dt._1(), dt._3());
        Tuple2<ObservableList<ActionDecl>, ObservableList<ObservableList<String>>> actData = Tuple.of(dt._2(), dt._4());

        V2_AsciiTable table = new V2_AsciiTable();

        table.addStrongRule();
        emitConditionsHeader(conData._2())
                .map(addRowsAndRules(table))
                .forEach(o -> o.ifPresent(p -> p.setAlignment("cllcccc".toCharArray())));
        table.addStrongRule();

        emitConditions().apply(conData)
                .map(addRowsAndRules(table))
                .forEach(o -> o.ifPresent(p -> p.setAlignment("cllcccc".toCharArray())));

        table.addStrongRule();
        emitActionsHeader(actData._2())
                .map(addRowsAndRules(table))
                .forEach(o -> o.ifPresent(p -> p.setAlignment("cllcccc".toCharArray())));
        table.addStrongRule();

        emitActions().apply(actData)
                .map(addRowsAndRules(table))
                .forEach(o -> o.ifPresent(p -> p.setAlignment("cllcccc".toCharArray())));


        table.addStrongRule();
        return table;
    }

    Function<Object[], Optional<ContentRow>> addRowsAndRules(V2_AsciiTable table) {
        return d -> {
            ContentRow r = null;
            if (d.length == 0) {
                table.addRule();
            } else {
                r = table.addRow(d);
            }
            return Optional.ofNullable(r);
        };
    }

    public Function<Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>>, Stream<Object[]>> emitConditions() {
        return (c) -> StreamUtils.zip(c._1().stream(), c._2().stream(), (l, r) -> Tuple.of(l, r))
                .map(t -> emitCondition(t));

    }

    public Object[] emitCondition(Tuple2<ConditionDecl, ObservableList<String>> t) {
        return emitDecl(t);
    }

    private <D extends Declaration> Object[] emitDecl(Tuple2<D, ObservableList<String>> t) {
        Object[] aPart = t._1().toArray();
        Object[] bPart = t._2().toArray();
        Object[] ret = Arrays.copyOf(aPart, aPart.length + bPart.length);
        System.arraycopy(bPart, 0, ret, aPart.length, bPart.length);
        return ret;
    }

    public Function<Tuple2<ObservableList<ActionDecl>, ObservableList<ObservableList<String>>>, Stream<Object[]>> emitActions() {
        return (c) -> StreamUtils.zip(c._1().stream(), c._2().stream(), (l, r) -> Tuple.of(l, r))
                .map(t -> emitAction(t));
    }

    public Object[] emitAction(Tuple2<ActionDecl, ObservableList<String>> t) {
        return emitDecl(t);
    }

    public Stream<Object[]> emitConditionsHeader(ObservableList<ObservableList<String>> t) {
        return emitHeader("CONDITIONS", t.get(0));
    }

    private Stream<Object[]> emitHeader(String title, ObservableList<String> t) {
        Object[] mainHeader = new Object[3 + t.size()];
        mainHeader[mainHeader.length - 1] = title;
        Object[] subHeader = new Object[3 + t.size()];
        subHeader[0] = "#";
        subHeader[1] = "Description";
        subHeader[2] = "Indicators";
        for (int i = 0; i < t.size(); i++) {
            subHeader[i + 3] = String.format("R%02d", i + 1);
        }
        return Stream.of(mainHeader, RULE_DESCRIPTOR, subHeader);
    }

    public Stream<Object[]> emitActionsHeader(ObservableList<ObservableList<String>> t) {
        return emitHeader("ACTIONS", t.get(0));
    }
}