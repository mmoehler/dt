package de.adesso.tools.ui.about;

import de.saxsys.mvvmfx.ViewModel;
import javafx.application.HostServices;

import javax.inject.Inject;

public class AboutAuthorViewModel implements ViewModel {
	
	@Inject
	private HostServices hostServices;
	
	
	public void openBlog() {
		hostServices.showDocument("http://www.lestard.eu");
	}
	
	public void openTwitter() {
		hostServices.showDocument("https://twitter.com/manuel_mauky");
	}
}
