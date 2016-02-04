package de.adesso.tools.ui.action;

import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionViewModelNotifications;
import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import static de.adesso.tools.ui.TableColumnOps.createTableColumn;
import static de.adesso.tools.util.func.DtOps.determineMaxColumns;

public class ActionView implements FxmlView<ActionViewModel>, Initializable {
    @FXML
    public SplitPane actionSplitPane;

    @FXML
    public TableView actionDeclTable;

    @FXML
    public TableView actionDefnsTable;

    @InjectViewModel
    public ActionViewModel viewModel;

    private String lastKey = null;

    public ActionView() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDividerSynchronization();
        initializeActionDeclTable();
        initializeObservers();
    }

    protected void initializeObservers() {
        this.viewModel.subscribe(ConditionViewModelNotifications.CONDITIONDEF_ADD.name(), (key, value) ->{
            final int countColumns = actionDefnsTable.getColumns().size();
            actionDefnsTable.getColumns().add(createTableColumn(countColumns));
            final ObservableList<ObservableList<String>> newDefns = (ObservableList<ObservableList<String>>) value[0];
            newDefns.stream().forEach(this.viewModel.getDefns()::add);
            actionDefnsTable.refresh();
        });
    }

    protected void initializeConditionDefnsTable(int countColumns, boolean shouldPopulateData) {
        this.actionDefnsTable.setEditable(true);
        this.actionDefnsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.actionDefnsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(actionDefnsTable);
        initializeActionDefnsTableColumns(countColumns);

        this.actionDefnsTable.getItems().clear();
        this.actionDefnsTable.setItems(viewModel.initializeActionDefnsData(countColumns, shouldPopulateData));
    }

    protected void initializeActionDeclTable() {
        this.actionDeclTable.setEditable(true);
        this.actionDeclTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.actionDeclTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(actionDeclTable);
        initializeActionDeclTableColumns();

        this.actionDeclTable.getItems().clear();
        this.actionDeclTable.setItems(viewModel.getDecls());

    }

    private void initializeActionDefnsTableColumns(int countColumns) {
        actionDefnsTable.getColumns().clear();
        final int cols = Math.min(determineMaxColumns(viewModel.getDecls()), countColumns);
        IntStream.rangeClosed(0,cols) // +1 column for the ELSE rule
                .mapToObj(i -> createTableColumn(i))
                .forEach(a -> actionDefnsTable.getColumns().add(a));
        actionDefnsTable.refresh();
    }

    private void initializeActionDeclTableColumns() {
        List<TableColumn<ActionDeclTableViewModel, String>> l = new ArrayList<>();
        l.add(createTableColumn("#", "lfdNr", 40, 40, 40, false,
                (TableColumn.CellEditEvent<ActionDeclTableViewModel, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .lfdNrProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Expression", "expression", 300, 300, Integer.MAX_VALUE, true,
                (TableColumn.CellEditEvent<ActionDeclTableViewModel, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .expressionProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Indicators", "possibleIndicators", 100, 100, Integer.MAX_VALUE, true,
                (TableColumn.CellEditEvent<ActionDeclTableViewModel, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .possibleIndicatorsProperty().setValue(evt.getNewValue());
                }));
        l.forEach(x -> this.actionDeclTable.getColumns().add(x));
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

        table.setOnKeyPressed((KeyEvent t) -> {
            System.out.println("t = " + t);
            TablePosition tp;
            if (!t.isControlDown() && (t.getCode().isLetterKey() || t.getCode().isDigitKey() || t.getCode()==KeyCode.MINUS || t.getCode()==KeyCode.NUMBER_SIGN)) {
                lastKey = t.getText();
                tp = table.getFocusModel().getFocusedCell();
                table.edit(tp.getRow(), tp.getTableColumn());
                lastKey = null;
            }
        });
    }

    private void initializeDividerSynchronization() {
        final RuleScope scope = viewModel.getRuleScope();
        scope.actionDividerPos.bind(actionSplitPane.getDividers().get(0).positionProperty());
        scope.conditionDividerPos.addListener((a,b,c) ->
                actionSplitPane.getDividers().get(0).setPosition(c.doubleValue()));
    }



}
