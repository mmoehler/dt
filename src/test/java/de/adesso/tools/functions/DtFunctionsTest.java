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

import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.ui.PossibleIndicatorsSupplier;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static de.adesso.tools.common.MatrixBuilder.observable;
import static de.adesso.tools.common.MatrixBuilder.on;
import static de.adesso.tools.functions.DtFunctions.*;
import static de.adesso.tools.functions.DtFunctionsTestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

/**
 * TEsts of the DtFunctions library
 * Created by moehler on 02.03.2016.
 */
public class DtFunctionsTest {

    @Test
    public void testDetermineMaxColumns() throws Exception {
        List<PossibleIndicatorsSupplier> indicators = listOfIndicatorSupliersBuilder()
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .build();

        int expected = 16; // 2*2*2*2 = 16
        int actual = determineMaxColumns(indicators);

        assertThat(actual, equalTo(expected));
    }


    @Test
    public void testDetermineCountIndicatorsPerRow() throws Exception {
        List<PossibleIndicatorsSupplier> indicators = listOfIndicatorSupliersBuilder()
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .build();

        List<Integer> actual = determineCountIndicatorsPerRow(indicators);
        List<Integer> expected = Arrays.asList(2, 2, 2, 2);

        assertThat(actual, equalTo(expected));

    }

    @Test
    public void testDetermineIndicatorArrayPerRow() throws Exception {
        List<PossibleIndicatorsSupplier> indicators = listOfIndicatorSupliersBuilder()
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .build();

        final List<String[]> actual = determineIndicatorArrayPerRow(indicators);
        final List<String[]> expected = Arrays.asList(
                new String[]{"Y", "N"},
                new String[]{"Y", "N"},
                new String[]{"Y", "N"},
                new String[]{"Y", "N"}
        );

        Iterator<String[]> acIterator = actual.iterator(), exIterator = expected.iterator();

        assertThat(actual.size(), equalTo(expected.size()));
        for (; acIterator.hasNext() && exIterator.hasNext(); ) {
            assertEquals(acIterator.next(), exIterator.next());
        }
    }

