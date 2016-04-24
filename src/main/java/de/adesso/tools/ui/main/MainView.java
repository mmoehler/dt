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

package de.adesso.tools.ui.main;

import de.adesso.tools.Dump;
import de.adesso.tools.analysis.structure.Operators;
import de.adesso.tools.exception.ExceptionHandler;
import de.adesso.tools.functions.DtFunctions;
import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.DeclarationTableViewModel;
import de.adesso.tools.ui.Notifications;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.ui.dialogs.Dialogs;
import de.adesso.tools.util.OsCheck;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.functions.DtFunctions.*;
import static de.adesso.tools.ui.Notifications.EV_CONSOLIDATE_RULES;

public class MainView implements FxmlView<MainViewModel> {

    public static final String COND_ROW_HEADER = "C%02d";
    private static final String ACT_ROW_HEADER = "A%02d";
    private final DoubleProperty conditionDividerPos = new SimpleDoubleProperty();
    private final DoubleProperty actionDividerPos = new SimpleDoubleProperty();
    @FXML
    public SplitPane conditionSplitPane;
    @FXML
    public SplitPane actionSplitPane;
    @FXML
    public TableView<ConditionDeclTableViewModel> conditionDeclarationsTable;
    @FXML
    public TableView conditionDefinitionsTable;
    @FXML
    public TableView<ActionDeclTableViewModel> actionDeclarationsTable;
    @FXML
    public TableView actionDefinitionsTable;
    @FXML
    private TextArea console;
    @InjectViewModel
    private MainViewModel viewModel;
    @Inject
    private NotificationCenter notificationCenter;
    @Inject
    private ExceptionHandler exceptionHandler;

    private String lastKey = null;
    private FileChooser fileChooser;

    public MainView() {
        super();
    }

    private static <T extends DeclarationTableViewModel> void initializeDeclarationTablesColumns(TableView<T> table) {
        List<TableColumn<T, String>> l = new ArrayList<>();
        l.add(createTableColumn("#", "lfdNr", 40, 40, 40, false, Pos.CENTER,
                (TableColumn.CellEditEvent<T, String> evt) -> evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                        .lfdNrProperty().setValue(evt.getNewValue())));
        l.add(createTableColumn("Expression", "expression", 300, 300, Integer.MAX_VALUE, true, Pos.CENTER_LEFT,
                (TableColumn.CellEditEvent<T, String> evt) -> evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                        .expressionProperty().setValue(evt.getNewValue())));
        l.add(createTableColumn("Indicators", "possibleIndicators", 100, 100, Integer.MAX_VALUE, true, Pos.CENTER,
                (TableColumn.CellEditEvent<T, String> evt) -> evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                        .possibleIndicatorsProperty().setValue(evt.getNewValue())));
        l.forEach(x -> table.getColumns().add(x));
    }

    private static boolean isValid(ObservableList<ConditionDeclTableViewModel> models) {
        for (ConditionDeclTableViewModel model : models) {
            if (!model.isValid()) {
                return false;
            }
        }
        return true;
    }

    private static void configureFileChooser(final FileChooser fileChooser, String title) {
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter dtmExtFilter = new FileChooser.ExtensionFilter("DTMG files (*.dtm)", "*.dtm");
        fileChooser.getExtensionFilters().add(dtmExtFilter);
    }

    public void initialize() {
        initializeDividerSynchronization();
        intializeTableViewScrolling();
        initializeDeclarationTables();
        initializeObservers();
    }

