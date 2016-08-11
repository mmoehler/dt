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

package de.adesso.dtmg.util;

import com.google.common.collect.Lists;
import de.adesso.dtmg.ui.DeclarationTableViewModel;
import de.adesso.dtmg.ui.DefinitionsTableCell;
import de.adesso.dtmg.ui.PossibleIndicatorsSupplier;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static de.adesso.dtmg.util.Adapters.Matrix.adapt;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableList;

/**
 * General Decisiontable Functions
 * Created by moehler ofList 11.01.2016.
 */
@SuppressWarnings("unchecked")
public final class DtFunctions {

    public static final String SPLITEX = "[,;]";
    public static final String QMARK = "?";
    public static final String DASH = "-";
    public static final String RULE_HEADER = "R%02d";
    public static final String   ELSE_RULE_HEADER = "ELSE";
    public static final Supplier<String> QMARK_SUPPLIER = () -> QMARK;
    public static final Supplier<String> DASH_SUPPLIER = () -> DASH;


    public static boolean DIR_DOWN = true;
    public static boolean DIR_RIGHT = DIR_DOWN;
    public static boolean DIR_UP = false;
    public static boolean DIR_LEFT = DIR_UP;


    private DtFunctions() {
    }

    public static <T extends PossibleIndicatorsSupplier> int determineMaxColumns(List<T> indicators) {
        return indicators.stream()
                .map(DtFunctions::determineIndicatorsCount)
                .reduce(1, (y, z) -> y * z);
    }

    public static <T extends PossibleIndicatorsSupplier> List<Integer> determineCountIndicatorsPerRow(List<T> indicators) {
        return indicators.stream()
                .map(DtFunctions::determineIndicatorsCount)
                .collect(toList());
    }

    public static <T extends PossibleIndicatorsSupplier> List<String[]> determineIndicatorArrayPerRow(List<T> indicators) {
        return indicators.stream()
                .map(DtFunctions::determineIndicators)
                .collect(toList());
    }

    public static <T extends PossibleIndicatorsSupplier> List<List<String>> determineIndicatorListPerRow(List<T> indicators) {
        return indicators.stream()
                .map(x -> Arrays.stream(determineIndicators(x)).collect(toList()))
                .collect(toList());
    }

    private static <T extends PossibleIndicatorsSupplier> String[] determineIndicators(T x) {
        return x.possibleIndicatorsProperty().get().split(SPLITEX);
    }

    private static <T extends PossibleIndicatorsSupplier> int determineIndicatorsCount(T x) {
        return determineIndicators(x).length;
    }


    /**
     * Retrns a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view.
     * This matriyx contains all possible compinations of the given condition indicators a conditions block of this decision table
     *
     * @param indicators a list of {@link ConditionDeclTableViewModel}s as declaration of the different conditions
     * @return a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view
     */
    public static ObservableList<ObservableList<String>> fullExpandConditions(List<ConditionDeclTableViewModel> indicators) {
        // TODO Define Preconditions if neccessary!
        final ObservableList<ObservableList<String>> retList = observableArrayList();
        final List<List<String>> rawIndicators = determineIndicatorListPerRow(indicators);
        final List<List<String>> permutations = permutations(rawIndicators);
        final List<List<String>> transposed = List2DFunctions.transpose(permutations);
        transposed.forEach(l -> retList.add(observableArrayList(l)));
        return retList;
    }

    public static ObservableList<ObservableList<String>> fullExpandActions(List<ActionDeclTableViewModel> indicators, int countColumns) {
        final ObservableList<ObservableList<String>> retList = observableArrayList();
        final int rowCount = indicators.size();

        if (0 < rowCount) {

            String[][] rawData = new String[rowCount][countColumns];
            final List<List<String>> transposed = Arrays.stream(rawData).map(s -> {
                Arrays.fill(s, QMARK);
                return new ArrayList<>(Arrays.asList(s));
            }).collect(toList());

            transposed.forEach(l -> retList.add(observableArrayList(l)));

        }

        return retList;
    }

