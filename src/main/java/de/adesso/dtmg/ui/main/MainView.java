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

package de.adesso.dtmg.ui.main;

import de.adesso.dtmg.Reserved;
import de.adesso.dtmg.exception.ExceptionHandler;
import de.adesso.dtmg.export.java.ClassDescription;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.ui.DeclarationTableViewModel;
import de.adesso.dtmg.ui.Notifications;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.ui.dialogs.Dialogs;
import de.adesso.dtmg.ui.editor.EditorDialog;
import de.adesso.dtmg.ui.editor.EditorDialogModel;
import de.adesso.dtmg.ui.export.ClassDescriptionDialog;
import de.adesso.dtmg.util.DialogHelper;
import de.adesso.dtmg.util.DtFunctions;
import de.adesso.dtmg.util.OsCheck;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.dtmg.analysis.structure.Operators.consolidateRules;
import static de.adesso.dtmg.ui.Notifications.EV_CONSOLIDATE_RULES;
import static de.adesso.dtmg.util.DtFunctions.*;
import static de.adesso.dtmg.util.MoreCollectors.toSingleObject;

@SuppressWarnings("unchecked")
public class MainView extends DtView implements FxmlView<MainViewModel> {

    public static final String COND_ROW_HEADER = "C%02d";
    public static final String PREFIX_IS = "is";
    public static final String PREFIX_DO = "do";
    private static final String ACT_ROW_HEADER = "A%02d";
    private static final String ELSE = "E";
    private final static Predicate<ObservableList<String>> HAS_ELSE_RULE =
            c -> (c.isEmpty()) ? false : c.get(c.size() - 1).equals(ELSE);
    private final DoubleProperty conditionDividerPos = new SimpleDoubleProperty();
    private final DoubleProperty actionDividerPos = new SimpleDoubleProperty();
    private final SimpleObjectProperty<Path> currentFile = new SimpleObjectProperty<>();
    @FXML
    public SplitPane conditionSplitPane;
    @FXML
    public SplitPane actionSplitPane;
    @FXML
    public TableView<ConditionDeclTableViewModel> conditionDeclarationsTable;
    @FXML
    public TableView<ObservableList<String>> conditionDefinitionsTable;
    @FXML
    public TableView<ActionDeclTableViewModel> actionDeclarationsTable;
    @FXML
    public TableView<ObservableList<String>> actionDefinitionsTable;
    @FXML
    private TextArea console;
    @InjectViewModel
    private MainViewModel viewModel;
    @Inject
    private Stage primaryStage;
    @Inject
    private ExceptionHandler exceptionHandler;
    private String lastKey = null;
    private FileChooser fileChooser;
    private DeclarationTableViewModel selectedModel;
    private TableView<? extends DeclarationTableViewModel> selectedTable;

    public MainView() {
        super();
    }

