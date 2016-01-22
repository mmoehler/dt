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
    private MenuItem removeMenuItem;

    @InjectViewModel
    private MenuViewModel viewModel;

    @Inject
    private Stage primaryStage;

    public void initialize() {
        removeMenuItem.disableProperty().bind(viewModel.removeItemDisabledProperty());
    }

    @FXML
    public void close() {
        viewModel.closeAction();
    }

    @FXML
    public void removeCondition(ActionEvent actionEvent) {
        viewModel.removeConditionDeclAction();
    }

    @FXML
    public void addCondition(ActionEvent actionEvent) {
        viewModel.addConditionDeclAction();
    }

    @FXML
    public void about() {
        Parent view = FluentViewLoader.fxmlView(AboutView.class)
                .load().getView();
        DialogHelper.showDialog(view, primaryStage, "/contacts.css");
    }


}