    protected void initializeObservers() {
        notificationCenter.subscribe(Notifications.PREPARE_CONSOLE.name(), (key, value) ->
                console.setText(String.valueOf(value[0])));

        this.viewModel.subscribe(Notifications.REM_ACTION.name(), this::doRemActionDecl);
        this.viewModel.subscribe(Notifications.REM_RULES_WITHOUT_ACTIONS.name(), this::doRemRulesWithoutActions);
        this.viewModel.subscribe(Notifications.REM_RULE.name(), this::doRemRule);
        this.viewModel.subscribe(Notifications.REM_CONDITION.name(), this::doRemConditionDecl);

        this.viewModel.subscribe(Notifications.INS_ACTION.name(), this::doInsActionDecl);
        this.viewModel.subscribe(Notifications.INS_RULE.name(), this::doInsRule);
        this.viewModel.subscribe(Notifications.INS_CONDITION.name(), this::doInsConditionDecl);

        this.viewModel.subscribe(Notifications.ADD_RULE.name(), this::doAddRule);
        this.viewModel.subscribe(Notifications.MOVE_ACTION_DECL_UP.name(), this::doMoveActionDeclUp);
        this.viewModel.subscribe(Notifications.MOVE_ACTION_DECL_DOWN.name(), this::doMoveActionDeclDown);
        this.viewModel.subscribe(Notifications.MOVE_COND_DECL_UP.name(), this::doMoveConditionDeclUp);
        this.viewModel.subscribe(Notifications.MOVE_COND_DECL_DOWN.name(), this::doMoveConditionDeclDown);
        this.viewModel.subscribe(Notifications.MOVE_RULE_LEFT.name(), this::doMoveRuleLeft);
        this.viewModel.subscribe(Notifications.MOVE_RULE_RIGHT.name(), this::doMoveRuleRight);
        this.viewModel.subscribe(Notifications.ADD_ELSE_RULE.name(), this::doAddElseRule);

        this.viewModel.subscribe(Notifications.FILE_OPEN.name(), this::doFileOpen);
        this.viewModel.subscribe(Notifications.FILE_SAVE_AS.name(), this::doFileSaveAs);

        this.viewModel.subscribe(EV_CONSOLIDATE_RULES.name(), this::doConsolidateRules);
    }

    private void doConsolidateRules(String s, Object... objects) {
        List<List<String>> conditions = conditionDefinitionsTable.getItems();
        List<List<String>> actions = actionDefinitionsTable.getItems();

        Dump.dumpTableItems("OLD CODITIONS", conditions);
        Dump.dumpTableItems("OLD ACTIONS", actions);

        Tuple2<List<List<String>>, List<List<String>>> consolidated = Stream.of(Tuple.of(conditions, actions))
                .map(Operators.consolidateRules())
                .collect(MoreCollectors.toSingleObject());

        Dump.dumpTableItems("NEW CODITIONS", consolidated._1());
        Dump.dumpTableItems("NEW ACTIONS", consolidated._2());

        List<List<String>> newConditions = consolidated._1();
        List<List<String>> newActions = consolidated._2();

        final int newCols = newConditions.get(0).size();
        System.out.println("newCols = " + newCols);

        conditionDefinitionsTable.getColumns().clear();
        actionDefinitionsTable.getColumns().clear();

        IntStream.range(0, newCols).forEach(i -> {
            conditionDefinitionsTable.getColumns().add(createTableColumn(i));
            actionDefinitionsTable.getColumns().add(createTableColumn(i));
        });

        conditionDefinitionsTable.getItems().clear();
        newConditions.stream().map(row -> FXCollections.observableArrayList(row)).forEach(conditionDefinitionsTable.getItems()::add);
        actionDefinitionsTable.getItems().clear();
        newActions.stream().map(row -> FXCollections.observableArrayList(row)).forEach(actionDefinitionsTable.getItems()::add);

        conditionDefinitionsTable.refresh();
        actionDefinitionsTable.refresh();
    }