    private static <T extends DeclarationTableViewModel> void initializeDeclarationTablesColumns(TableView<T> table, String prefix) {
        List<TableColumn<T, String>> l = new ArrayList<>();
        l.add(createTableColumn("#", "lfdNr", 40, 40, 40, false, Pos.CENTER,
                (TableColumn.CellEditEvent<T, String> evt) -> evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                        .lfdNrProperty().setValue(evt.getNewValue())));

        l.add(createExpressionTableColumn("Expression", "expression", 300, 300, Integer.MAX_VALUE, true, Pos.CENTER_LEFT, prefix,
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
        FileChooser.ExtensionFilter dtmgExtFilters[] = {new FileChooser.ExtensionFilter("Binary files (*.dtm)", "*.dtm"),
                new FileChooser.ExtensionFilter("Horizontal ASCII files (*.dth)", "*.dth"),
                new FileChooser.ExtensionFilter("Vertical ASCII files (*.dtv)", "*.dtv"),
                new FileChooser.ExtensionFilter("Zipped CSV files (*.dtz)", "*.dtz"),
        };
        Arrays.stream(dtmgExtFilters).forEach(fileChooser.getExtensionFilters()::add);

    }

    private static void configureFileChooser4Export(final FileChooser fileChooser, String title) {
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter dtmgExtFilters[] = {
                new FileChooser.ExtensionFilter("ODF text file (*.odt)", "*.odt")
        };
        Arrays.stream(dtmgExtFilters).forEach(fileChooser.getExtensionFilters()::add);
    }

    @Override
    public TableView<ActionDeclTableViewModel> getActionDeclarationsTable() {
        return this.actionDeclarationsTable;
    }

    @Override
    public TableView<ObservableList<String>> getActionDefinitionsTable() {
        return this.actionDefinitionsTable;
    }

    @Override
    public TableView<ConditionDeclTableViewModel> getConditionDeclarationsTable() {
        return this.conditionDeclarationsTable;
    }

    @Override
    public TableView<ObservableList<String>> getConditionDefinitionsTable() {
        return this.conditionDefinitionsTable;
    }

    public void initialize() {

        /*
        this.conditionDeclarationsTable.focusedProperty().addListener((ov,oldB, newB) -> {
            if(oldB && !newB) {
                conditionDeclarationsTable.getSelectionModel().clearSelection();
            }
        });
        */


        initializeDividerSynchronization();
        intializeTableViewScrolling();
        initializeDeclarationTables();
        initializeObservers();
    }

    protected void initializeObservers() {
        this.viewModel.subscribe(Notifications.PREPARE_CONSOLE.name(), (key, value) ->
                console.setText(String.valueOf(value[0])));

        this.viewModel.subscribe(Notifications.REM_ACTION.name(), this::doRemActionDecl);
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
        this.viewModel.subscribe(Notifications.FILE_EXPORT_AS.name(), this::doFileExportAs);

        this.viewModel.subscribe(EV_CONSOLIDATE_RULES.name(), this::doConsolidateRules);

        this.viewModel.subscribe(Notifications.DOCUMENT_DECLARATION.name(), this::doDocumentDeclaration);

        this.viewModel.subscribe(Notifications.UPD_DOCUMENT.name(), this::updateDocumentDeclaration);

        this.viewModel.subscribe(Notifications.GENERATE_LINE_MASK.name(), this::generateUsingLineMask);
        this.viewModel.subscribe(Notifications.GENERATE_STRAIGHT_SCAN.name(), this::generateUsingStraightScan);
        this.viewModel.subscribe(Notifications.GENERATE_TREE_METHOD.name(), this::generateUsingTreeMethod);
        this.viewModel.subscribe(Notifications.GENERATE_VEINOTT.name(), this::generateUsingVeinott);
    }

    private void generateUsingLineMask(String s, Object... objects) {
    }

    private void generateUsingStraightScan(String s, Object... objects) {
        try {
            ClassDescriptionDialog dialog = new ClassDescriptionDialog();
            Optional<ClassDescription> classDescription = dialog.showAndWait();
            viewModel.handleGenerateUsingStraightScan(classDescription);
        } catch (Exception e) {
            exceptionHandler.showAndWaitAlert(e);
            return;
        }
    }

    private void generateUsingVeinott(String s, Object... objects) {
    }

    private void generateUsingTreeMethod(String s, Object... objects) {
        try {
            ClassDescriptionDialog dialog = new ClassDescriptionDialog();
            Optional<ClassDescription> classDescription = dialog.showAndWait();
            viewModel.handleGenerateUsingTreeMethod(classDescription);
        } catch (Exception e) {
            exceptionHandler.showAndWaitAlert(e);
            return;
        }
    }

    private void doConsolidateRules(String s, Object... objects) {
        try {
            ObservableList<ObservableList<String>> conditions = conditionDefinitionsTable.getItems();
            ObservableList<ObservableList<String>> actions = actionDefinitionsTable.getItems();

            Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> consolidated = Stream.of(Tuple.of(conditions, actions))
                    .map(consolidateRules())
                    .collect(toSingleObject());

            ObservableList<ObservableList<String>> newConditions = consolidated._1();
            ObservableList<ObservableList<String>> newActions = consolidated._2();

            updateDefinitions(newConditions, newActions);


        } catch (Exception e) {
            exceptionHandler.showAndWaitAlert(e);
            return;
        }
    }

    private FileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser = new FileChooser();
        }
        return fileChooser;
    }

    private void doDocumentDeclaration(String s, Object... objects) {
        OptionalInt index = getIndex(objects);
        Optional<TableView<? extends DeclarationTableViewModel>> table = getSelectedDeclTable();
        if (table.isPresent()) {
            selectedTable = table.get();
            selectedModel = table.get().getSelectionModel().getSelectedItem();


            ViewTuple<EditorDialog, EditorDialogModel> load = FluentViewLoader
                    .fxmlView(EditorDialog.class)
                    .providedScopes(viewModel.getDialogScopes())
                    .load();

            load.getCodeBehind().setText(selectedModel.documentationProperty().get());//model.documentationProperty().get());

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/about.css");
            load.getCodeBehind().setDisplayingStage(showDialog);

        }
    }

    private void updateDocumentDeclaration(String s, Object... objects) {
        String s1 = String.valueOf(objects[0]);
        selectedModel.documentationProperty().set((String) objects[0]);
        selectedTable.getColumns().get(1).setVisible(false);
        selectedTable.getColumns().get(1).setVisible(true);

    }

