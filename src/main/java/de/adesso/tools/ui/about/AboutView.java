package de.adesso.tools.ui.about;

import de.adesso.tools.util.DialogHelper;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import org.controlsfx.control.HyperlinkLabel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AboutView implements FxmlView<AboutViewModel> {
	
	@FXML
	private HyperlinkLabel librariesLabel;
	
	@InjectViewModel
	private AboutViewModel viewModel;
	
	@Inject
	private Stage primaryStage;
	
	
	public void initialize() {
		librariesLabel.textProperty().bind(viewModel.librariesLabelTextProperty());
		librariesLabel.setOnAction(event -> {
			Hyperlink link = (Hyperlink) event.getSource();
			String str = link == null ? "" : link.getText();
			viewModel.onLinkClicked(str);
		});
	}
	
	@FXML
	public void openAuthorPage() {
		Parent view = FluentViewLoader.fxmlView(AboutAuthorView.class)
				.load().getView();
		DialogHelper.showDialog(view, primaryStage, "/dtmg.css");
	}
	
}
