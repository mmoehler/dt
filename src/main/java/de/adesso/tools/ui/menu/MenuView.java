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

    @FXML
    public void doClose() {
        viewModel.closeAction();
    }

    @FXML
    public void doRemoveConditionDecl(ActionEvent actionEvent) {
        viewModel.removeConditionDeclAction();
    }

    @FXML
    public void doAddConditionDecl(ActionEvent actionEvent) {
        viewModel.addConditionDeclAction();
    }

    @FXML
    public void doRemoveRuleDef(ActionEvent actionEvent) {
        viewModel.removeConditionDeclAction();
    }

    @FXML
    public void doAddConditionDef(ActionEvent actionEvent) {
        viewModel.addConditionDef();
    }

    @FXML
    public void doSimpleCompletenessCheck(ActionEvent actionEvent) {
        viewModel.simpleCompletenessCheckAction();
    }

    @FXML
    public void doExtendedCompletenessCheck(ActionEvent actionEvent) {
    }

    @FXML
    public void doAbout() {
        Parent view = FluentViewLoader.fxmlView(AboutView.class)
                .load().getView();
        DialogHelper.showDialog(view, primaryStage, "/contacts.css");
    }

    @FXML
    public void doAddActionDecl(ActionEvent actionEvent) {
        viewModel.addActionDecl();
    }

    @FXML
    public void doRemoveActionDecl(ActionEvent actionEvent) {
        viewModel.removeActionDecl();
    }

    @FXML
    public void doRemoveRuleDefsWithoutAction(ActionEvent actionEvent) {
        viewModel.removeConditionDefsWithoutAction();
    }
}
