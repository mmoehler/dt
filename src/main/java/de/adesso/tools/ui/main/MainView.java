package de.adesso.tools.ui.main;

import de.adesso.tools.functions.MatrixFunctions;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.DeclarationTableViewModel;
import de.adesso.tools.ui.Notifications;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.ui.dialogs.Dialogs;
import de.adesso.tools.util.OsCheck;
import de.adesso.tools.util.tuple.Tuple2;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static de.adesso.tools.functions.DtFunctions.*;

public class MainView implements FxmlView<MainViewModel> {
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
    @Inject
    private NotificationCenter notificationCenter;
    @FXML
    private TextArea console;
    @InjectViewModel
    private MainViewModel viewModel;
    private String lastKey = null;

    public MainView() {
        super();
    }

    private static <T extends DeclarationTableViewModel> void initializeDeclTableColumns(TableView<T> table) {
        List<TableColumn<T, String>> l = new ArrayList<>();
        l.add(createTableColumn("#", "lfdNr", 40, 40, 40, false, Pos.CENTER,
                (TableColumn.CellEditEvent<T, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .lfdNrProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Expression", "expression", 300, 300, Integer.MAX_VALUE, true, Pos.CENTER_LEFT,
                (TableColumn.CellEditEvent<T, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .expressionProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Indicators", "possibleIndicators", 100, 100, Integer.MAX_VALUE, true, Pos.CENTER,
                (TableColumn.CellEditEvent<T, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .possibleIndicatorsProperty().setValue(evt.getNewValue());
                }));
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

    public void initialize() {
        initializeDividerSynchronization();
        intializeTableViewScrolling();
        initializeConditionDeclTable();
        initializeObservers();
    }

    protected void initializeObservers() {
        notificationCenter.subscribe(Notifications.PREPARE_CONSOLE.name(), (key, value) ->
                console.setText(String.valueOf(value[0])));

        this.viewModel.subscribe(Notifications.REM_ACTION.name(), (key, value) -> doRemActionDecl(key, value));
        this.viewModel.subscribe(Notifications.REM_RULES_WITHOUT_ACTIONS.name(), (key, value) -> doRemRulesWithoutActions(key, value));
        this.viewModel.subscribe(Notifications.REM_RULE.name(), (key, value) -> doRemRule(key, value));
        this.viewModel.subscribe(Notifications.REM_CONDITION.name(), (key, value) -> doRemConditionDecl(key, value));

        this.viewModel.subscribe(Notifications.INS_ACTION.name(), (key, value) -> doInsActionDecl(key, value));
        this.viewModel.subscribe(Notifications.INS_RULE.name(), (key, value) -> doInsRule(key, value));
        this.viewModel.subscribe(Notifications.INS_CONDITION.name(), (key, value) -> doInsConditionDecl(key, value));

        this.viewModel.subscribe(Notifications.ADD_RULE.name(), (key, value) -> doAddRule(key, value));
        this.viewModel.subscribe(Notifications.MOVE_ACTION_DECL_UP.name(), (key, value) -> doMoveActionDeclUp(key, value));
        this.viewModel.subscribe(Notifications.MOVE_ACTION_DECL_DOWN.name(), (key, value) -> doMoveActionDeclDown(key, value));
        this.viewModel.subscribe(Notifications.MOVE_COND_DECL_UP.name(), (key, value) -> doMoveConditionDeclUp(key, value));
        this.viewModel.subscribe(Notifications.MOVE_COND_DECL_DOWN.name(), (key, value) -> doMoveConditionDeclDown(key, value));
        this.viewModel.subscribe(Notifications.MOVE_RULE_LEFT.name(), (key, value) -> doMoveRuleLeft(key, value));
        this.viewModel.subscribe(Notifications.MOVE_RULE_RIGHT.name(), (key, value) -> doMoveRuleRight(key, value));
        this.viewModel.subscribe(Notifications.ADD_ELSE_RULE.name(), (key, value) -> doAddElseRule(key, value));

    }

    private void doAddElseRule(String key, Object[] value) {
        final int countColumns = conditionDefinitionsTable.getColumns().size();
        final Optional<String> name = Optional.of(ELSE_RULE_HEADER);
        conditionDefinitionsTable.getColumns().add(createTableColumn(countColumns, name));
        ObservableList<ObservableList<String>> newDefns = (ObservableList<ObservableList<String>>) value[0];
        newDefns.stream().forEach(this.viewModel.getConditionDefinitions()::add);
        conditionDefinitionsTable.refresh();

        actionDefinitionsTable.getColumns().add(createTableColumn(countColumns, name));
        if (value.length >= 2 && null != value[1]) {
            newDefns = (ObservableList<ObservableList<String>>) value[1];
            newDefns.stream().forEach(this.viewModel.getActionDefinitions()::add);
            actionDefinitionsTable.refresh();
        }
    }

    private void doMoveConditionDeclDown(String key, Object[] value) {

    }

    private void doMoveConditionDeclUp(String key, Object[] value) {

    }

    private void doMoveActionDeclDown(String key, Object[] value) {
        doMoveRows(this.viewModel.getConditionDefinitions(), this.viewModel.getActionDefinitions(),
                this.conditionDefinitionsTable, this.actionDefinitionsTable, value, true);
        viewModel.updateRowHeader();
    }

    private void doMoveActionDeclUp(String key, Object[] value) {
        doMoveRows(this.viewModel.getConditionDefinitions(), this.viewModel.getActionDefinitions(),
                this.conditionDefinitionsTable, this.actionDefinitionsTable, value, false);
        viewModel.updateRowHeader();
    }

    private void doMoveRuleRight(String key, Object[] value) {
        doMoveColumns(this.viewModel.getConditionDefinitions(), this.viewModel.getActionDefinitions(),
                this.conditionDefinitionsTable, this.actionDefinitionsTable, value, true);
        updateColHeaders(this.conditionDefinitionsTable, this.actionDefinitionsTable);
    }

    private void doMoveRuleLeft(String key, Object[] value) {
        doMoveColumns(this.viewModel.getConditionDefinitions(), this.viewModel.getActionDefinitions(),
                this.conditionDefinitionsTable, this.actionDefinitionsTable, value, false);
        updateColHeaders(this.conditionDefinitionsTable, this.actionDefinitionsTable);
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
        doRemoveColumns(this.viewModel.getConditionDefinitions(), this.viewModel.getActionDefinitions(),
                this.conditionDefinitionsTable, this.actionDefinitionsTable, value);
        updateColHeaders(this.conditionDefinitionsTable, this.actionDefinitionsTable);
    }

    private void doInsRule(String key, Object[] value) {
        doInsertColumns(this.viewModel.getConditionDefinitions(), this.viewModel.getActionDefinitions(),
                this.conditionDefinitionsTable, this.actionDefinitionsTable, value, () -> QMARK);
        updateColHeaders(this.conditionDefinitionsTable, this.actionDefinitionsTable);
    }

    private void doInsConditionDecl(String key, Object[] value) {
        doInsertRows(this.viewModel.getConditionDeclarations(), this.viewModel.getConditionDefinitions(),
                this.conditionDeclarationsTable, this.conditionDefinitionsTable, value,
                () -> new ConditionDeclTableViewModel(new ConditionDecl()),
                () -> QMARK);
        this.viewModel.updateRowHeader();
    }

    private void doInsActionDecl(String key, Object[] value) {
        doInsertRows(this.viewModel.getActionDeclarations(), this.viewModel.getActionDefinitions(),
                this.actionDeclarationsTable, this.actionDefinitionsTable, value,
                () -> new ActionDeclTableViewModel(new ActionDecl()),
                () -> "?");
        this.viewModel.updateRowHeader();
    }


    private void doRemRulesWithoutActions(String kk, Object[] value) {
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
    }

    private void doRemConditionDecl(String key, Object[] value) {
        doRemoveRows(this.viewModel.getConditionDeclarations(),
                this.viewModel.getConditionDefinitions(),
                this.conditionDeclarationsTable,
                this.conditionDefinitionsTable, value);

        // but here this is not enough! If hte count of columns after the row removal is greater thean
        // the max possible count, than the difference of columns must also be deleted.
        int cols = this.viewModel.getConditionDefinitions().size();
        int start = determineMaxColumns(viewModel.getConditionDeclarations());

        // TODO implement >>> doRemConditionDecl(String key, Object[] value) !!
        /* --
        int end = Range.newBuilder().withFrom(start, end - start)
        viewModel.removeRulesIn(Range range)
        -- */

        this.viewModel.updateRowHeader();
    }

    private void doRemActionDecl(String key, Object[] value) {
        doRemoveRows(this.viewModel.getActionDeclarations(), this.viewModel.getActionDefinitions(),
                this.actionDeclarationsTable, this.actionDefinitionsTable, value);
        this.viewModel.updateRowHeader();
    }


    protected void initializeConditionDefnsTable(int countColumns, boolean shouldPopulateData) {
        this.conditionDefinitionsTable.setEditable(true);
        this.conditionDefinitionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDefinitionsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(conditionDefinitionsTable);
        initializeConditionDefinitionTableColumns(countColumns);

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

    protected void initializeConditionDeclTable() {
        this.conditionDeclarationsTable.setEditable(true);
        this.conditionDeclarationsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDeclarationsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeConditionDeclFocusHandling();
        initializeTableKeyboardHandling(conditionDeclarationsTable);

        initializeDeclTableColumns(this.conditionDeclarationsTable);


        this.conditionDeclarationsTable.getItems().clear();
        this.conditionDeclarationsTable.setItems(viewModel.getConditionDeclarations());

        this.actionDeclarationsTable.setEditable(true);
        this.actionDeclarationsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.actionDeclarationsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(actionDeclarationsTable);
        initializeDeclTableColumns(this.actionDeclarationsTable);

        this.actionDeclarationsTable.getItems().clear();
        this.actionDeclarationsTable.setItems(viewModel.getActionDeclarations());

    }

    private void initializeConditionDefinitionTableColumns(int countColumns) {
        conditionDefinitionsTable.getColumns().clear();
        final int cols = Math.min(determineMaxColumns(viewModel.getConditionDeclarations()), countColumns);
        IntStream.range(0, cols)
                .mapToObj(i -> createTableColumn(i))
                .forEach(a -> conditionDefinitionsTable.getColumns().add(a));
        conditionDefinitionsTable.refresh();

        actionDefinitionsTable.getColumns().clear();
        IntStream.range(0, cols)
                .mapToObj(i -> createTableColumn(i))
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
                    initializeConditionDefnsTable(dlgResult._1(), dlgResult._2());
                }
            }
        });
    }

    private void initializeDividerSynchronization() {
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

