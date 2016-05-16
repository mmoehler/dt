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

import de.adesso.tools.Dump;
import de.adesso.tools.common.ListBuilder;
import de.adesso.tools.common.ObservableList2DBuilder;
import de.adesso.tools.functions.fixtures.TableColumnBuilder;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.PossibleIndicatorsSupplier;
import de.adesso.tools.ui.UpdateDefinitionTable;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.util.matchers.Matchers;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TableView;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static de.adesso.tools.functions.DtFunctions.*;
import static de.adesso.tools.functions.DtFunctions.fullExpandConditions;
import static de.adesso.tools.functions.fixtures.DtFunctionsTestData.*;
import static de.adesso.tools.functions.MoreCollectors.toObservableList;
import static de.adesso.tools.functions.ObservableList2DFunctions.insertColumn;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * TEsts of the DtFunctions library
 * Created by moehler ofList 02.03.2016.
 */
public class DtFunctionsTest {

    public static void dumpTableItems(String msg, TableView<?> table) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        table.getItems().forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    @BeforeClass
    public void initToolkit()
            throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });

        // That's a pretty reasonable delay... Right?
        if (!latch.await(5L, TimeUnit.SECONDS))
            throw new ExceptionInInitializerError();
    }

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

        ObservableList<ObservableList<String>> expected = ObservableList2DBuilder.observable2DOf(matrixCode).dim(4, 16).build();

        // TODO create function for fullexpand conditions!!
        ObservableList<ObservableList<String>> actual = fullExpandConditions(indicators);

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

        ObservableList<ObservableList<String>> expected = ObservableList2DBuilder.observable2DOf(matrixCode).dim(4, 16).build();
        ObservableList<ObservableList<String>> actual = fullExpandActions(indicators, 16);

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

        ObservableList<ObservableList<String>> expected = ObservableList2DBuilder.observable2DOf(matrixCode).dim(4, 8).build();
        // TODO create function for limited expand conditions
        ObservableList<ObservableList<String>> actual = limitedExpandConditions(indicators, 8);

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

        ObservableList<ObservableList<String>> expected = ObservableList2DBuilder.observable2DOf(matrixCode).dim(4, 8).build();
        ObservableList<ObservableList<String>> actual = limitedExpandConditions(indicators, 8, true);

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

        ObservableList<ObservableList<String>> expected = ObservableList2DBuilder.observable2DOf(matrixCode).dim(4, 16).build();
        ObservableList<ObservableList<String>> actual = limitedExpandConditions(indicators, 32, true);

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
                Arrays.asList("Y", "Y", "Y", "Y"),
                Arrays.asList("Y", "Y", "Y", "N"),
                Arrays.asList("Y", "Y", "N", "Y"),
                Arrays.asList("Y", "Y", "N", "N"),
                Arrays.asList("Y", "N", "Y", "Y"),
                Arrays.asList("Y", "N", "Y", "N"),
                Arrays.asList("Y", "N", "N", "Y"),
                Arrays.asList("Y", "N", "N", "N"),
                Arrays.asList("N", "Y", "Y", "Y"),
                Arrays.asList("N", "Y", "Y", "N"),
                Arrays.asList("N", "Y", "N", "Y"),
                Arrays.asList("N", "Y", "N", "N"),
                Arrays.asList("N", "N", "Y", "Y"),
                Arrays.asList("N", "N", "Y", "N"),
                Arrays.asList("N", "N", "N", "Y"),
                Arrays.asList("N", "N", "N", "N")
        );
        // TODO create function for permutations!!
        List<List<String>> actual = permutations(original);

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

        TableView<ObservableList<String>> conditionDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("Y,Y,N,N,Y,N,Y,N")
                .withSelectionAt(1, 3)
                .build();

        TableView<ObservableList<String>> actionDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("X,X,X,X,X,X,X,X")
                .build();


        final OptionalInt index = determineColumnIndex(conditionDefTab, actionDefTab, OptionalInt.empty());

        assertTrue(index.isPresent());
        assertEquals(index.getAsInt(),3);

        if (index.isPresent()) {
            ObservableList<ObservableList<String>> tmp = conditionDefTab.getItems();
            ObservableList<ObservableList<String>> oldConDefs = tmp.stream()
                    .map(i -> i.stream().collect(toObservableList()))
                    .collect(toObservableList());
            ObservableList<ObservableList<String>> newConDefs = oldConDefs.stream()
                    .map(insertColumn("?", index.getAsInt()))
                    .collect(toObservableList());

            tmp = actionDefTab.getItems();
            ObservableList<ObservableList<String>> oldActDefs = tmp.stream()
                    .map(i -> i.stream().collect(toObservableList()))
                    .collect(toObservableList());
            ObservableList<ObservableList<String>> newActDefs = oldActDefs.stream()
                    .map(insertColumn("-", index.getAsInt()))
                    .collect(toObservableList());

            Dump.dumpTableItems("Conditions", newConDefs);
            Dump.dumpTableItems("Actions", newActDefs);

            Stream.of(newConDefs).forEach(new UpdateDefinitionTable(conditionDefTab));
            Stream.of(newActDefs).forEach(new UpdateDefinitionTable(actionDefTab));

            Dump.dumpTableItems("Conditions", conditionDefTab.getItems());
            Dump.dumpTableItems("Actions", actionDefTab.getItems());


        }


        ObservableList<ObservableList<String>> expConditionDef = ObservableList2DBuilder.observable2DOf("Y,Y,N,?,N,Y,N,Y,?,N").dim(2, 5).build();
        assertEquals(conditionDefTab.getItems(), expConditionDef);

        ObservableList<ObservableList<String>> expActionDef = ObservableList2DBuilder.observable2DOf("X,X,X,-,X,X,X,X,-,X").dim(2, 5).build();
        assertEquals(actionDefTab.getItems(), expActionDef);
    }

    @Test
    public void testDoRemoveColumns() throws Exception {
        TableView<ObservableList<String>> conditionDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data(
                        "Y,Y,N,N," +
                                "Y,N,Y,N")
                .withSelectionAt(1, 2)
                .build();
        dumpTableItems("Conditions before", conditionDefTab);

        TableView<ObservableList<String>> actionDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("X,X,X,X,X,X,X,X")
                .build();
        dumpTableItems("Actions before", actionDefTab);

        doRemoveColumns(conditionDefTab, actionDefTab, OptionalInt.empty());

        dumpTableItems("Conditions after", conditionDefTab);
        dumpTableItems("Actions after", actionDefTab);

        ObservableList<ObservableList<String>> expConditionDef = ObservableList2DBuilder.observable2DOf(
                "Y,Y,N," +
                        "Y,N,N").dim(2, 3).build();
        assertEquals(conditionDefTab.getItems(), expConditionDef);

        ObservableList<ObservableList<String>> expActionDef = ObservableList2DBuilder.observable2DOf("X,X,X,X,X,X").dim(2, 3).build();
        assertEquals(actionDefTab.getItems(), expActionDef);
    }

    @Test
    public void testDoMoveColumns() throws Exception {

        TableView<ObservableList<String>> conditionDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("Y,Y,N,Y,Y,Y,N,Y")
                .withSelectionAt(1, 2)
                .build();
        dumpTableItems("Conditions before", conditionDefTab);


        TableView<ObservableList<String>> actionDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("X,X,,X,X,X,,X")
                .build();

        dumpTableItems("Actions before", actionDefTab);

        doMoveColumns(conditionDefTab, actionDefTab, OptionalInt.empty(), DIR_LEFT);

        dumpTableItems("Conditions after", conditionDefTab);
        dumpTableItems("Actions after", actionDefTab);

        ObservableList<ObservableList<String>> expConditionDef = ObservableList2DBuilder.observable2DOf("Y,N,Y,Y,Y,N,Y,Y").dim(2, 4).build();
        assertEquals(conditionDefTab.getItems(), expConditionDef);

        ObservableList<ObservableList<String>> expActionDef = ObservableList2DBuilder.observable2DOf("X,,X,X,X,,X,X").dim(2, 4).build();
        assertEquals(actionDefTab.getItems(), expActionDef);

    }

    @Test
    public void testDoInsertRows() throws Exception {
        TableView<ConditionDeclTableViewModel> conditionDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("02").withExpression("EXP-02").withIndicators("Y,N")
                .build();

        TableView<ObservableList<String>> conditionDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("Y,Y,N,Y,Y,Y,N,Y")
                .withSelectionAt(1, 2)
                .build();

        TableView<ConditionDeclTableViewModel> expectedDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("").withExpression("").withIndicators("")
                .addModelWithLfdNbr("02").withExpression("EXP-02").withIndicators("Y,N")
                .build();

        TableView<ObservableList<String>> expectedDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("Y,Y,N,Y,?,?,?,?,Y,Y,N,Y")
                .withSelectionAt(1, 2)
                .build();


        doInsertRows(conditionDeclTab, conditionDefTab, OptionalInt.empty(),
                () -> new ConditionDeclTableViewModel(new ConditionDecl()),
                () -> "?", () -> "%02d");


        Iterator<ConditionDeclTableViewModel> ci = conditionDeclTab.getItems().iterator();
        Iterator<ConditionDeclTableViewModel> ei = expectedDeclTab.getItems().iterator();

        assertEquals(conditionDeclTab.getItems().size(), expectedDeclTab.getItems().size());
        for (; ci.hasNext() && ei.hasNext(); ) {
            assertThat(ci.next(), Matchers.conditionDeclTableViewModelEquals(ei.next()));
        }

        assertEquals(conditionDefTab.getItems(), expectedDefTab.getItems());
    }

    @Test
    public void testDoRemoveRows() throws Exception {
        TableView<ConditionDeclTableViewModel> conditionDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("C01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("C02").withExpression("EXP-02").withIndicators("Y,N")
                .addModelWithLfdNbr("C03").withExpression("EXP-03").withIndicators("Y,N")
                .build();
        dumpTableItems("DECL BEFORE", conditionDeclTab);


        TableView<ObservableList<String>> conditionDefTab = definitionsTableViewBuilder()
                .dim(3, 4)
                .data("Y,Y,Y,Y,N,N,N,N,Y,Y,Y,Y")
                .withSelectionAt(1, 2)
                .build();
        dumpTableItems("DEFN BEFORE", conditionDefTab);

        TableView<ConditionDeclTableViewModel> expectedDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("C01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("C03").withExpression("EXP-03").withIndicators("Y,N")
                .build();


        TableView<ObservableList<String>> expectedDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("Y,Y,Y,Y,Y,Y,Y,Y")
                .build();


        doRemoveRows(conditionDeclTab, conditionDefTab, OptionalInt.empty());

        dumpTableItems("DECL AFTER", conditionDeclTab);
        dumpTableItems("DEFN AFTER", conditionDefTab);

        Iterator<ConditionDeclTableViewModel> ci = conditionDeclTab.getItems().iterator();
        Iterator<ConditionDeclTableViewModel> ei = expectedDeclTab.getItems().iterator();

        assertEquals(conditionDeclTab.getItems().size(), expectedDeclTab.getItems().size());
        for (; ci.hasNext() && ei.hasNext(); ) {
            assertThat(ci.next(), Matchers.conditionDeclTableViewModelEquals(ei.next()));
        }

        assertEquals(conditionDefTab.getItems(), expectedDefTab.getItems());
    }

    @Test
    public void testDoMoveRows() throws Exception {
        TableView<ConditionDeclTableViewModel> conditionDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("C01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("C02").withExpression("EXP-02").withIndicators("Y,N")
                .addModelWithLfdNbr("C03").withExpression("EXP-03").withIndicators("Y,N")
                .build();
        dumpTableItems("DECL BEFORE", conditionDeclTab);


        TableView<ObservableList<String>> conditionDefTab = definitionsTableViewBuilder()
                .dim(3, 4)
                .data("Y,Y,Y,Y,N,N,N,N,Y,Y,Y,Y")
                .withSelectionAt(1, 2)
                .build();
        dumpTableItems("DEFN BEFORE", conditionDefTab);

        TableView<ConditionDeclTableViewModel> expectedDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("C02").withExpression("EXP-02").withIndicators("Y,N")
                .addModelWithLfdNbr("C01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("C03").withExpression("EXP-03").withIndicators("Y,N")
                .build();


        TableView<ObservableList<String>> expectedDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data("N,N,N,N,Y,Y,Y,Y,Y,Y,Y,Y")
                .build();


        doMoveRows(conditionDeclTab, conditionDefTab, OptionalInt.empty(), DIR_UP);

        dumpTableItems("DECL AFTER", conditionDeclTab);
        dumpTableItems("DEFN AFTER", conditionDefTab);

        Iterator<ConditionDeclTableViewModel> ci = conditionDeclTab.getItems().iterator();
        Iterator<ConditionDeclTableViewModel> ei = expectedDeclTab.getItems().iterator();

        assertEquals(conditionDeclTab.getItems().size(), expectedDeclTab.getItems().size());
        for (; ci.hasNext() && ei.hasNext(); ) {
            assertThat(ci.next(), Matchers.conditionDeclTableViewModelEquals(ei.next()));
        }

        assertEquals(conditionDefTab.getItems(), expectedDefTab.getItems());
    }

    @Test
    public void testDoReplaceColumns() throws Exception {


        TableView<ObservableList<String>> conditionDefTab = definitionsTableViewBuilder()
                .dim(3, 4)
                .data(
                                "N,N,N,N," +
                                "N,N,N,N," +
                                "N,N,N,N")
                .withSelectionAt(1, 2)
                .build();
        dumpTableItems("DEFN BEFORE", conditionDefTab);

        List<String> rplcol = ListBuilder.ofList("-,-,-").build();

        doReplaceRuleConditions(conditionDefTab.getItems(),OptionalInt.of(2),rplcol);

        TableView<ObservableList<String>> expectedDefTab = definitionsTableViewBuilder()
                .dim(2, 4)
                .data(
                        "N,N,-,N," +
                        "N,N,-,N," +
                        "N,N,-,N")
                .build();

        dumpTableItems("DEFN AFTER", conditionDefTab);

        assertEquals(conditionDefTab.getItems(), expectedDefTab.getItems());
    }



    @Test
    public void testIsElseColumn() throws Exception {
        TableColumnBuilder<String> tb = new TableColumnBuilder<>("ELSE", null, null);
        assertEquals(isElseColumn(tb.build()), true);
    }

    @Test
    public void testIsElseColumnFail() throws Exception {
        TableColumnBuilder<String> tb = new TableColumnBuilder<>("ELFRIEDE", null, null);
        assertEquals(isElseColumn(tb.build()), false);
    }
}