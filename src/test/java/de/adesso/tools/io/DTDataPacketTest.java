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

package de.adesso.tools.io;

import de.adesso.tools.common.builder.List2DBuilder;
import de.adesso.tools.functions.Adapters;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple4;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TableView;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static de.adesso.tools.functions.fixtures.DtFunctionsTestData.actionDeclTableViewBuilder;
import static de.adesso.tools.functions.fixtures.DtFunctionsTestData.conditionDeclTableViewBuilder;

/**
 * Created by mmoehler ofList 03.04.16.
 */
public class DTDataPacketTest {
    public final static JFXPanel fxPanel = new JFXPanel();
    private final static String OUTPUT_FILE = "second-shot.dtm";

    @Test(priority = 100)
    public void testReadExternal() throws Exception {
        System.out.println("READ");

        try (FileInputStream inputStream = new FileInputStream(OUTPUT_FILE);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {

            DTDataPacket dp = new DTDataPacket();
            dp.reset();

            dp.readExternal(objectInputStream);

        }

        System.out.println("READ");
    }

    @Test(priority = 0)
    public void testWriteExternal() throws Exception {
        System.out.println("WRITE");

        try (FileOutputStream outputStream = new FileOutputStream(OUTPUT_FILE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {

            Tuple4<ObservableList<ConditionDeclTableViewModel>, ObservableList<ObservableList<String>>,
                    ObservableList<ActionDeclTableViewModel>, ObservableList<ObservableList<String>>> data
                    = testData();

            DTDataPacket dp = new DTDataPacket(data._1(), data._2(), data._3(), data._4());

            dp.writeExternal(objectOutputStream);

            objectOutputStream.flush();

        }
        System.out.println("WRITTEN");
    }

    private Tuple4 testData() {
        final TableView<ConditionDeclTableViewModel> conditionDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("C01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("C02").withExpression("EXP-02").withIndicators("Y,N")
                .addModelWithLfdNbr("C03").withExpression("EXP-03").withIndicators("Y,N")
                .addModelWithLfdNbr("C04").withExpression("EXP-04").withIndicators("Y,N")
                .build();
        final ObservableList<ConditionDeclTableViewModel> conditionDeclarations = conditionDeclTab.getItems();

        final List<List<String>> inConditions = List2DBuilder
                .matrixOf("Y,Y,Y,-,-,N,N,N,-,-,-,N,Y,Y,N,N")
                .dim(4, 4)
                .build();
        final ObservableList<ObservableList<String>> conditionDefinitions = Adapters.Matrix.adapt(inConditions);

        final TableView<ActionDeclTableViewModel> actionDeclTab = actionDeclTableViewBuilder()
                .addModelWithLfdNbr("C01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("C02").withExpression("EXP-02").withIndicators("Y,N")
                .addModelWithLfdNbr("C03").withExpression("EXP-03").withIndicators("Y,N")
                .addModelWithLfdNbr("C04").withExpression("EXP-04").withIndicators("Y,N")
                .build();
        final ObservableList<ActionDeclTableViewModel> actionDeclarations = actionDeclTab.getItems();

        final List<List<String>> inActions = List2DBuilder
                .matrixOf("-,-,-,-,X,X,X,-,X,-,-,X,-,X,X,-")
                .dim(4, 4)
                .build();
        final ObservableList<ObservableList<String>> actionDefinitions = Adapters.Matrix.adapt(inActions);

        return Tuple.of(conditionDeclarations, conditionDefinitions, actionDeclarations, actionDefinitions);
    }

}