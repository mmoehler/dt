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

import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by mmoehler on 31.07.16.
 */
public class OdtDecisionTableData {
    public static final Object NO_ARG = new Object();
    private String[][] data;

    public String[][] getData() {
        return data;
    }


    // --
    private OdtDecisionTableData(Builder builder) {
        String[] colheader = concat(builder.conditionDeclsHeader, builder.conditionDefsHeader);
        String[] actionsHeader = Arrays.copyOf(colheader, colheader.length);
        String conditions[][] = new String[builder.conditionDecls.length][];
        for (int i = 0; i < builder.conditionDecls.length; i++) {
            conditions[i] = concat(builder.conditionDecls[i], builder.conditionDefs[i]);
        }
        String actions[][] = new String[builder.actionDecls.length][];
        for (int i = 0; i < builder.actionDecls.length; i++) {
            actions[i] = concat(builder.actionDecls[i], builder.actionDefs[i]);
        }

        this.data = new String[conditions.length+actions.length+2][];
        System.arraycopy(conditions,0,this.data,1,conditions.length);
        System.arraycopy(actions,0,this.data,conditions.length+2, actions.length);
        this.data[0] = colheader;
        this.data[conditions.length+1] = actionsHeader;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        public static final String STR_E = "E";
        public static final String STR_ELSE = "ELSE";
        public static final String DCL_HEADER[] = {"#", "Expression", "Indicators"};
        String[] conditionDeclsHeader;
        String[] actionDeclsHeader;
        String[] conditionDefsHeader;
        String[] actionDefsHeader;

        String[][] conditionDecls;
        String[][] conditionDefs;
        String[][] actionDecls;
        String[][] actionDefs;

        private Function<ObservableList<ObservableList<String>>, String[][]> array2D = lists ->
                lists.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);


        private Builder() {
        }

        public Builder conditionDefs(ObservableList<ObservableList<String>> val) {
            conditionDefs = array2D.apply(val);
            conditionDefsHeader = IntStream.range(0, val.get(0).size()).mapToObj(i -> String.format("R%02d", (i+1))).toArray(String[]::new);
            if(hasElseRule(val.get(0))) {
                setElseRuleHeader(conditionDefsHeader);
            }
            return this;
        }

        public Builder conditionDecls(ObservableList<ConditionDeclTableViewModel> val) {
            Function<ConditionDecl, String> f[] = newArray(3,
                    x -> x.getLfdNr(),
                    x -> x.getExpression(),
                    x -> x.getPossibleIndicators());

            conditionDecls = val.stream()
                    .map(m -> IntStream.range(0, f.length)
                            .mapToObj(i -> f[i].apply(m.getModel()))
                            .toArray(String[]::new)
                    ).toArray(String[][]::new);

            conditionDeclsHeader = Arrays.copyOf(DCL_HEADER, DCL_HEADER.length);
            return this;
        }

        public Builder actionDecls(ObservableList<ActionDeclTableViewModel> val) {
            Function<ActionDecl, String> f[] = newArray(3,
                    x -> x.getLfdNr(),
                    x -> x.getExpression(),
                    x -> x.getPossibleIndicators());

            actionDecls = val.stream()
                    .map(m -> IntStream.range(0, f.length)
                            .mapToObj(i -> f[i].apply(m.getModel()))
                            .toArray(String[]::new)
                    ).toArray(String[][]::new);

            actionDeclsHeader = Arrays.copyOf(DCL_HEADER, DCL_HEADER.length);

            return this;
        }

        public Builder actionDefs(ObservableList<ObservableList<String>> val) {
            actionDefs = array2D.apply(val);
            actionDefsHeader = IntStream.range(0, val.get(0).size()).mapToObj(i -> String.format("R%02d", (i+1))).toArray(String[]::new);
            if(hasElseRule(val.get(0))) {
                setElseRuleHeader(actionDefsHeader);
            }
            return this;
        }

        public OdtDecisionTableData build() {
            return new OdtDecisionTableData(this);
        }

        // -- private stuff -----------------------------------------

        @SafeVarargs
        static <E> E[] newArray(int length, E... array) {
            return Arrays.copyOf(array, length);
        }

        static boolean hasElseRule(ObservableList<String> def) {
            return STR_E.equals(def.get(def.size() - 1));
        }

        static void setElseRuleHeader(String[] header) {
            header[header.length-1]=STR_ELSE;
        }
    }

    static String[][] concat(String[][] data) {
        return Arrays.stream(data).map(a -> Arrays.copyOf(a, a.length)).toArray(String[][]::new);
    }

    static <T> T[] concat(T[] left, T[] right) {
        T[] copyOf = Arrays.copyOf(left, left.length + right.length);
        System.arraycopy(right,0,copyOf,left.length,right.length);
        return copyOf;
    }

}