    private FileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser = new FileChooser();
        }
        return fileChooser;
    }

    private void doFileOpen(String key, Object[] value) {
        configureFileChooser(getFileChooser(), "Open DTMG File");
        File file = getFileChooser().showOpenDialog(this.actionSplitPane.getScene().getWindow());
        if (file != null) {
            if (file != null) {
                try {
                    final int countColumns = viewModel.openFile(file);
                    prepareDefinitionsTables4NewData(countColumns);
                    viewModel.populateLoadedData();
                } catch (IOException | ClassNotFoundException e) {
                    exceptionHandler.showAndWaitAlert(e);
                    return;
                }
            }
        }
    }

    private void doFileSaveAs(String key, Object[] value) {
        configureFileChooser(getFileChooser(), "Save DTMG File");
        File file = getFileChooser().showSaveDialog(this.actionSplitPane.getScene().getWindow());
        if (file != null) {
            try {
                viewModel.saveFile(file);
            } catch (IOException e) {
                exceptionHandler.showAndWaitAlert(e);
                return;
            }
        }
    }

    private void doAddElseRule(String key, Object[] value) {
        final int countColumns = conditionDefinitionsTable.getColumns().size();
        final Optional<String> elseRuleName = Optional.of(ELSE_RULE_HEADER);
        conditionDefinitionsTable.getColumns().add(createTableColumn(countColumns, elseRuleName));
        ObservableList<ObservableList<String>> newDefns = (ObservableList<ObservableList<String>>) value[0];
        newDefns.stream().forEach(this.viewModel.getConditionDefinitions()::add);
        conditionDefinitionsTable.refresh();

        actionDefinitionsTable.getColumns().add(createTableColumn(countColumns, elseRuleName));
        if (value.length >= 2 && null != value[1]) {
            newDefns = (ObservableList<ObservableList<String>>) value[1];
            newDefns.stream().forEach(this.viewModel.getActionDefinitions()::add);
            actionDefinitionsTable.refresh();
        }
    }

    private OptionalInt getIndex(Object[] o) {
        OptionalInt index = OptionalInt.empty();
        if (null != o && o.length > 0) {
            index = OptionalInt.of((Integer) o[0]);
        }
        return index;
    }

    private void doMoveConditionDeclDown(String key, Object[] value) {
        doMoveRows(this.conditionDeclarationsTable, this.conditionDefinitionsTable, getIndex(value), DIR_DOWN);
        updateRowHeader();
    }

    private void doMoveConditionDeclUp(String key, Object[] value) {
        doMoveRows(this.conditionDeclarationsTable, this.conditionDefinitionsTable, getIndex(value), DIR_UP);
        updateRowHeader();
    }

    private void doMoveActionDeclDown(String key, Object[] value) {
        doMoveRows(this.actionDeclarationsTable, this.actionDefinitionsTable, getIndex(value), DIR_DOWN);
        updateRowHeader();
    }

    private void doMoveActionDeclUp(String key, Object[] value) {
        doMoveRows(this.actionDeclarationsTable, this.actionDefinitionsTable, getIndex(value), DIR_UP);
        updateRowHeader();
    }

    private void doMoveRuleRight(String key, Object[] value) {
        doMoveColumns(this.conditionDefinitionsTable,
                this.actionDefinitionsTable,
                getIndex(value), DIR_RIGHT);
        updateColHeaders(this.conditionDefinitionsTable);
        updateColHeaders(this.actionDefinitionsTable);
    }

    private void doMoveRuleLeft(String key, Object[] value) {
        doMoveColumns(this.conditionDefinitionsTable,
                this.actionDefinitionsTable,
                getIndex(value), DIR_LEFT);
        updateColHeaders(this.conditionDefinitionsTable);
        updateColHeaders(this.actionDefinitionsTable);
    }

    private void doAddRule(String key, Object[] value) {
        final int countColumns = conditionDefinitionsTable.getColumns().size();
        conditionDefinitionsTable.getColumns().add(createTableColumn(countColumns));
        ObservableList<ObservableList<String>> newDefns = (ObservableList<ObservableList<String>>) value[0];
        newDefns.stream().forEach(this.viewModel.getConditionDefinitions()::add);
        conditionDefinitionsTable.refresh();

        actionDefinitionsTable.getColumns().add(createTableColumn(countColumns));
        if (value.length >= 2 && null != value[1]) {
            newDefns = (ObservableList<ObservableList<String>>) value[1];
            newDefns.stream().forEach(this.viewModel.getActionDefinitions()::add);
            actionDefinitionsTable.refresh();
        }
    }

    private void doRemRule(String key, Object[] value) {
        doRemoveColumns(this.conditionDefinitionsTable, this.actionDefinitionsTable, getIndex(value));
        updateColHeaders(this.conditionDefinitionsTable);
        updateColHeaders(this.actionDefinitionsTable);

    }

    private void doInsRule(String key, Object[] value) {
        doInsertColumns(this.conditionDefinitionsTable, this.actionDefinitionsTable, getIndex(value), QMARK_SUPPLIER, DASH_SUPPLIER);
        updateColHeaders(this.conditionDefinitionsTable);
        updateColHeaders(this.actionDefinitionsTable);

    }

    private void doInsConditionDecl(String key, Object[] value) {
        doInsertRows(this.conditionDeclarationsTable, this.conditionDefinitionsTable, getIndex(value),
                () -> new ConditionDeclTableViewModel(new ConditionDecl()),
                QMARK_SUPPLIER, () -> "C%02d");
        this.viewModel.updateRowHeader();
    }

    private void doInsActionDecl(String key, Object[] value) {
        doInsertRows(this.actionDeclarationsTable, this.actionDefinitionsTable, getIndex(value),
                () -> new ActionDeclTableViewModel(new ActionDecl()),
                QMARK_SUPPLIER, () -> "A%02d");
        this.viewModel.updateRowHeader();
    }


    private void doRemRulesWithoutActions(String kk, Object[] value) {
        /*
        if (null != value && value.length == 1) {
            List<Integer> indices = (List<Integer>) value[0];
            int newCols = conditionDefinitionsTable.getColumns().size() - indices.size();
            conditionDefinitionsTable.getColumns().clear();
            actionDefinitionsTable.getColumns().clear();

            IntStream.range(0, newCols).forEach(i -> {
                conditionDefinitionsTable.getColumns().add(createTableColumn(i));
                actionDefinitionsTable.getColumns().add(createTableColumn(i));
            });

            final ObservableList<ObservableList<String>> newConDefs = MatrixFunctions.removeColumnsAt(viewModel.getConditionDefinitions(), indices);
            final ObservableList<ObservableList<String>> newActDefs = MatrixFunctions.removeColumnsAt(viewModel.getActionDefinitions(), indices);

            viewModel.getConditionDefinitions().clear();
            newConDefs.forEach(viewModel.getConditionDefinitions()::add);
            viewModel.getActionDefinitions().clear();
            newActDefs.forEach(viewModel.getActionDefinitions()::add);

            conditionDefinitionsTable.refresh();
            actionDefinitionsTable.refresh();
        }
        */
    }

    private void doRemConditionDecl(String key, Object[] value) {
        doRemoveRows(this.conditionDeclarationsTable, this.conditionDefinitionsTable, getIndex(value));
        updateRowHeader();

    }

    private void doRemActionDecl(String key, Object[] value) {
        doRemoveRows(this.actionDeclarationsTable, this.actionDefinitionsTable, getIndex(value));
        updateRowHeader();
    }


    public void updateRowHeader() {
        int counter[] = {1};
        this.conditionDeclarationsTable.getItems()
                .forEach(d -> {
                    d.lfdNrProperty().setValue(String.format(COND_ROW_HEADER, counter[0]++));
                    d.save();
                });
        counter[0] = 1;
        this.actionDeclarationsTable.getItems()
                .forEach(d -> {
                    d.lfdNrProperty().setValue(String.format(ACT_ROW_HEADER, counter[0]++));
                    d.save();
                });
    }

    /**
     * Initializes the definition parts of the decision table. At the end of the processing of this method,
     * Tables are ready for an actualization with new data during a load or reload of decision table data.
     *
     * @param countColumns an {@code int} as count of columns which should be created
     *                     during the initialization process
     */
    protected void prepareDefinitionsTables4NewData(int countColumns) {
        this.conditionDefinitionsTable.setEditable(true);
        this.conditionDefinitionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDefinitionsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(conditionDefinitionsTable);
        initializeDefinitionTableColumns(() -> countColumns);

        conditionDefinitionsTable.setItems(viewModel.getConditionDefinitions());


        this.actionDefinitionsTable.setEditable(true);
        this.actionDefinitionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.actionDefinitionsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(actionDefinitionsTable);

        actionDefinitionsTable.setItems(viewModel.getActionDefinitions());

    }

    protected void prepareDefinitionsTables4NewData(int countColumns, boolean shouldPopulateData) {
        this.conditionDefinitionsTable.setEditable(true);
        this.conditionDefinitionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDefinitionsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(conditionDefinitionsTable);
        initializeDefinitionTableColumns(() -> Math.min(determineMaxColumns(viewModel.getConditionDeclarations()), countColumns));

        this.conditionDefinitionsTable.getItems().clear();
        this.conditionDefinitionsTable.setItems(viewModel.initializeConditionDefnsData(countColumns, shouldPopulateData));


        this.actionDefinitionsTable.setEditable(true);
        this.actionDefinitionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.actionDefinitionsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(actionDefinitionsTable);

        //initializeActionDefinitionTableColumns(countColumns);

        this.actionDefinitionsTable.getItems().clear();
        this.actionDefinitionsTable.setItems(viewModel.initializeActionDefnsData(countColumns));
    }

    // TODO #100 Base Template for TableInitialization of Declarations
    protected void initializeDeclarationTables() {
        this.conditionDeclarationsTable.setEditable(true);
        this.conditionDeclarationsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDeclarationsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeConditionDeclFocusHandling();
        initializeTableKeyboardHandling(conditionDeclarationsTable);
        initializeDeclarationTablesColumns(this.conditionDeclarationsTable);

        this.conditionDeclarationsTable.getItems().clear();
        this.conditionDeclarationsTable.setItems(viewModel.getConditionDeclarations());

        this.actionDeclarationsTable.setEditable(true);
        this.actionDeclarationsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.actionDeclarationsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(actionDeclarationsTable);
        initializeDeclarationTablesColumns(this.actionDeclarationsTable);

        this.actionDeclarationsTable.getItems().clear();
        this.actionDeclarationsTable.setItems(viewModel.getActionDeclarations());

    }

    private void initializeDefinitionTableColumns(Supplier<Integer> countColumnsSupplier) {
        conditionDefinitionsTable.getColumns().clear();
        final int cols = countColumnsSupplier.get();
        IntStream.range(0, cols)
                .mapToObj(DtFunctions::createTableColumn)
                .forEach(a -> conditionDefinitionsTable.getColumns().add(a));
        conditionDefinitionsTable.refresh();

        actionDefinitionsTable.getColumns().clear();
        IntStream.range(0, cols)
                .mapToObj(DtFunctions::createTableColumn)
                .forEach(a -> actionDefinitionsTable.getColumns().add(a));
        actionDefinitionsTable.refresh();
    }

    private void initializeTableKeyboardHandling(TableView<?> table) {
        table.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (table.getEditingCell() == null && t.getCode() == KeyCode.ENTER) {
                if (t.isShiftDown()) {
                    table.getSelectionModel().selectAboveCell();
                } else {
                    table.getSelectionModel().selectBelowCell();
                }
                t.consume();
            }
            if (t.isControlDown() && t.getCode() == KeyCode.TAB) {
                if (t.isShiftDown()) {
                    table.getSelectionModel().selectLeftCell();
                } else {
                    table.getSelectionModel().selectRightCell();
                }
                t.consume();
            }
        });

        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        table.setOnKeyPressed((KeyEvent t) -> {
            System.out.println("t = " + t);
            switch (ostype) {
                case MAC_OS:
                    handleMacOSSpecific(table, t);
                    break;
                default:
                    handleWindowsSpecific(table, t);
                    break;
            }
        });
    }

    private void handleMacOSSpecific(TableView<?> table, KeyEvent t) {
        TablePosition tp;
        if (!t.isControlDown() && (t.getCode().isLetterKey()
                || t.getCode().isDigitKey()
                || t.getCode() == KeyCode.SLASH
                || t.getCode() == KeyCode.NUMBER_SIGN)) {
            lastKey = t.getText();
            tp = table.getFocusModel().getFocusedCell();
            table.edit(tp.getRow(), tp.getTableColumn());
            lastKey = null;
        }
    }

    private void handleWindowsSpecific(TableView<?> table, KeyEvent t) {
        TablePosition tp;
        if (!t.isControlDown() && (t.getCode().isLetterKey()
                || t.getCode().isDigitKey()
                || t.getCode() == KeyCode.MINUS
                || t.getCode() == KeyCode.NUMBER_SIGN)) {
            lastKey = t.getText();
            tp = table.getFocusModel().getFocusedCell();
            table.edit(tp.getRow(), tp.getTableColumn());
            lastKey = null;
        }
    }

    private void initializeConditionDeclFocusHandling() {
        this.conditionDeclarationsTable.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && this.conditionDefinitionsTable.focusedProperty().get()) {
                if (this.conditionDefinitionsTable.getItems().isEmpty() && isValid(this.conditionDeclarationsTable.getItems())) {
                    Tuple2<Integer, Boolean> dlgResult = Dialogs.acceptOrDefineRuleCountDialog0(
                            determineMaxColumns(this.viewModel.getConditionDeclarations()), false);
                    prepareDefinitionsTables4NewData(dlgResult._1(), dlgResult._2());
                }
            }
        });
    }

    private void initializeDividerSynchronization() {

        this.console.setFont(Font.font("Courier New", FontWeight.BOLD, 12));

        this.conditionDividerPos.bind(conditionSplitPane.getDividers().get(0).positionProperty());
        this.actionDividerPos.addListener((a, b, c) ->
                conditionSplitPane.getDividers().get(0).setPosition(c.doubleValue()));
        this.actionDividerPos.bind(actionSplitPane.getDividers().get(0).positionProperty());
        this.conditionDividerPos.addListener((a, b, c) ->
                actionSplitPane.getDividers().get(0).setPosition(c.doubleValue()));
    }

    protected void intializeTableViewScrolling() {
        System.err.println("scrollbar = " + String.valueOf(getVerticalScrollbar(this.conditionDefinitionsTable)));
    }

    private ScrollBar getVerticalScrollbar(TableView<?> table) {
        ScrollBar result = null;
        for (Node n : table.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

}

