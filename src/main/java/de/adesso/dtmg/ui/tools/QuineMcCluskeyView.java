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

package de.adesso.dtmg.ui.tools;

import com.google.common.base.Strings;
import de.adesso.dtmg.export.quine.QuineMcCluskey;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.function.Function;

/**
 * Created by mmoehler on 30.07.16.
 */
public class QuineMcCluskeyView implements FxmlView<QuineMcCluskeyViewModel> {

    private final Function<String, String> quineMcCluskeyOptimizer = new QuineMcCluskey();
    @InjectViewModel
    private QuineMcCluskeyViewModel viewModel;
    @FXML
    private TextArea txtInput;
    @FXML
    private TextArea txtOutput;
    @Inject
    private Stage primaryStage;

    public void doClose(ActionEvent actionEvent) {
        viewModel.doClose();
    }

    public void doOptimze(ActionEvent actionEvent) {
        txtOutput.setEditable(false);
        final String expression = txtInput.getText();
        if (Strings.isNullOrEmpty(expression)) {
            txtOutput.setStyle("-fx-text-fill: #ff0000; -fx-font-family: courier;");
            //txtOutput.addEventFilter(MouseEvent.ANY, MouseEvent::consume);
            txtOutput.setText("It will not work like that!!\n"
                    + "For your formula definition use the following BNF:\n"
                    + "<orExpr>   ::= <andExpr> | <andExpr> '+' <orExpr>\n"
                    + "<andExpr>  ::= <var> | <var> ('*')? <andExpr>\n"
                    + "<var>      ::= ('!')?[a..z]\n"
                    + "\n"
                    + "Sample-01 : !a!b!c!d + !acd + ab!c + ab!d + bcd\n"
                    + "Sample-02 : !a*!b*!c*!d + !a*c*d + a*b*!c + a*b*!d + b*c*d\n"
                    + "\n"
                    + "Now try again.");
        } else {
            final String optimized = quineMcCluskeyOptimizer.apply(expression);
            txtOutput.setStyle("-fx-text-fill: black;");
            txtOutput.setText(optimized);
        }


    }

    public void doClearAll(ActionEvent actionEvent) {
        txtInput.setText("");
        txtOutput.setText("");
    }
}