    /**
     * @param indicators   a list of {@link ConditionDeclTableViewModel}s as declaration of the different conditions
     * @param countColumns an {@code int} as cont of columns which sould be generated and initalized
     * @return a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view
     */
    public static ObservableList<ObservableList<String>> limitedExpandConditions(List<ConditionDeclTableViewModel> indicators, int countColumns) {
        return limitedExpandConditions(indicators, countColumns, false);
    }

    /**
     * @param indicators             a list of {@link ConditionDeclTableViewModel}s as declaration of the different conditions
     * @param countColumns           an {@code int} as cont of columns which sould be generated and initalized
     * @param dontFillWithIndicators if {@code true} then all cells of the returned matrix are filled with empty strings
     *                               otherwise the generated indicaqtor permutations are used for the initialization of te matrix
     * @return a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view
     */
    public static ObservableList<ObservableList<String>> limitedExpandConditions(List<ConditionDeclTableViewModel> indicators, int countColumns, boolean dontFillWithIndicators) {
        final ObservableList<ObservableList<String>> retList = observableArrayList();
        final ObservableList<ObservableList<String>> fullExpanded = fullExpandConditions(indicators);
        // tolerate invalid com count. In case of errors, the contColumns is
        // corrected to determineMaxColumns(indicators) columns.
        final int internalCountColumns = min(determineMaxColumns(indicators), countColumns);
        if (dontFillWithIndicators) {
            fullExpanded.forEach(x -> Collections.fill(x, QMARK));
        }
        fullExpanded.forEach(l -> {
            List<String> subList = l.subList(0, internalCountColumns);
            if (subList instanceof ObservableList) {
                retList.add((ObservableList) subList);
            } else {
                retList.add(observableList(subList));
            }
        });
        return retList;
    }

