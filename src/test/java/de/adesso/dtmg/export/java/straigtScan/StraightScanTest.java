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

package de.adesso.dtmg.export.java.straigtScan;

import com.sun.codemodel.JCodeModel;
import de.adesso.dtmg.export.java.ClassDescription;
import de.adesso.dtmg.export.java.straightscan.StraightScan;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.model.DecisionTable;
import de.adesso.dtmg.util.ObservableList2DBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by moehler on 14.06.2016.
 */
public class StraightScanTest {


    /**
     * <table border="1" cellpadding="5" cellspacing="0" align="center">
     * <caption><b>Printer troubleshooter</b></caption>
     * <tr>
     * <td></td>
     * <td></td>
     * <th colspan="8">Rules</th>
     * </tr>
     * <tr>
     * <td rowspan="3">Conditions</td>
     * <td>Printer does not print</td>
     * <td>Y</td>
     * <td>Y</td>
     * <td>Y</td>
     * <td>Y</td>
     * <td>N</td>
     * <td>N</td>
     * <td>N</td>
     * <td>N</td>
     * </tr>
     * <tr>
     * <td>A red light is flashing</td>
     * <td>Y</td>
     * <td>Y</td>
     * <td>N</td>
     * <td>N</td>
     * <td>Y</td>
     * <td>Y</td>
     * <td>N</td>
     * <td>N</td>
     * </tr>
     * <tr>
     * <td>Printer is unrecognised</td>
     * <td>Y</td>
     * <td>N</td>
     * <td>Y</td>
     * <td>N</td>
     * <td>Y</td>
     * <td>N</td>
     * <td>Y</td>
     * <td>N</td>
     * </tr>
     * <tr>
     * <td rowspan="5">Actions</td>
     * <td>Check the power cable</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * </tr>
     * <tr>
     * <td>Check the printer-computer cable</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * </tr>
     * <tr>
     * <td>Ensure printer software is installed</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>&#160;</td>
     * </tr>
     * <tr>
     * <td>Check/replace ink</td>
     * <td>X</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * </tr>
     * <tr>
     * <td>Check for paper jam</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>X</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * <td>&#160;</td>
     * </tr>
     * </table>
     */
    @Test
    public void testApply() throws Exception {

        final ObservableList<ConditionDecl> conditionDecls = FXCollections.observableArrayList(
                ConditionDecl.newBuilder().withExpression("Printer does not print").withPossibleIndicators("Y,N").withLfdNr("C01").build(),
                ConditionDecl.newBuilder().withExpression("A red light is flashing").withPossibleIndicators("Y,N").withLfdNr("C02").build(),
                ConditionDecl.newBuilder().withExpression("Printer is unrecognised").withPossibleIndicators("Y,N").withLfdNr("C03").build()
        );

        final ObservableList<ObservableList<String>> conditionDefns = ObservableList2DBuilder.observable2DOf("Y,Y,Y,Y,N,N,N,N,Y,Y,N,N,Y,Y,N,N,Y,N,Y,N,Y,N,Y,N").dim(3, 8).build();

        final ObservableList<ActionDecl> actionDecls = FXCollections.observableArrayList(
                ActionDecl.newBuilder().withExpression("Check the power cable").withPossibleIndicators("X").withLfdNr("A01").build(),
                ActionDecl.newBuilder().withExpression("Check the printer-computer cable").withPossibleIndicators("X").withLfdNr("A01").build(),
                ActionDecl.newBuilder().withExpression("Ensure printer software is installed").withPossibleIndicators("X").withLfdNr("A01").build(),
                ActionDecl.newBuilder().withExpression("Check/replace ink").withPossibleIndicators("X").withLfdNr("A01").build(),
                ActionDecl.newBuilder().withExpression("Check for paper jam").withPossibleIndicators("X").withLfdNr("A01").build()
        );

        final ObservableList<ObservableList<String>> actionDefns = ObservableList2DBuilder.observable2DOf(
                "-,-,X,-,-,-,-,-," +
                        "X,-,X,-,-,-,-,-," +
                        "X,-,X,-,X,-,X,-," +
                        "X,X,-,-,X,X,-,-," +
                        "-,X,-,X,-,-,-,-").dim(3, 8).build();

        final DecisionTable decisionTable = DecisionTable.newBuilder()
                .conditionDecls(conditionDecls)
                .conditionDefs(conditionDefns)
                .actionDecls(actionDecls)
                .actionDefs(actionDefns)
                .build();

        final ClassDescription classDescription = ClassDescription.newBuilder()
                .classname("PrinterTroubleshootingRules")
                .packagename("de.adesso.dtmg.export.java")
                .sourceroot("./src/test/java")
                .build();

        final JCodeModel jCodeModel = new StraightScan().apply(decisionTable, classDescription);

        final File file = new File(classDescription.getSourceRoot());
        //file.mkdirs();
        jCodeModel.build(file);

    }
}