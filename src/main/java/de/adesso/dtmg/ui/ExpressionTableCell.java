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

package de.adesso.dtmg.ui;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * Created by mmoehler on 14.08.16.
 */
public class ExpressionTableCell<S extends DeclarationTableViewModel> extends TextFieldTableCell<S, String> {

    private final static String HAS_NOTE_STYLE = "-fx-background-image: url(\"de/adesso/dtmg/ui/main/hasdoc.png\");\n" +
            "-fx-background-position: right top;\n" +
            "-fx-background-repeat: no-repeat;";


    public ExpressionTableCell() {
    }

    public ExpressionTableCell(StringConverter<String> converter) {
        super(converter);
    }

    public static <Y extends DeclarationTableViewModel> Callback<TableColumn<Y, String>, TableCell<Y, String>> forExpressionTableColumn() {
        return forExpressionTableColumn(new DefaultStringConverter());
    }

    public static <Y extends DeclarationTableViewModel> Callback<TableColumn<Y, String>, TableCell<Y, String>> forExpressionTableColumn(
            final StringConverter<String> converter) {
        return list -> new ExpressionTableCell<Y>(converter);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setStyle("");
        } else {
            boolean hasDoc = this.getTableView().getItems().get(getIndex()).documentationProperty().isNotEmpty().get();
            if (hasDoc) {
                setStyle(HAS_NOTE_STYLE);
            }
        }
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
        getTableView().requestFocus();
    }
}