    public static <T> List<List<T>> permutations(List<List<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<List<T>> res = new LinkedList<>();
            recursivePermutation(collections, res, 0, new LinkedList<>());
            return res;
        }
    }

    /**
     * Recursive implementation for {@link #permutations(List)} }
     */
    private static <T> void recursivePermutation(List<? extends List<T>> ori, List<List<T>> res, int d, List<T> current) {
        if (d == ori.size()) {
            res.add(current);
            return;
        }
        List<? extends T> currentCollection = ori.get(d);
        for (T element : currentCollection) {
            List<T> copy = Lists.newLinkedList(current);
            copy.add(element);
            recursivePermutation(ori, res, d + 1, copy);
        }
    }


    public static <T extends DeclarationTableViewModel> void doInsertRows(TableView<T> declarations,
                                                                          TableView<ObservableList<String>> definitions,
                                                                          OptionalInt value,
                                                                          Supplier<T> defaultDecl, Supplier<String> defaultDefValue, Supplier<String> rowHeaderTemplate) {

        OptionalInt index = determineRowIndices(declarations, definitions, value);

        if (index.isPresent()) {
            List<T> newDecls = ListFunctions.insertElementsAt(declarations.getItems(), index.getAsInt(), defaultDecl);
            List<List<String>> newDefs = List2DFunctions.insertRowsAt(adapt(definitions.getItems()), index.getAsInt(), defaultDefValue);

            declarations.getItems().clear();
            newDecls.forEach(declarations.getItems()::add);
            //          updateRowHeaders(declarations, rowHeaderTemplate);

            definitions.getItems().clear();
            newDefs.stream().map(s -> observableArrayList(s)).forEach(definitions.getItems()::add);

            declarations.refresh();
            definitions.refresh();
        }
    }

    public static <T extends DeclarationTableViewModel> void doInsertRowsWithElseRule(TableView<T> declarations,
                                                                          TableView<ObservableList<String>> definitions,
                                                                          OptionalInt value,
                                                                          Supplier<T> defaultDecl, Supplier<String> defaultDefValue, Supplier<String> rowHeaderTemplate) {

        OptionalInt index = determineRowIndices(declarations, definitions, value);

        if (index.isPresent()) {
            List<T> newDecls = ListFunctions.insertElementsAt(declarations.getItems(), index.getAsInt(), defaultDecl);
            List<List<String>> newDefs = List2DFunctions.insertRowsWithElseRuleAt(adapt(definitions.getItems()), index.getAsInt(), defaultDefValue);

            declarations.getItems().clear();
            newDecls.forEach(declarations.getItems()::add);
            //          updateRowHeaders(declarations, rowHeaderTemplate);

            definitions.getItems().clear();
            newDefs.stream().map(s -> observableArrayList(s)).forEach(definitions.getItems()::add);

            declarations.refresh();
            definitions.refresh();
        }
    }


    public static void doRemoveColumns(TableView<ObservableList<String>> conditionTable,
                                       TableView<ObservableList<String>> actionTable,
                                       OptionalInt value) {

        final OptionalInt index = determineColumnIndex(conditionTable, actionTable, value);
        if (index.isPresent()) {

            ObservableList<ObservableList<String>> conditionDefns = conditionTable.getItems();
            ObservableList<ObservableList<String>> actionDefns = actionTable.getItems();
            final int newCols = conditionTable.getColumns().size() - 1;

            conditionTable.getColumns().clear();
            actionTable.getColumns().clear();

            IntStream.range(0, newCols).forEach(i -> {
                conditionTable.getColumns().add(createTableColumn(i));
                actionTable.getColumns().add(createTableColumn(i));
            });

            final List<List<String>> newConDefs = List2DFunctions.removeColumnsAt(adapt(conditionDefns), index.getAsInt());
            final List<List<String>> newActDefs = List2DFunctions.removeColumnsAt(adapt(actionDefns), index.getAsInt());

            conditionDefns.clear();
            newConDefs.stream().map(s -> observableArrayList(s)).forEach(conditionDefns::add);
            actionDefns.clear();
            newActDefs.stream().map(s -> observableArrayList(s)).forEach(actionDefns::add);

            conditionTable.refresh();
            actionTable.refresh();
        }
    }


    public static void doRemoveRows(TableView declarations,
                                    TableView definitions, OptionalInt value) {

        OptionalInt index = determineRowIndices(declarations, definitions, value);

        if (index.isPresent()) {

            List newDecls = ListFunctions.removeElementsAt(declarations.getItems(), index.getAsInt());
            List newDefs = List2DFunctions.removeRowsAt(definitions.getItems(), index.getAsInt());

            declarations.getItems().clear();
            newDecls.forEach(declarations.getItems()::add);
            definitions.getItems().clear();
            newDefs.forEach(definitions.getItems()::add);

            Arrays.asList(declarations, definitions).forEach(TableView::refresh);

        }
    }

    private static boolean isSwappingAllowed(ObservableList<ObservableList<String>> conditionDefinitions, int targetIndex) {
        return !conditionDefinitions.get(0).get(targetIndex).equals(List2DFunctions.ELSE);
    }

    public static boolean doMoveColumns(TableView conditionTable, TableView actionTable, OptionalInt value, boolean direction) {
        final OptionalInt index = determineColumnIndex(conditionTable, actionTable, value);
        boolean ret = false;
        if (index.isPresent()) {
            final int c1Idx = index.getAsInt();
            final int c2Idx = determineNextIndex(direction, c1Idx, conditionTable.getColumns().size());
            ObservableList<ObservableList<String>> conditionDefinitions = conditionTable.getItems();

            if(isSwappingAllowed(conditionDefinitions, c2Idx)) {
                ObservableList<ObservableList<String>> actionDefinitions = actionTable.getItems();

                List<List<String>> newConditionDefns = List2DFunctions.swapColumnsAt(adapt(conditionDefinitions), c1Idx, c2Idx);
                List<List<String>> newActionDefns = List2DFunctions.swapColumnsAt(adapt(actionDefinitions), c1Idx, c2Idx);

                conditionDefinitions.clear();
                newConditionDefns.stream().map(s -> observableArrayList(s)).forEach(conditionDefinitions::add);
                actionDefinitions.clear();
                newActionDefns.stream().map(s -> observableArrayList(s)).forEach(actionDefinitions::add);
                ret = true;
            }
        }
        return ret;
    }

    private static int determineNextIndex(boolean directionDownOrRight, int c1Idx, int maxExclIndex) {
        return (directionDownOrRight)
                ? Math.min(c1Idx + 1, maxExclIndex - 1)
                : Math.max(c1Idx - 1, 0);
    }

    public static void doMoveRows(TableView declarations,
                                  TableView definitions,
                                  OptionalInt value, boolean direction) {

        final OptionalInt index = determineRowIndices(declarations, definitions, value);
        if (index.isPresent()) {
            final int r1Idx = index.getAsInt();
            final int r2Idx = determineNextIndex(direction, r1Idx, declarations.getItems().size());
            List2DFunctions.swapRowsAt(declarations.getItems(), r1Idx, r2Idx);
            List2DFunctions.swapRowsAt(definitions.getItems(), r1Idx, r2Idx);
/*
            declarations.getItems().clear();
            newDecls.forEach(declarations.getItems()::add);
            definitions.getItems().clear();
            newDefns.forEach(definitions.getItems()::add);
*/
        }

    }

    public static <T, U> OptionalInt determineColumnIndex(TableView<T> tableView0, TableView<U> tableView1, OptionalInt externalIndex) {
        OptionalInt index = OptionalInt.empty();
        if (externalIndex.isPresent()) {
            index = externalIndex;
        } else {
            Optional<TablePosition> selectionPos = determineSelectedCellPosition(tableView0, tableView1);
            if (selectionPos.isPresent()) {
                index = OptionalInt.of(selectionPos.get().getColumn());
            }
        }
        return index;
    }

    // FIXME !!
    private static <T, U> OptionalInt determineRowIndices(TableView<T> tableView0, TableView<U> tableView1, OptionalInt externalIndex) {
        OptionalInt index = OptionalInt.empty();
        if (externalIndex.isPresent()) {
            index = externalIndex;
        } else {
            Optional<TablePosition> selectionPos = determineSelectedCellPosition(tableView0, tableView1);
            if (selectionPos.isPresent()) {
                index = OptionalInt.of(selectionPos.get().getRow());
            }
        }
        return index;
    }

    /**
     * Determines a {@link TablePosition} from the given {@link TableView} instances by using the following
     * Algorithm:
     * <pre>
     *     Conditions                               R1 R2 R3 R4
     *     -----------------------------------------------------
     *     C01 tableView0 notNull                    Y  N  N
     *     C02 tableView1 notNull                    -  Y  N
     *     -----------------------------------------------------
     *     Actions
     *     A01 return tableView0.getSelectedCell()   X
     *     A02 return tableView1.getSelectedCell()      X
     *     A03 return &empty;                              X
     *     -----------------------------------------------------
     * </pre>
     *
     * @param tableView0 a nullable {@link TableView} which can contain a selection
     * @param tableView1 a nullable {@link TableView} which can contain a selection
     * @param <T>        Type of the first {@link TableView}
     * @param <U>        Type of the second {@link TableView}
     * @return an {@link Optional} which is present, when a {@link TablePosition} was determined, otherwise
     * an {@link Optional#empty()} is returned.
     */
    private static <T, U> Optional<TablePosition> determineSelectedCellPosition(TableView<T> tableView0, TableView<U> tableView1) {
        Optional<TablePosition> index = Optional.empty();
        // ... together with the condition above, it is ensured, that at least one table view is usable.
        if (null != tableView0 && hasSelectedCells(tableView0)) {
            index = getSelectedCell(tableView0);
        } else if (null != tableView1 && hasSelectedCells(tableView1)) {
            index = getSelectedCell(tableView1);
        }
        return index;

    }

    private static boolean hasSelectedCells(TableView<?> t) {
        return !t.getSelectionModel().getSelectedCells().isEmpty();
    }

    public static boolean isElseColumn(TableColumn<?, ?> tableColumn) {
        return (null != tableColumn && ELSE_RULE_HEADER.equals(tableColumn.getText()));
    }

    public static <T> void updateColHeaders(TableView<T> table) {
        int counter[] = {1};
        table.getColumns().forEach(c -> {
            if (!isElseColumn(c)) {
                c.setText(String.format(RULE_HEADER, counter[0]++));
            }
        });
    }

    /*
    public static <T extends DeclarationTableViewModel> void updateRowHeaders(TableView<T> table, Supplier<String> template) {
        int counter[] = {1};
        table.getItems().forEach(c -> {
            c.lfdNrProperty().set(String.format(template.getVar(), counter[0]++));
            c.save();
        });
    }
*/

    public static <C> Optional<TablePosition> getSelectedCell(TableView<C> table) {
        final ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        return (selectedCells.isEmpty()) ? Optional.empty() : Optional.of(selectedCells.get(0));
    }

    public static TableColumn<ObservableList<String>, String> createTableColumn(int x, Optional<String> name) {
        String tpl = RULE_HEADER;
        final String columnHeader = (name.isPresent()) ? (name.get()) : (String.format(tpl, x + 1));

        TableColumn<ObservableList<String>, String> tc = new TableColumn(columnHeader);

        tc.setCellFactory(DefinitionsTableCell.forTableColumn());

        tc.setOnEditCommit(
                (t) -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).set(t.getTablePosition().getColumn(), t.getNewValue()));

        tc.setCellValueFactory(
                (features) -> new SimpleStringProperty((features.getValue().get(x))
                ));

        tc.setPrefWidth(40);
        tc.setMinWidth(40);
        tc.setResizable(false);
        return tc;
    }

    public static TableColumn<ObservableList<String>, String> createTableColumn(int x) {
        return createTableColumn(x, Optional.empty());
    }

    public static <S> TableColumn<S, String> createExpressionTableColumn(String columnName, String propertyName, int prefWidth,
                                                               int minWidth, int maxWidth, boolean resizable,
                                                               Pos alignment,final String prefix, EventHandler<TableColumn.CellEditEvent<S, String>> value) {
        TableColumn<S, String> col = new TableColumn<>(columnName);
        col.setMinWidth(minWidth);
        col.setPrefWidth(prefWidth);
        col.setMaxWidth(maxWidth);
        col.setResizable(resizable);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        //col.setCellFactory(DeclarationsTableCell.forTableColumn(alignment));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return Normalizer.INSTANCE.toJavaIdentifer(prefix, string);
            }
        }));
        col.setOnEditCommit(value);
        return col;
    }

    public static <S> TableColumn<S, String> createTableColumn(String columnName, String propertyName, int prefWidth,
                                                               int minWidth, int maxWidth, boolean resizable,
                                                               Pos alignment, EventHandler<TableColumn.CellEditEvent<S, String>> value) {
        TableColumn<S, String> col = new TableColumn<>(columnName);
        col.setMinWidth(minWidth);
        col.setPrefWidth(prefWidth);
        col.setMaxWidth(maxWidth);
        col.setResizable(resizable);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        //col.setCellFactory(DeclarationsTableCell.forTableColumn(alignment));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setOnEditCommit(value);
        return col;
    }

    public static void doReplaceRuleConditions(ObservableList<ObservableList<String>> conditionDefinitions, OptionalInt index, List<String> newData) {
        if (index.isPresent() && !conditionDefinitions.isEmpty()) {
            final int col = index.getAsInt();
            final Iterator<String> it = newData.iterator();
            final int rows = conditionDefinitions.size();
            IntStream.range(0, rows).forEach(i -> conditionDefinitions.get(i).set(col, it.next()));
        }
    }
}