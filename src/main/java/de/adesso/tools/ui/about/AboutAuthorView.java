package de.adesso.tools.ui.about;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;

import javax.inject.Singleton;

@Singleton
public class AboutAuthorView implements FxmlView<AboutAuthorViewModel> {

    @InjectViewModel
    private AboutAuthorViewModel viewModel;


    @FXML
    public void openBlog() {
        viewModel.openBlog();
    }

    @FXML
    public void openTwitter() {
        viewModel.openTwitter();
    }
}
