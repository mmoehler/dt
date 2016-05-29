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
import com.google.common.collect.Lists;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.model.DecisionTable;
import de.adesso.tools.model.Declaration;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 21.05.16.
 */
public class Emitter implements Function<DecisionTable, AsciiTable> {

    private static final String SPACE = " ";
    private static final String ASTERISK = "*";

    @Override
    public AsciiTable apply(DecisionTable dt) {

        Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>> conData
                = Tuple.of(dt.getConditionDecls(), dt.getConditionDefs());

        Tuple2<ObservableList<ActionDecl>, ObservableList<ObservableList<String>>> actData
                = Tuple.of(dt.getActionDecls(), dt.getActionDefs());

        AsciiTable consumer = new AsciiTable();

        emitHeader().apply(conData).forEach(consumer.getHeaderRows());

        emitConditions().apply(conData).forEach(consumer.getConditionRows());

        emitActions().apply(actData).forEach(consumer.getActionRows());

        return consumer;
    }

    Function<Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>>, Stream<AsciiRow>> emitConditions() {
        int i[] = {1};
        return (c) -> StreamUtils.zip(c._1().stream(), c._2().stream(), (l, r) -> Tuple.of(l, r))
                .map(t -> emitRow(i[0]++, t));

    }

    Function<Tuple2<ObservableList<ActionDecl>, ObservableList<ObservableList<String>>>, Stream<AsciiRow>> emitActions() {
        int i[] = {101};
        return (c) -> StreamUtils.zip(c._1().stream(), c._2().stream(), (l, r) -> Tuple.of(l, r))
                .map(t -> emitRow(i[0]++, t));
    }

    static <D extends Declaration> AsciiRow emitRow(int nbr, Tuple2<D, ObservableList<String>> t) {
        AsciiRow ret = new AsciiRow();
        List<String> theDecl = Lists.newArrayList(ASTERISK, t._1().getExpression(), String.valueOf(nbr), ASTERISK);
        ret.addAll(theDecl);
        ret.addAll(t._2());
        return ret;
    }

    Function<Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>>, Stream<AsciiRow>> emitHeader() {
        return (tuple) -> {
            ObservableList<String> t = tuple._2().get(0);
            AsciiRow mainHeader = new AsciiRow(4 + t.size());
            AsciiRow subHeader = new AsciiRow(4 + t.size());
            int k = 1;
            for (int i = 0; i < (4 + t.size()); i++) {
                if (i < 4) {
                    mainHeader.add(SPACE);
                    subHeader.add(SPACE);
                } else {
                    int ruleNumber = k % 10;
                    int decCounter = k / 10;
                    k++;
                    String sRuleNumber = String.valueOf(ruleNumber);
                    String sDecCounter = (decCounter == 0 || ruleNumber != 0) ? " " : String.valueOf(decCounter);

                    mainHeader.add(sDecCounter);
                    subHeader.add(sRuleNumber);
                }
            }
            return Stream.of(mainHeader, subHeader);
        };
    }

}