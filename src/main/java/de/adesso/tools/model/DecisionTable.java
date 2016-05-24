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

package de.adesso.tools.model;

import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple4;
import javafx.collections.ObservableList;

import javax.annotation.Nonnull;

/**
 * Created by moehler on 23.05.2016.
 */
public class DecisionTable {
    private final Tuple4<
            ObservableList<ConditionDecl>,
            ObservableList<ActionDecl>,
            ObservableList<ObservableList<String>>,
            ObservableList<ObservableList<String>>> data;

    public DecisionTable(
            ObservableList<ObservableList<String>> conditionDefs,
            ObservableList<ConditionDecl> conditionDecls,
            ObservableList<ObservableList<String>> actionDefs,
            ObservableList<ActionDecl> actionDecls) {

        this.data = Tuple.of(conditionDecls, actionDecls, conditionDefs, actionDefs);
    }

    private DecisionTable(Builder builder) {
        this.data = Tuple.of(builder.conditionDecls, builder.actionDecls, builder.conditionDefs, builder.actionDefs);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull DecisionTable copy) {
        Builder builder = new Builder();
        builder.actionDecls = copy.data._2();
        builder.actionDefs = copy.data._4();
        builder.conditionDecls = copy.data._1();
        builder.conditionDefs = copy.data._3();
        return builder;
    }

    public ObservableList<ActionDecl> getActionDecls() {
        return data._2();
    }

    public ObservableList<ObservableList<String>> getActionDefs() {
        return data._4();
    }

    public ObservableList<ConditionDecl> getConditionDecls() {
        return data._1();
    }

    public ObservableList<ObservableList<String>> getConditionDefs() {
        return data._3();
    }

    public Tuple4<
            ObservableList<ConditionDecl>,
            ObservableList<ActionDecl>,
            ObservableList<ObservableList<String>>,
            ObservableList<ObservableList<String>>> getData() {
        return data;
    }

    /**
     * {@code DecisionTable} builder static inner class.
     */
    public static final class Builder {
        private ObservableList<ActionDecl> actionDecls;
        private ObservableList<ObservableList<String>> actionDefs;
        private ObservableList<ConditionDecl> conditionDecls;
        private ObservableList<ObservableList<String>> conditionDefs;

        private Builder() {
        }

        /**
         * Sets the {@code actionDecls} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code actionDecls} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder actionDecls(@Nonnull ObservableList<ActionDecl> val) {
            actionDecls = val;
            return this;
        }

        /**
         * Sets the {@code actionDefs} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code actionDefs} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder actionDefs(@Nonnull ObservableList<ObservableList<String>> val) {
            actionDefs = val;
            return this;
        }

        /**
         * Sets the {@code conditionDecls} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code conditionDecls} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder conditionDecls(@Nonnull ObservableList<ConditionDecl> val) {
            conditionDecls = val;
            return this;
        }

        /**
         * Sets the {@code conditionDefs} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code conditionDefs} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder conditionDefs(@Nonnull ObservableList<ObservableList<String>> val) {
            conditionDefs = val;
            return this;
        }

        /**
         * Returns a {@code DecisionTable} built from the parameters previously set.
         *
         * @return a {@code DecisionTable} built with parameters of this {@code DecisionTable.Builder}
         */
        @Nonnull
        public DecisionTable build() {
            return new DecisionTable(this);
        }
    }
}