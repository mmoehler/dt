package de.adesso.tools.ui.condition;

import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

import static de.adesso.tools.ui.TableColumnOps.createTableColumn;

public class ConditionView implements FxmlView<ConditionViewModel> {
    @FXML
    public TableView<ConditionDeclTableViewModel> conditionDeclTable;

    @FXML
    public TableView conditionDefTable;

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

    private void initializeConditionDeclTable() {
        this.conditionDeclTable.setEditable(true);
        this.conditionDeclTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.conditionDeclTable.getSelectionModel().setCellSelectionEnabled(true);
/*
        this.conditionDeclTable.getFocusModel().focusedCellProperty().addListener(
                (ObservableValue<? extends TablePosition> observable, TablePosition oldValue, TablePosition newValue) -> {
                    if (newValue != null) {
                        Platform.runLater(() -> this.conditionDeclTable.edit(newValue.getRow(), newValue.getTableColumn()));
                    }
                }
        );
*/

        this.conditionDeclTable.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (this.conditionDeclTable.getEditingCell() == null && t.getCode() == KeyCode.ENTER) {
                if (t.isShiftDown()) {
                    this.conditionDeclTable.getSelectionModel().selectAboveCell();
                } else {
                    this.conditionDeclTable.getSelectionModel().selectBelowCell();
                }
                t.consume();
            }
            //I decided not to override the default tab behavior
            //using ctrl tab for cell traversal, but arrow keys are better
            if (t.isControlDown() && t.getCode() == KeyCode.TAB) {
                if (t.isShiftDown()) {
                    this.conditionDeclTable.getSelectionModel().selectLeftCell();
                } else {
                    this.conditionDeclTable.getSelectionModel().selectRightCell();
                }
                t.consume();
            }
        });

        this.conditionDeclTable.setOnKeyPressed((KeyEvent t) -> {
            TablePosition tp;
            if (!t.isControlDown() &&
                    (t.getCode().isLetterKey() || t.getCode().isDigitKey())) {
                lastKey = t.getText();
                tp = this.conditionDeclTable.getFocusModel().getFocusedCell();
                this.conditionDeclTable.edit(tp.getRow(), tp.getTableColumn());
                lastKey = null;
            }
        });
/*
        tv.setOnKeyReleased((KeyEvent t) -> {
            TablePosition tp;
            switch (t.getCode()) {
                case INSERT:
                    items.add(new LineItem("",0d,0));//maybe try adding at position
                    break;
                case DELETE:
                    tp = tv.getFocusModel().getFocusedCell();
                    if (tp.getTableColumn() == descCol) {
                        deletedLines.push(items.remove(tp.getRow()));
                    } else { //maybe delete cell value
                    }
                    break;
                case Z:
                    if (t.isControlDown()) {
                        if (!deletedLines.isEmpty()) {
                            items.add(deletedLines.pop());
                        }
                    }
            }
        });
*/


        List<TableColumn<ConditionDeclTableViewModel, String>> l = new ArrayList<>();
        l.add(createTableColumn("#", "lfdNr", 40, 40, 40, false,
                (TableColumn.CellEditEvent<ConditionDeclTableViewModel, String> evt) -> {
                    System.err.println(evt.getNewValue());
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .lfdNrProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Expression", "expression", 300, 300, Integer.MAX_VALUE, true,
                (TableColumn.CellEditEvent<ConditionDeclTableViewModel, String> evt) -> {
                    System.err.println(evt.getNewValue());
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .expressionProperty().setValue(evt.getNewValue());
                }));
        l.add(createTableColumn("Indicators", "possibleIndicators", 100, 100, Integer.MAX_VALUE, true,
                (TableColumn.CellEditEvent<ConditionDeclTableViewModel, String> evt) -> {
                    System.err.println(evt.getNewValue());
                    evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                            .possibleIndicatorsProperty().setValue(evt.getNewValue());
                }));
        l.forEach(x -> this.conditionDeclTable.getColumns().add(x));
        this.conditionDeclTable.getItems().clear();
        this.conditionDeclTable.setItems(viewModel.getDecls());
    }

    private void initializeDividerSynchronization() {
        final RuleScope scope = viewModel.getRuleScope();
        scope.conditionDividerPos.bind(conditionSplitPane.getDividers().get(0).positionProperty());
        scope.actionDividerPos.addListener((a, b, c) ->
                conditionSplitPane.getDividers().get(0).setPosition(c.doubleValue()));
    }


}
