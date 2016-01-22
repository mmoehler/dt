package de.adesso.tools.ui.condition;

import de.adesso.tools.ui.dialogs.Dialogs;
import de.adesso.tools.ui.scopes.RuleScope;
import de.adesso.tools.util.func.DtOps;
import de.adesso.tools.util.tuple.Tuple2;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static de.adesso.tools.ui.TableColumnOps.createTableColumn;

public class ConditionView implements FxmlView<ConditionViewModel> {
    @FXML
    public TableView<ConditionDeclTableViewModel> conditionDeclTable;

    @FXML
    public TableView conditionDefnsTable;

    @FXML
    public SplitPane conditionSplitPane;

    @InjectViewModel
    public ConditionViewModel viewModel;
    private String lastKey = null;

    public ConditionView() {
    }

    public void initialize() {
        initializeDividerSynchronization();
        initializeConditionDeclTable();

    }

    protected void initializeConditionDefnsTable(int countColumns, boolean shouldPopulateData) {
        this.conditionDefnsTable.setEditable(true);
        this.conditionDefnsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDefnsTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeTableKeyboardHandling(conditionDefnsTable);
        initializeConditionDefnsTableColumns(countColumns, shouldPopulateData);

        this.conditionDefnsTable.getItems().clear();
        this.conditionDefnsTable.setItems(viewModel.intializeConditionDefnsData(countColumns, shouldPopulateData));
    }

    protected void initializeConditionDeclTable() {
        this.conditionDeclTable.setEditable(true);
        this.conditionDeclTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDeclTable.getSelectionModel().setCellSelectionEnabled(true);

        initializeConditionDeclFocusHandling();
        initializeTableKeyboardHandling(conditionDeclTable);
        initializeConditionDeclTableColumns();

        this.conditionDeclTable.getItems().clear();
        this.conditionDeclTable.setItems(viewModel.getDecls());

    }

    private void initializeConditionDefnsTableColumns(int countColumns, boolean shouldPopulateData) {
        final int cols = Math.min(DtOps.determineMaxColumns(viewModel.getDecls()), countColumns);
        IntStream.range(0,cols)
                .mapToObj(i -> createTableColumn(i))
                .forEach(a -> conditionDefnsTable.getColumns().add(a));
    }

    private void initializeConditionDeclTableColumns() {
        List<TableColumn<ConditionDeclTableViewModel, String>> l = new ArrayList<>();
        l.add(createTableColumn("#", "lfdNr", 40, 40, 40, false,
                (TableColumn.CellEditEvent<ConditionDeclTableViewModel, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .lfdNrProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Expression", "expression", 300, 300, Integer.MAX_VALUE, true,
                (TableColumn.CellEditEvent<ConditionDeclTableViewModel, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .expressionProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Indicators", "possibleIndicators", 100, 100, Integer.MAX_VALUE, true,
                (TableColumn.CellEditEvent<ConditionDeclTableViewModel, String> evt) -> {
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .possibleIndicatorsProperty().setValue(evt.getNewValue());
                }));
        l.forEach(x -> this.conditionDeclTable.getColumns().add(x));
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
            //I decided not to override the default tab behavior
            //using ctrl tab for cell traversal, but arrow keys are better
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
            TablePosition tp;
            if (!t.isControlDown() &&
                    (t.getCode().isLetterKey() || t.getCode().isDigitKey())) {
                lastKey = t.getText();
                tp = table.getFocusModel().getFocusedCell();
                table.edit(tp.getRow(), tp.getTableColumn());
                lastKey = null;
            }
        });
    }

    private void initializeConditionDeclFocusHandling() {
        this.conditionDeclTable.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (oldValue && !newValue) {
                if (!isParent(conditionDeclTable, conditionDeclTable.getScene().getFocusOwner())) {
                    if (this.conditionDefnsTable.getItems().isEmpty()) {
                        Tuple2<Integer, Boolean> dlgResult = Dialogs.acceptOrDefineRuleCountDialog0(
                                DtOps.determineMaxColumns(this.viewModel.getDecls()), false);
                        initializeConditionDefnsTable(dlgResult._1(), dlgResult._2());
                    }
                }
            }
        });
    }

    private void initializeDividerSynchronization() {
        final RuleScope scope = viewModel.getRuleScope();
        scope.conditionDividerPos.bind(conditionSplitPane.getDividers().get(0).positionProperty());
        scope.actionDividerPos.addListener((a, b, c) ->
                conditionSplitPane.getDividers().get(0).setPosition(c.doubleValue()));
    }

    private boolean isParent(Parent parent, Node child) {
        if (child == null) {
            return false;
        }
        Parent curr = child.getParent();
        while (curr != null) {
            if (curr == parent) {
                return true;
            }
            curr = curr.getParent();
        }
        return false;
    }

}
