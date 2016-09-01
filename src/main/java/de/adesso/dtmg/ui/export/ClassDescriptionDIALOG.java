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

package de.adesso.dtmg.ui.export;

import de.adesso.dtmg.export.java.ClassDescription;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 28.08.16.
 */
public class ClassDescriptionDialog extends Dialog<ClassDescription> {
    private final DialogPane dialogPane;
    private final TextField sourceRootField;
    private final TextField packagenameField;
    private final TextField classnameField;
    private final CheckBox optimizedField;
    private final Button okButton;
    private final Button sourceRootButton;


    public ClassDescriptionDialog() throws IOException {
        dialogPane = FXMLLoader.load(getClass().getResource("ClassDescriptionView.fxml"));
        dialogPane.getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL);

        /*
        FadeTransition ft = new FadeTransition(Duration.millis(1000), dialogPane);
        ft.setFromValue(0.0);
        ft.setToValue(0.97);
        ft.play();
*/

        sourceRootButton = (Button) dialogPane.lookup("#sourceRootButton");
        sourceRootField = (TextField) dialogPane.lookup("#sourceRootField");
        packagenameField = (TextField) dialogPane.lookup("#packagenameField");
        classnameField = (TextField) dialogPane.lookup("#classnameField");
        optimizedField = (CheckBox) dialogPane.lookup("#optimizedField");
        okButton = (Button) dialogPane.lookupButton(ButtonType.OK);

        initializeControls();

    }


    private void initializeControls() {

        // -- SourceRoot Button
        sourceRootButton.setText(" ? ");
        sourceRootButton.setDisable(false);
        sourceRootButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setInitialDirectory(new File("."));
                File dir = chooser.showDialog(dialogPane.getScene().getWindow());
                if (dir == null) {
                    return;
                }
                sourceRootField.setText(dir.getAbsolutePath());
            }
        });

        ChangeListener<String> notEmptyListener = (event, oldValue, newValue)
                -> okButton.setDisable(Stream.of(sourceRootField, packagenameField, classnameField)
                .anyMatch(c -> c.getText().trim().isEmpty())
        );

        // -- SourceRoot Field
        sourceRootField.setEditable(false);
        sourceRootField.textProperty().addListener(notEmptyListener);

        // -- Package name Field
        packagenameField.setEditable(true);
        packagenameField.textProperty().addListener(notEmptyListener);

        // -- Class name Field
        classnameField.setEditable(true);
        classnameField.textProperty().addListener(notEmptyListener);

        // -- Optimezed CheckBox
        optimizedField.setSelected(true);

        // -- OK-Button
        okButton.setDisable(true);
        okButton.setText("Generate");

        setDialogPane(dialogPane);
        Platform.runLater(() -> sourceRootButton.requestFocus());

        setResultConverter(resultConverter());
    }

    private Callback<ButtonType, ClassDescription> resultConverter() {
        return (dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();

            ClassDescription classDescription = null;
            if (data == ButtonBar.ButtonData.OK_DONE) {
                classDescription = ClassDescription.newBuilder()
                        .sourceroot(sourceRootField.getText())
                        .packagename(packagenameField.getText())
                        .classname(classnameField.getText())
                        .optimized(optimizedField.isSelected())
                        .build();
            }
            return classDescription;
        };
    }
}