    private Optional<TableView<? extends DeclarationTableViewModel>> getSelectedDeclTable() {
        TableView<? extends DeclarationTableViewModel> ret = null;
        if (getConditionDeclarationsTable().focusedProperty().get()) {
            ret = getConditionDeclarationsTable();
        } else if (getActionDeclarationsTable().focusedProperty().get()) {
            ret = getActionDeclarationsTable();
        }
        return Optional.ofNullable(ret);
    }

    private void doFileOpen(String key, Object[] value) {

        if (checkForOpenFile()) {

            switch (querySaveIfChanged()) {
                case YES:
                    // save the old file
                    try {
                        viewModel.saveFile(this.currentFile.get().toUri());
                    } catch (IOException e) {
                        exceptionHandler.showAndWaitAlert(e);
                        return;
                    } finally {
                        this.currentFile.setValue(null);
                    }
                    break;
                case NO:
                    // do nothing and forget the old stuff
                    break;
                default:
                    // (Cancel was pushed) break the whole process
                    return;
            }
        }

        if (null != value && value.length > 0 && null != value[0] && value[0] instanceof URI) {
            URI uri = URI.class.cast(value[0]);
            internalOpenFile(uri);
            return;
        }

        FileChooser fc = getFileChooser();
        try {
            configureFileChooser(getFileChooser(), "Open DTMG File");
            File file = fc.showOpenDialog(this.actionSplitPane.getScene().getWindow());
            if (file != null) {
                internalOpenFile(file.toURI());
            }
        } finally {
            fc.getExtensionFilters().clear();
        }

    }

    private void internalOpenFile(URI uri) {
        try {
            final int countColumns = viewModel.openFile(uri);
            prepareDefinitionsTables4NewData(countColumns);
            viewModel.populateLoadedData();
            this.currentFile.set(Paths.get(uri));
            viewModel.getRecentItems().ifPresent(i -> i.push(Paths.get(uri).toString()));
            viewModel.setUnchanged();
        } catch (IOException | ClassNotFoundException e) {
            exceptionHandler.showAndWaitAlert(e);
        }
    }

