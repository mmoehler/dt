package de.adesso.dtmg.ui.about;

import de.adesso.dtmg.util.DialogHelper;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AboutView implements FxmlView<AboutViewModel> {

    @Inject
    private Stage primaryStage;


    @FXML
    public void openAuthorPage() {
        Parent view = FluentViewLoader.fxmlView(AboutAuthorView.class)
                .load().getView();
        DialogHelper.showDialog(view, primaryStage, "/dtmg.css");
    }

}