    @Test
    public void testDetermineIndicatorListPerRow() throws Exception {
        List<PossibleIndicatorsSupplier> indicators = listOfIndicatorSupliersBuilder()
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .add("Y,N")
                .build();

        final List<List<String>> expected = Arrays.asList(
                Arrays.asList("Y", "N"),
                Arrays.asList("Y", "N"),
                Arrays.asList("Y", "N"),
                Arrays.asList("Y", "N")
        );


        List<List<String>> actual = determineIndicatorListPerRow(indicators);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testFullExpandConditions() throws Exception {
        List<ConditionDeclTableViewModel> indicators = conditionDeclTableViewModelListBuilder()
                .addTableViewModelWithLfdNbr("0").withExpression("NOP-01").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("1").withExpression("NOP-02").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("2").withExpression("NOP-03").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("3").withExpression("NOP-04").withIndicators("Y,N")
                .build();


        final String matrixCode =
                        "Y,Y,Y,Y,Y,Y,Y,Y,N,N,N,N,N,N,N,N," +
                        "Y,Y,Y,Y,N,N,N,N,Y,Y,Y,Y,N,N,N,N," +
                        "Y,Y,N,N,Y,Y,N,N,Y,Y,N,N,Y,Y,N,N," +
                        "Y,N,Y,N,Y,N,Y,N,Y,N,Y,N,Y,N,Y,N";

        ObservableList<ObservableList<String>> expected = observable(MatrixBuilder.on(matrixCode).dim(4, 16).build());
        ObservableList<ObservableList<String>> actual = fullExpandConditions(indicators);

        System.out.println("ACTUAL: -------------------------------");
        actual.forEach(System.out::println);
        System.out.println("EXPECTED: -----------------------------");
        expected.forEach(System.out::println);

        assertEquals(actual.size(), expected.size());

        Iterator<ObservableList<String>> acIterator = actual.iterator(), exIterator = expected.iterator();

        assertThat(actual.size(), equalTo(expected.size()));
        for (; acIterator.hasNext() && exIterator.hasNext(); ) {
            assertEquals(acIterator.next(), exIterator.next());
        }

    }

    @Test
    public void testFullExpandActions() throws Exception {

        List<ActionDeclTableViewModel> indicators = actionDeclTableViewModelListBuilder()
                .addTableViewModelWithLfdNbr("0").withExpression("NOP-01").withIndicators("X")
                .addTableViewModelWithLfdNbr("1").withExpression("NOP-02").withIndicators("X")
                .build();

        final String matrixCode =
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";

        ObservableList<ObservableList<String>> expected = observable(MatrixBuilder.on(matrixCode).dim(4, 16).build());
        ObservableList<ObservableList<String>> actual = fullExpandActions(indicators, 16);

        System.out.println("ACTUAL: -------------------------------");
        actual.forEach(System.out::println);
        System.out.println("EXPECTED: -----------------------------");
        expected.forEach(System.out::println);

        assertEquals(actual.size(), expected.size());

        Iterator<ObservableList<String>> acIterator = actual.iterator(), exIterator = expected.iterator();

        assertThat(actual.size(), equalTo(expected.size()));
        for (; acIterator.hasNext() && exIterator.hasNext(); ) {
            assertEquals(acIterator.next(), exIterator.next());
        }

    }

    @Test
    public void testLimitedExpandConditionsWithIndicators() throws Exception {
        List<ConditionDeclTableViewModel> indicators = conditionDeclTableViewModelListBuilder()
                .addTableViewModelWithLfdNbr("0").withExpression("NOP-01").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("1").withExpression("NOP-02").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("2").withExpression("NOP-03").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("3").withExpression("NOP-04").withIndicators("Y,N")
                .build();


        final String matrixCode =
                "Y,Y,Y,Y,Y,Y,Y,Y," +
                        "Y,Y,Y,Y,N,N,N,N," +
                        "Y,Y,N,N,Y,Y,N,N," +
                        "Y,N,Y,N,Y,N,Y,N";

        ObservableList<ObservableList<String>> expected = observable(MatrixBuilder.on(matrixCode).dim(4, 8).build());
        ObservableList<ObservableList<String>> actual = limitedExpandConditions(indicators, 8);

        System.out.println("ACTUAL: -------------------------------");
        actual.forEach(System.out::println);
        System.out.println("EXPECTED: -----------------------------");
        expected.forEach(System.out::println);

        assertEquals(actual.size(), expected.size());

        Iterator<ObservableList<String>> acIterator = actual.iterator(), exIterator = expected.iterator();

        assertThat(actual.size(), equalTo(expected.size()));
        for (; acIterator.hasNext() && exIterator.hasNext(); ) {
            assertEquals(acIterator.next(), exIterator.next());
        }

    }

    @Test
    public void testLimitedExpandConditionsWithoutIndicators() throws Exception {
        List<ConditionDeclTableViewModel> indicators = conditionDeclTableViewModelListBuilder()
                .addTableViewModelWithLfdNbr("0").withExpression("NOP-01").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("1").withExpression("NOP-02").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("2").withExpression("NOP-03").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("3").withExpression("NOP-04").withIndicators("Y,N")
                .build();


        final String matrixCode =
                "?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?";

        ObservableList<ObservableList<String>> expected = observable(MatrixBuilder.on(matrixCode).dim(4, 8).build());
        ObservableList<ObservableList<String>> actual = limitedExpandConditions(indicators, 8, true);

        System.out.println("ACTUAL: -------------------------------");
        actual.forEach(System.out::println);
        System.out.println("EXPECTED: -----------------------------");
        expected.forEach(System.out::println);

        assertEquals(actual.size(), expected.size());

        Iterator<ObservableList<String>> acIterator = actual.iterator(), exIterator = expected.iterator();

        assertThat(actual.size(), equalTo(expected.size()));
        for (; acIterator.hasNext() && exIterator.hasNext(); ) {
            assertEquals(acIterator.next(), exIterator.next());
        }
    }

    @Test
    public void testLimitedExpandConditionsWithoutIndicatorsAndToManyColumns() throws Exception {
        List<ConditionDeclTableViewModel> indicators = conditionDeclTableViewModelListBuilder()
                .addTableViewModelWithLfdNbr("0").withExpression("NOP-01").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("1").withExpression("NOP-02").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("2").withExpression("NOP-03").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("3").withExpression("NOP-04").withIndicators("Y,N")
                .build();


        final String matrixCode =
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";

        ObservableList<ObservableList<String>> expected = observable(MatrixBuilder.on(matrixCode).dim(4, 16).build());
        ObservableList<ObservableList<String>> actual = limitedExpandConditions(indicators, 32, true);

        System.out.println("ACTUAL: -------------------------------");
        actual.forEach(System.out::println);
        System.out.println("EXPECTED: -----------------------------");
        expected.forEach(System.out::println);

        assertEquals(actual.size(), expected.size());

        Iterator<ObservableList<String>> acIterator = actual.iterator(), exIterator = expected.iterator();

        assertThat(actual.size(), equalTo(expected.size()));
        for (; acIterator.hasNext() && exIterator.hasNext(); ) {
            assertEquals(acIterator.next(), exIterator.next());
        }
    }

    @Test
    public void testPermutations() throws Exception {
        final List<List<String>> original = Arrays.asList(
                Arrays.asList("Y", "N"),
                Arrays.asList("Y", "N"),
                Arrays.asList("Y", "N"),
                Arrays.asList("Y", "N")
        );

        List<List<String>> expected = Arrays.asList(
                Arrays.asList("Y","Y","Y","Y"),
                Arrays.asList("Y","Y","Y","N"),
                Arrays.asList("Y","Y","N","Y"),
                Arrays.asList("Y","Y","N","N"),
                Arrays.asList("Y","N","Y","Y"),
                Arrays.asList("Y","N","Y","N"),
                Arrays.asList("Y","N","N","Y"),
                Arrays.asList("Y","N","N","N"),
                Arrays.asList("N","Y","Y","Y"),
                Arrays.asList("N","Y","Y","N"),
                Arrays.asList("N","Y","N","Y"),
                Arrays.asList("N","Y","N","N"),
                Arrays.asList("N","N","Y","Y"),
                Arrays.asList("N","N","Y","N"),
                Arrays.asList("N","N","N","Y"),
                Arrays.asList("N","N","N","N")
        );
        List<List<String>> actual = permutations(original);

        System.out.println("ACTUAL: -------------------------------");
        actual.forEach(System.out::println);
        System.out.println("EXPECTED: -----------------------------");
        expected.forEach(System.out::println);

        assertEquals(actual.size(), expected.size());

        Iterator<List<String>> acIterator = actual.iterator();
        Iterator<List<String>> exIterator = expected.iterator();

        assertThat(actual.size(), equalTo(expected.size()));
        for (; acIterator.hasNext() && exIterator.hasNext(); ) {
            assertEquals(acIterator.next(), exIterator.next());
        }

    }

    @Test
    public void testDoInsertColumns() throws Exception {




        ObservableList<ObservableList<String>> actionDef = observable(on("X,X,X,X,X,X,X,X").dim(2, 4).build());
        ObservableList<ObservableList<String>> conditionDef = observable(on("Y,Y,N,N,Y,N,Y,N").dim(2, 4).build());

        TableView<ConditionDeclTableViewModel> conditionTab = PowerMockito.mock(TableView.class, Mockito
                .withSettings()
                .name("TableView")
                .verboseLogging());

        List<ConditionDeclTableViewModel> conditionIndicators = conditionDeclTableViewModelListBuilder()
                .addTableViewModelWithLfdNbr("0").withExpression("NOP-01").withIndicators("Y,N")
                .addTableViewModelWithLfdNbr("1").withExpression("NOP-02").withIndicators("Y,N")
                .build();
        conditionTab.setItems(FXCollections.observableArrayList(conditionIndicators));


        List<ActionDeclTableViewModel> actionIndicators = actionDeclTableViewModelListBuilder()
                .addTableViewModelWithLfdNbr("0").withExpression("NOP-01").withIndicators("X")
                .addTableViewModelWithLfdNbr("1").withExpression("NOP-02").withIndicators("X")
                .build();
        TableView<ConditionDeclTableViewModel> actionTab = PowerMockito.mock(TableView.class, Mockito
                .withSettings()
                .name("ActionTableView")
                .verboseLogging());

        //actionTab.setItems(FXCollections.observableArrayList(actionIndicators));


        //ObservableList<ObservableList<String>> actual = doInsertColumns(conditionDef,actionDef,conditionTab,actionTab, );
/*
        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }

        */
    }

    @Test
    public void testDoInsertRows() throws Exception {

    }

    @Test
    public void testDoRemoveColumns() throws Exception {

    }

    @Test
    public void testDoRemoveRows() throws Exception {

    }

    @Test
    public void testDoMoveColumns() throws Exception {

    }

    @Test
    public void testDoMoveRows() throws Exception {

    }

    @Test
    public void testIsElseColumn() throws Exception {

    }

    @Test
    public void testUpdateColHeaders() throws Exception {

    }

    @Test
    public void testGetSelectedCell() throws Exception {

    }

    @Test
    public void testCreateTableColumn() throws Exception {

    }

    @Test
    public void testCreateTableColumn1() throws Exception {

    }

    @Test
    public void testCreateTableColumn2() throws Exception {

    }
}