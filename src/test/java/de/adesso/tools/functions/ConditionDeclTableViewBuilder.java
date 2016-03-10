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

import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by mmoehler on 06.03.16.
 */
public class ConditionDeclTableViewBuilder extends DeclarationsTableViewBuilder<ConditionDeclTableViewModel> {

    final List<ConditionDeclTableViewModel> items = FXCollections.observableArrayList();

    public ConditionDeclTableViewBuilder() {
    }

    public ConditionDeclBuilder<ConditionDeclTableViewBuilder> addTableViewModelWithLfdNbr(String number) {
        return new ConditionDeclBuilder<>(number, this,
                (lfdNr, expression, possibleIndicators) -> internHandleCallback(new ConditionDecl(lfdNr, expression, possibleIndicators)));
    }


    @Override
    public TableViewBuilder withSelectionAt(int row, int col) {
        return super.withSelectionAt(row, col);
    }


    private void internHandleCallback(ConditionDecl decl) {
        if(null == this.tableView) {
            this.tableView = new TableView<>(FXCollections.observableArrayList());
        }
        tableView.getItems().add(new ConditionDeclTableViewModel(decl));
    }

    @Nonnull
    @Override
    public TableView<ConditionDeclTableViewModel> build() {
        return super.build();
    }

    @Override
    protected void buildResult(int r, int c, String d) {
        ObservableList<ConditionDeclTableViewModel> tableViewData = FXCollections.observableArrayList();
        tableView = new TableView<>(tableViewData);
        IntStream.rangeClosed(1, c).forEach(i -> {
            TableColumn<ConditionDeclTableViewModel, String> tc = new TableColumn<>(String.format("R%02d", i));
            tableView.getColumns().add(tc);
        });

    }

    public interface Callback {
        void handleCallback(List<ConditionDeclTableViewModel> conditionDecls);
    }

}
