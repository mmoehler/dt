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

package de.adesso.dtmg.io;

import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.stream.IntStream;

import static de.adesso.dtmg.exception.LambdaExceptionUtil.rethrowIntConsumer;
import static de.adesso.dtmg.exception.LambdaExceptionUtil.rethrowIntFunction;

/**
 * TODO Need a special state for the right processing of this data during the use cases new-, load- and save-file aand close app and ... ??
 * <p>
 * Created by mmoehler ofList 01.04.16.
 */
public class DtEntity implements Externalizable {
    private static final long serialVersionUID = -2022916876266547636L;
    private ObservableList<ObservableList<String>> conditionDefinitions;
    private ObservableList<ObservableList<String>> actionDefinitions;
    private ObservableList<ConditionDeclTableViewModel> conditionDeclarations;
    private ObservableList<ActionDeclTableViewModel> actionDeclarations;

    public DtEntity() {
        this.conditionDefinitions = FXCollections.observableArrayList();
        this.conditionDeclarations = FXCollections.observableArrayList();
        this.actionDeclarations = FXCollections.observableArrayList();
        this.actionDefinitions = FXCollections.observableArrayList();
    }

    public DtEntity(
            ObservableList<ConditionDeclTableViewModel> conditionDeclarations,
            ObservableList<ObservableList<String>> conditionDefinitions,
            ObservableList<ActionDeclTableViewModel> actionDeclarations,
            ObservableList<ObservableList<String>> actionDefinitions) {

        initConditionsData(conditionDeclarations, conditionDefinitions);
        initActionsData(actionDeclarations, actionDefinitions);
    }

    private void initActionsData(ObservableList<ActionDeclTableViewModel> actionDeclarations, ObservableList<ObservableList<String>> actionDefinitions) {
        this.actionDeclarations = actionDeclarations;
        this.actionDefinitions = actionDefinitions;
    }

    private void initConditionsData(ObservableList<ConditionDeclTableViewModel> conditionDeclarations, ObservableList<ObservableList<String>> conditionDefinitions) {
        this.conditionDeclarations = conditionDeclarations;
        this.conditionDefinitions = conditionDefinitions;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        final int crows = conditionDefinitions.size();
        final int arows = actionDefinitions.size();
        final int cols = conditionDefinitions.get(0).size();

        out.writeInt(crows);
        out.writeInt(arows);
        out.writeInt(cols);

        writeConditionsExternal(out, crows, cols);
        writeActionsExternal(out, arows, cols);
    }


    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int crows = in.readInt();
        final int arows = in.readInt();
        final int cols = in.readInt();

        readConditionsExternal(in, crows, cols);
        readActionsExternal(in, arows, cols);
    }

    private void writeConditionsExternal(ObjectOutput out, int rows, int cols) {

        IntStream.range(0, rows).forEach(rethrowIntConsumer(i -> {
            ConditionDecl model = conditionDeclarations.get(i).save().getModel();
            out.writeUTF(model.getLfdNr());
            out.writeUTF(model.getExpression());
            out.writeUTF(model.getPossibleIndicators());
        }));

        IntStream.range(0, rows)
                .forEach(rethrowIntConsumer(i -> IntStream.range(0, cols)
                        .forEach(rethrowIntConsumer(j -> out.writeUTF(conditionDefinitions.get(i).get(j))))));
    }

    private void readConditionsExternal(ObjectInput in, int rows, int cols) {
        IntStream.range(0, rows).mapToObj(rethrowIntFunction(i -> {
            final String lfdnr = in.readUTF();
            final String expr = in.readUTF();
            final String posind = in.readUTF();
            return new ConditionDeclTableViewModel(new ConditionDecl(lfdnr, expr, posind));
        })).forEach(k -> conditionDeclarations.add(k));

        IntStream.range(0, rows).forEach(rethrowIntConsumer(i -> {
            conditionDefinitions.add(FXCollections.observableArrayList());
            IntStream.range(0, cols).forEach(rethrowIntConsumer(j -> conditionDefinitions.get(i).add(in.readUTF())));
        }));
    }

    private void readActionsExternal(ObjectInput in, int rows, int cols) {
        IntStream.range(0, rows).mapToObj(rethrowIntFunction(i -> {
            final String lfdnr = in.readUTF();
            final String expr = in.readUTF();
            final String posind = in.readUTF();

            return new ActionDeclTableViewModel(new ActionDecl(lfdnr, expr, posind));
        })).forEach(k -> actionDeclarations.add(k));

        IntStream.range(0, rows).forEach(rethrowIntConsumer(i -> {
            actionDefinitions.add(FXCollections.observableArrayList());
            IntStream.range(0, cols)
                    .forEach(rethrowIntConsumer(j -> actionDefinitions.get(i).add(in.readUTF())));
        }));
    }

    private void writeActionsExternal(ObjectOutput out, int rows, int cols) {

        IntStream.range(0, rows).forEach(rethrowIntConsumer(i -> {
            ActionDecl model = actionDeclarations.get(i).save().getModel();
            out.writeUTF(model.getLfdNr());
            out.writeUTF(model.getExpression());
            out.writeUTF(model.getPossibleIndicators());
        }));

        IntStream.range(0, rows)
                .forEach(rethrowIntConsumer(i -> IntStream.range(0, cols)
                        .forEach(rethrowIntConsumer(j -> {
                            out.writeUTF(actionDefinitions.get(i).get(j));
                        }))));
    }

    public ObservableList<ObservableList<String>> getConditionDefinitions() {
        return conditionDefinitions;
    }

    public ObservableList<ActionDeclTableViewModel> getActionDeclarations() {
        return actionDeclarations;
    }

    public ObservableList<ObservableList<String>> getActionDefinitions() {
        return actionDefinitions;
    }

    public ObservableList<ConditionDeclTableViewModel> getConditionDeclarations() {
        return conditionDeclarations;
    }

    public void become(DtEntity other) {
        this.conditionDeclarations.clear();
        other.conditionDeclarations.forEach(this.conditionDeclarations::add);
        this.conditionDefinitions.clear();
        other.conditionDefinitions.forEach(this.conditionDefinitions::add);
        this.actionDeclarations.clear();
        other.actionDeclarations.forEach(this.actionDeclarations::add);
        this.actionDefinitions.clear();
        other.actionDefinitions.forEach(this.actionDefinitions::add);
    }
}
