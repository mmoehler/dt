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

package de.adesso.dtmg.ui.editor;

import de.adesso.dtmg.exception.ExceptionHandler;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.inject.Inject;

public class EditorDialog implements FxmlView<EditorDialogModel> {
    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    @FXML
    private TextArea textArea;

    @InjectViewModel
    private EditorDialogModel viewModel;

    @Inject
    private Stage primaryStage;

    @Inject
    private ExceptionHandler exceptionHandler;

    private String text;


    private Stage showDialog;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.textArea.setText(text);
    }

    public void editorOnOk(ActionEvent actionEvent) {
        this.text = textArea.getText();
        viewModel.handleEditorOnOK(this.text);
        this.showDialog.close();
    }

    public void initialize() {
        viewModel.subscribe("", (key, payload) -> {
            showDialog.close();
        });
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
    }




    public void editorOnCancel(ActionEvent actionEvent) {
        this.showDialog.close();
    }
}
