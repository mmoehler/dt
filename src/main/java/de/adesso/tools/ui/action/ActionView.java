package de.adesso.tools.ui.action;

import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

import static de.adesso.tools.ui.TableColumnOps.createTableColumn;

public class ActionView implements FxmlView<ActionViewModel>, Initializable {
    @FXML
    public SplitPane actionSplitPane;

    @FXML
    public TableView actionDeclTable;

    @FXML
    public TableView actionDefTable;

    @InjectViewModel
    public ActionViewModel viewModel;

    public ActionView() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDividerSynchronization();
        initializeActionDeclTable();
    }

    private void initializeDividerSynchronization() {
        final RuleScope scope = viewModel.getRuleScope();
        scope.actionDividerPos.bind(actionSplitPane.getDividers().get(0).positionProperty());
        scope.conditionDividerPos.addListener((a,b,c) ->
                actionSplitPane.getDividers().get(0).setPosition(c.doubleValue()));
    }


    private void initializeActionDeclTable() {
        this.actionDeclTable.getColumns().addAll(

                createTableColumn("#", "lfdNr", 40, 40, 40, false, null),
        /*
                        (TableColumn.CellEditEvent<ActionDeclTableViewModel, String> evt) -> {
                            evt.getTableView().getItems().get(evt.getTablePosition().getRow())
                                    .lfdNrProperty().setValue(evt.getNewValue());
                        }));
                        */
                createTableColumn("Expression", "expression", 300, 300, Integer.MAX_VALUE, true, null),
                createTableColumn("Indicators", "possibleIndicators", 100, 100, Integer.MAX_VALUE, true, null)
        );
        this.actionDeclTable.setItems(viewModel.getDecls());
    }
}
