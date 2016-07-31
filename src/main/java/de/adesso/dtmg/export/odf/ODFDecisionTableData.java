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

import de.adesso.dtmg.common.builder.Callback;
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
public class ODFDecisionTableData {
    public static final Object NO_ARG = new Object();
    String[][] data;
    String[] conditionDeclsHeader;
    String[] actionDeclsHeader;
    String[] rowheader;

    public String[] getActionDeclsHeader() {
        return actionDeclsHeader;
    }

    public String[][] getData() {
        return data;
    }

    public String[] getRowheader() {
        return rowheader;
    }

    public String[] getConditionDeclsHeader() {
        return conditionDeclsHeader;
    }

    // --


    private ODFDecisionTableData(Builder builder) {
        // TODO Open Issue - Who gives me the different header informations??!!



    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        String[][] data;
        String[] conditionDeclsHeader;
        String[] actionDeclsHeader;
        String[] conditionDefsHeader;
        String[] actionDefsHeader;

        String[] rowheader;

        String[][] conditionDecls;
        String[][] conditionDefs;
        String[][] actionDecls;
        String[][] actionDefs;

        private Function<ObservableList<ObservableList<String>>, String[][]> array2D = lists ->
                lists.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);


        HeaderBuilder conditionDeclsHeaderBuilder = new HeaderBuilder(ODFDecisionTableData.Builder.this, new Callback<String[]>(){
            @Override
            public void call(String[] values) {
                ODFDecisionTableData.Builder.this.setConditionDeclsHeader(values);
            }
        });
        HeaderBuilder actionDeclHeaderBuilder = new HeaderBuilder(ODFDecisionTableData.Builder.this, new Callback<String[]>(){
            @Override
            public void call(String[] values) {
                ODFDecisionTableData.Builder.this.setActionDeclsHeader(values);
            }
        });

        private Builder() {
        }

        public HeaderBuilder conditionDeclsHeader() {
            return this.conditionDeclsHeaderBuilder;
        }

        public Builder conditionDefs(ObservableList<ObservableList<String>> val) {
            conditionDefs = array2D.apply(val);
            return this;
        }

        public Builder conditionDecls(ObservableList<ConditionDeclTableViewModel> val) {
            Function<ConditionDecl,String> f[] = newArray(3,
                    x -> x.getLfdNr(),
                    x -> x.getExpression(),
                    x -> x.getPossibleIndicators());

            conditionDecls = val.stream()
                    .map(m -> IntStream.range(0,f.length)
                            .mapToObj(i -> f[i].apply(m.getModel()))
                            .toArray(String[]::new)
                    ).toArray(String[][]::new);
            return this;
        }

        public Builder conditionDefsHeader(ObservableList<String> val) {
            conditionDefsHeader = IntStream.range(0, val.size()).mapToObj(i -> String.format("%02d", i)).toArray(String[]::new);
            return this;
        }

        public HeaderBuilder actionDeclsHeader() {
            return actionDeclHeaderBuilder;
        }

        public Builder actionDecls(ObservableList<ActionDeclTableViewModel> val) {
            Function<ActionDecl,String> f[] = newArray(3,
                    x -> x.getLfdNr(),
                    x -> x.getExpression(),
                    x -> x.getPossibleIndicators());

            actionDecls = val.stream()
                    .map(m -> IntStream.range(0,f.length)
                            .mapToObj(i -> f[i].apply(m.getModel()))
                            .toArray(String[]::new)
            ).toArray(String[][]::new);

            return this;
        }

        public Builder actionDefs(ObservableList<ObservableList<String>> val) {
            actionDefs = array2D.apply(val);
            return this;
        }

        public Builder actionDefsHeader(ObservableList<String> val) {
            actionDefsHeader = IntStream.range(0, val.size()).mapToObj(i -> String.format("%02d", i)).toArray(String[]::new);
            return this;
        }

        // -- internal setter ---------------------------------------

        void setActionDeclsHeader(String[] actionDeclsHeader) {
            this.actionDeclsHeader = actionDeclsHeader;
        }

        void setConditionDeclsHeader(String[] conditionDeclsHeader) {
            this.conditionDeclsHeader = conditionDeclsHeader;
        }

        public ODFDecisionTableData build() {
            return new ODFDecisionTableData(this);
        }

        // -- private stuff -----------------------------------------

        @SafeVarargs
        static <E> E[] newArray(int length, E... array) {
            return Arrays.copyOf(array, length);
        }

    }
}
