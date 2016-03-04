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

import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.PossibleIndicatorsSupplier;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test fixtures of the DtFunctionsTest's
 * Created by moehler on 02.03.2016.
 */
public class DtFunctionsTestData {

    public static ListOfIndicatorSuppliersBuilder listOfIndicatorSupliersBuilder() {
        return ListOfIndicatorSuppliersBuilder.newBuilder();
    }

    public static ConditionDeclTableViewModelListBuilder conditionDeclTableViewModelListBuilder() {
        return new ConditionDeclTableViewModelListBuilder();
    }

    public static ActionDeclTableViewModelListBuilder actionDeclTableViewModelListBuilder() {
        return new ActionDeclTableViewModelListBuilder();
    }


    static class ListOfIndicatorSuppliersBuilder {
        List<PossibleIndicatorsSupplierBuilder> builders = new LinkedList<>();

        public static ListOfIndicatorSuppliersBuilder newBuilder() {
            return new ListOfIndicatorSuppliersBuilder();
        }

        public ListOfIndicatorSuppliersBuilder add(String indicators) {
            builders.add(PossibleIndicatorsSupplierBuilder.newBuilder().withIndicators(indicators));
            return this;
        }

        public List<PossibleIndicatorsSupplier> build() {
            return builders.stream().map(PossibleIndicatorsSupplierBuilder::build).collect(Collectors.toList());
        }
    }

    static class PossibleIndicatorsSupplierBuilder {
        private String indicatorsString;

        public static PossibleIndicatorsSupplierBuilder newBuilder() {
            return new PossibleIndicatorsSupplierBuilder();
        }

        public PossibleIndicatorsSupplierBuilder withIndicators(String indicators) {
            this.indicatorsString = indicators;
            return this;
        }

        public PossibleIndicatorsSupplier build() {
            return () -> new ReadOnlyStringWrapper(indicatorsString);
        }

    }

    static class ConditionDeclTableViewModelListBuilder  {
        List<ConditionDeclTableViewModel> list = new ArrayList<>();

        public ConditionDeclTableViewModelListBuilder() {
            super();
        }

        public ConditionDeclBuilder<ConditionDeclTableViewModelListBuilder> addTableViewModelWithLfdNbr(String number) {
            return new ConditionDeclBuilder<>(number, this,
                    (lfdNr, expression, possibleIndicators) -> _addTableViewModel(new ConditionDecl(lfdNr, expression, possibleIndicators)));
        }

        protected void _addTableViewModel(ConditionDecl element) {
            list.add(new ConditionDeclTableViewModel(element));
        }

        public List<ConditionDeclTableViewModel> build(){
            return list;
        }
    }

    static class ConditionDeclBuilder<T> {
        private final String lfdNr;
        private final T caller;
        private final ConditionDeclBuilderCallback callback;
        private String expression;

        public ConditionDeclBuilder(String lfdNr, T caller, ConditionDeclBuilderCallback callback) {
            this.lfdNr = lfdNr;
            this.caller = caller;
            this.callback = callback;
        }

        public ConditionDeclBuilder<T> withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public T withIndicators(String possibleIndicators) {
            this.callback.addTableViewModel(this.lfdNr, this.expression, possibleIndicators);
            return this.caller;
        }
    }

    static class ActionDeclTableViewModelListBuilder  {
        List<ActionDeclTableViewModel> list = new ArrayList<>();

        public ActionDeclTableViewModelListBuilder() {
            super();
        }

        public ActionDeclBuilder<ActionDeclTableViewModelListBuilder> addTableViewModelWithLfdNbr(String number) {
            return new ActionDeclBuilder<>(number, this,
                    (lfdNr, expression, possibleIndicators) -> _addTableViewModel(new ActionDecl(lfdNr, expression, possibleIndicators)));
        }

        protected void _addTableViewModel(ActionDecl element) {
            list.add(new ActionDeclTableViewModel(element));
        }

        public List<ActionDeclTableViewModel> build(){
            return list;
        }
    }

    static class ActionDeclBuilder<T> {
        private final String lfdNr;
        private final T caller;
        private final ActionDeclBuilderCallback callback;
        private String expression;

        public ActionDeclBuilder(String lfdNr, T caller, ActionDeclBuilderCallback callback) {
            this.lfdNr = lfdNr;
            this.caller = caller;
            this.callback = callback;
        }

        public ActionDeclBuilder<T> withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public T withIndicators(String possibleIndicators) {
            this.callback.addTableViewModel(this.lfdNr, this.expression, possibleIndicators);
            return this.caller;
        }
    }

}

interface ConditionDeclBuilderCallback {
    void addTableViewModel(String lfdNr, String expression, String possibleIndicators);
}

interface ActionDeclBuilderCallback {
    void addTableViewModel(String lfdNr, String expression, String possibleIndicators);
}