    private Answer querySaveIfChanged() {
        if (viewModel.changedProperty().get()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("DTMG");
            Path p = this.currentFile.get().getFileName();
            alert.setHeaderText(String.format("Save file: %s?", p));

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeYes) {
                return Answer.YES;
            } else if (result.get() == buttonTypeNo) {
                return Answer.NO;
            } else {
                return Answer.CANCEL;
            }
        }
        return Answer.NO;
    }

    private boolean checkForOpenFile() {
        return this.currentFile.isNotNull().get();
    }

    private void doFileSave(String key, Object[] value) {
        if (viewModel.changedProperty().get()) {
            if (null != currentFile.get()) {
                internalSaveFile(currentFile.get().toFile());
            }
        }
    }

    private void doFileSaveAs(String key, Object[] value) {
        FileChooser fc = getFileChooser();
        try {
            configureFileChooser(getFileChooser(), "Save DTMG File");
            File file = fc.showSaveDialog(this.actionSplitPane.getScene().getWindow());
            if (file != null) {
                internalSaveFile(file);
            }
        } finally {
            fc.getExtensionFilters().clear();
        }
    }

    private boolean internalSaveFile(File file) {
        try {
            viewModel.saveFile(file.toURI());
            viewModel.setUnchanged();
        } catch (IOException e) {
            exceptionHandler.showAndWaitAlert(e);
            return true;
        }
        return false;
    }

    private void doFileExportAs(String key, Object[] value) {
        FileChooser fc = getFileChooser();
        try {
            configureFileChooser4Export(getFileChooser(), "Export DTMG File");
            File file = getFileChooser().showSaveDialog(this.actionSplitPane.getScene().getWindow());
            if (file != null) {
                try {
                    viewModel.exportFile(file.toURI());
                } catch (IOException e) {
                    exceptionHandler.showAndWaitAlert(e);
                    return;
                }
            }
        } finally {
            fc.getExtensionFilters().clear();
        }

    }

    private void doAddElseRule(String key, Object[] value) {
        final int countColumns = conditionDefinitionsTable.getColumns().size();
        final Optional<String> elseRuleName = Optional.of(ELSE_RULE_HEADER);

        if (value.length >= 2 && null != value[1]) {
            conditionDefinitionsTable.getColumns().add(createTableColumn(countColumns, elseRuleName));
            ObservableList<ObservableList<String>> newCDefns = (ObservableList<ObservableList<String>>) value[0];
            newCDefns.stream().forEach(this.viewModel.getConditionDefinitions()::add);
            conditionDefinitionsTable.refresh();

            actionDefinitionsTable.getColumns().add(createTableColumn(countColumns, elseRuleName));
            ObservableList<ObservableList<String>> newADefns = (ObservableList<ObservableList<String>>) value[1];
            newADefns.stream().peek(System.out::println).forEach(this.viewModel.getActionDefinitions()::add);
            actionDefinitionsTable.refresh();
        }
    }


    // publish(ADD_ELSE_RULE.name(), adapt(newCDefns), adapt(newADefns));

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
        if (doMoveColumns(this.conditionDefinitionsTable,
                this.actionDefinitionsTable,
                getIndex(value), DIR_RIGHT)) {
            updateColHeaders(this.conditionDefinitionsTable);
            updateColHeaders(this.actionDefinitionsTable);
        }
    }

    private void doMoveRuleLeft(String key, Object[] value) {
        OptionalInt index = getIndex(value);
        if (index.isPresent() && !isElseRule(index.getAsInt())) {
            if (doMoveColumns(this.conditionDefinitionsTable,
                    this.actionDefinitionsTable,
                    index, DIR_LEFT)) {
                updateColHeaders(this.conditionDefinitionsTable);
                updateColHeaders(this.actionDefinitionsTable);
            }
        }
    }

    private boolean isElseRule(int index) {
        boolean ret = false;
        ObservableList<ObservableList<String>> items = this.conditionDefinitionsTable.getItems();
        if (!items.isEmpty()) {
            ObservableList<String> strings = items.get(0);
            if (!strings.isEmpty()) {
                ret = Reserved.isELSE(strings.get(strings.size() - 1));
            }
        }
        return ret;
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

        final OptionalInt index = determineColumnIndex(this.conditionDefinitionsTable, this.actionDefinitionsTable, getIndex(value));

        if (index.isPresent()) {

            int newCols = this.conditionDefinitionsTable.getColumns().size() + 1;
            this.conditionDefinitionsTable.getColumns().clear();
            this.actionDefinitionsTable.getColumns().clear();

            IntStream.range(0, newCols).forEach(i -> {
                this.conditionDefinitionsTable.getColumns().add(createTableColumn(i));
                this.actionDefinitionsTable.getColumns().add(createTableColumn(i));
            });

            Tuple2<ObservableList, ObservableList> oldDefs = Tuple.of(this.conditionDefinitionsTable.getItems(), this.actionDefinitionsTable.getItems());


            Tuple2<List<? extends List<String>>, List<? extends List<String>>> newDefs = viewModel.doInsRule(index, oldDefs);

            oldDefs._1().clear();
            newDefs._1().forEach(oldDefs.$1()::add);
            oldDefs._2().clear();
            newDefs._2().forEach(oldDefs._2()::add);

            this.conditionDefinitionsTable.refresh();
            this.actionDefinitionsTable.refresh();
        }

        updateColHeaders(this.conditionDefinitionsTable);
        updateColHeaders(this.actionDefinitionsTable);

    }

    private void doInsConditionDecl(String key, Object[] value) {
        if (HAS_ELSE_RULE.test(this.conditionDefinitionsTable.getItems().get(0))) {
            doInsertRowsWithElseRule(this.conditionDeclarationsTable, this.conditionDefinitionsTable, getIndex(value),
                    () -> new ConditionDeclTableViewModel(new ConditionDecl()),
                    QMARK_SUPPLIER, () -> "C%02d");

        } else {
            doInsertRows(this.conditionDeclarationsTable, this.conditionDefinitionsTable, getIndex(value),
                    () -> new ConditionDeclTableViewModel(new ConditionDecl()),
                    QMARK_SUPPLIER, () -> "C%02d");
        }
        this.viewModel.updateRowHeader();
    }

    private void doInsActionDecl(String key, Object[] value) {
        doInsertRows(this.actionDeclarationsTable, this.actionDefinitionsTable, getIndex(value),
                () -> new ActionDeclTableViewModel(new ActionDecl()),
                DASH_SUPPLIER, () -> "A%02d");
        this.viewModel.updateRowHeader();
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
        initializeDeclarationTablesColumns(this.conditionDeclarationsTable, PREFIX_IS);

        this.conditionDeclarationsTable.getItems().clear();
        this.conditionDeclarationsTable.setItems(viewModel.getConditionDeclarations());

        this.actionDeclarationsTable.setEditable(true);
        this.actionDeclarationsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.actionDeclarationsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(actionDeclarationsTable);
        initializeDeclarationTablesColumns(this.actionDeclarationsTable, PREFIX_DO);

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
    }

    enum Answer {
        YES, NO, CANCEL
    }

}

