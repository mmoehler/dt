package de.adesso.dtmg.ui.about;

import de.saxsys.mvvmfx.ViewModel;
import javafx.application.HostServices;

import javax.inject.Inject;

public class AboutAuthorViewModel implements ViewModel {

    @Inject
    private HostServices hostServices;


    public void openBlog() {
        hostServices.showDocument("http://www.adesso.de");
    }

    public void openTwitter() {
        hostServices.showDocument("mailto:moehler@adesso.de?subject=DTMG");
    }
}
