package de.adesso.tools.ui.menu;

import de.adesso.tools.ui.about.AboutView;
import de.adesso.tools.util.DialogHelper;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import javax.inject.Inject;

/**
 * Created by mohler on 15.01.16.
 */
public class MenuView implements FxmlView<MenuViewModel> {

    @FXML
    private MenuItem removeConditionDeclMenuItem;

    @InjectViewModel
    private MenuViewModel viewModel;

    @Inject
    private Stage primaryStage;

    public void initialize() {
        removeConditionDeclMenuItem.disableProperty().bind(viewModel.removeItemDisabledProperty());
    }

    // General

    @FXML
    public void doClose() {
        viewModel.closeAction();
    }

    @FXML
    public void doAbout() {
        Parent view = FluentViewLoader.fxmlView(AboutView.class)
                .load().getView();
        DialogHelper.showDialog(view, primaryStage, "/dtmg.css");
    }

    // Conditions

    @FXML
    public void doAddConditionDecl(ActionEvent actionEvent) {
        viewModel.addConditionDeclAction();
    }

    @FXML
    public void doRemoveConditionDecl(ActionEvent actionEvent) {
        viewModel.removeConditionDecl();
    }

    @FXML
    public void doAddRuleDef(ActionEvent actionEvent) {
        viewModel.addRuleDef();
    }

    // Actions

    @FXML
    public void doAddActionDecl(ActionEvent actionEvent) {
        viewModel.addActionDecl();
    }

    @FXML
    public void doRemoveActionDecl(ActionEvent actionEvent) {
        viewModel.removeActionDecl();
    }

    @FXML
    public void doRemoveRuleDef(ActionEvent actionEvent) {
        viewModel.removeRuleAction();
    }

    @FXML
    public void doRemoveRuleDefsWithoutAction(ActionEvent actionEvent) {
        viewModel.removeRulesWithoutAction();
    }

    @FXML
    public void doSimpleCompletenessCheck(ActionEvent actionEvent) {
        viewModel.simpleCompletenessCheckAction();
    }

    @FXML
    public void doExtendedCompletenessCheck(ActionEvent actionEvent) {
    }

    @FXML
    public void doInsertConditionDecl(ActionEvent actionEvent) {
        viewModel.insertConditionDecl();
    }

    @FXML
    public void doInsertActionDecl(ActionEvent actionEvent) {
        viewModel.insertActionDecl();
    }

    @FXML
    public void doInsertRuleDef(ActionEvent actionEvent) {
        viewModel.insertRuleDef();
    }

    @FXML
    public void doMoveActionDeclUp(ActionEvent actionEvent) {
        viewModel.moveActionDeclUp();
    }

    @FXML
    public void doMoveActionDeclDown(ActionEvent actionEvent) {
        viewModel.moveActionDeclDown();
    }

    @FXML
    public void doMoveConditionDeclUp(ActionEvent actionEvent) {
        viewModel.moveConditionDeclUp();
    }

    @FXML
    public void doMoveConditionDeclDown(ActionEvent actionEvent) {
        viewModel.moveConditionDeclDown();
    }

    @FXML
    public void doMoveRuleLeft(ActionEvent actionEvent) {
        viewModel.moveRuleLeft();
    }

    @FXML
    public void doMoveRuleRight(ActionEvent actionEvent) {
        viewModel.moveRuleRight();
    }

    public void doAddElseRule(ActionEvent actionEvent) {
        viewModel.addElseRule();
    }
}
