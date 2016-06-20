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
import de.adesso.dtmg.common.builder.ObservableList2DBuilder;
import de.adesso.dtmg.export.java.ClassDescription;
import de.adesso.dtmg.export.java.StraightScan;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.model.DecisionTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by moehler on 14.06.2016.
 */
public class StraightScanTest2 {


    /**
     *S
    */
    @Test
    public void testApply() throws Exception {

        final ObservableList<ConditionDecl> conditionDecls = FXCollections.observableArrayList(
            ConditionDecl.newBuilder().withExpression("istLeistungsartKGVGWTKG").withPossibleIndicators("Y,N").withLfdNr("C01").build(),
            ConditionDecl.newBuilder().withExpression("istLeistungsartOnlyWTKG").withPossibleIndicators("Y,N").withLfdNr("C02").build(),
            ConditionDecl.newBuilder().withExpression("keineLeistungsarten").withPossibleIndicators("Y,N").withLfdNr("C03").build(),
                ConditionDecl.newBuilder().withExpression("andereLeistungsarten").withPossibleIndicators("Y,N").withLfdNr("C04").build(),
                ConditionDecl.newBuilder().withExpression("istUrsachenSchluessel010203").withPossibleIndicators("Y,N").withLfdNr("C05").build()
        );

        final ObservableList<ObservableList<String>> conditionDefns = ObservableList2DBuilder.observable2DOf(
                "Y,Y,N,N,N," +
                        "Y,Y,N,N,N," +
                        "N,N,Y,Y,N," +
                        "N,N,N,N,Y," +
                        "Y,N,Y,N,-").dim(5, 5).build();

        final ObservableList<ActionDecl> actionDecls = FXCollections.observableArrayList(
                ActionDecl.newBuilder().withExpression("aufgabeLeistungsartFestlegen").withPossibleIndicators("X").withLfdNr("A01").build(),
                ActionDecl.newBuilder().withExpression("lesitungsartKGFestlegenCheck the printer-computer cable").withPossibleIndicators("X").withLfdNr("A01").build(),
                ActionDecl.newBuilder().withExpression("exitBatch").withPossibleIndicators("X").withLfdNr("A01").build()
        );

        final ObservableList<ObservableList<String>> actionDefns = ObservableList2DBuilder.observable2DOf(
                        "X,-,X,-,-," +
                        "-,X,-,X,-," +
                        "X,-,-,-,X").dim(3, 5).build();

        final DecisionTable decisionTable = DecisionTable.newBuilder()
                .conditionDecls(conditionDecls)
                .conditionDefs(conditionDefns)
                .actionDecls(actionDecls)
                .actionDefs(actionDefns)
                .build();

        final ClassDescription classDescription = ClassDescription.newBuilder()
                .classname("LeistungsartenFestlegenPart01Rules")
                .packagename("de.adesso.dtmg.export.java")
                .sourceroot("./src/test/java")
                .build();

        final JCodeModel jCodeModel = new StraightScan().apply(decisionTable, classDescription);

        final File file = new File(classDescription.getSourceRoot());
        //file.mkdirs();
        jCodeModel.build(file);

    }
}