package de.adesso.dtmg.ui.about;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.HostServices;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.function.Consumer;

public class AboutViewModel implements ViewModel {

    private final ReadOnlyStringWrapper librariesLabelText = new ReadOnlyStringWrapper("");
    // Package Private because of testing reasons
    ObservableMap<String, String> libraryLinkMap = FXCollections.observableHashMap();
    /**
     * Sadly the {@link javafx.application.HostServices} class of JavaFX is <code>final</code> so we can't mock it in
     * tests. To still be able to test link actions we have introduced this handler as a mockable indirection.
     */
    Consumer<String> onLinkClickedHandler;
    @Inject
    private HostServices hostServices;
    @Inject
    private NotificationCenter notificationCenter;

    public AboutViewModel() {

        libraryLinkMap.addListener((MapChangeListener<String, String>) change -> {
            StringBuilder labelText = new StringBuilder();

            libraryLinkMap.keySet().stream().sorted().forEach(libraryName -> {
                labelText.append("- [");
                labelText.append(libraryName);
                labelText.append("]\n");
            });

            librariesLabelText.set(labelText.toString());
        });
    }

    @PostConstruct
    public void initLibraryMap() {
        onLinkClickedHandler = hostServices::showDocument;

        libraryLinkMap.put("DataFX", "http://www.javafxdata.org/");
        libraryLinkMap.put("ControlsFX", "http://fxexperience.com/controlsfx/");
        libraryLinkMap.put("FontAwesomeFX", "https://bitbucket.org/Jerady/fontawesomefx");
        libraryLinkMap.put("Advanced-Bindings", "https://github.com/lestard/advanced-bindings");
        libraryLinkMap.put("AssertJ-JavaFX", "https://github.com/lestard/assertj-javafx");
        libraryLinkMap.put("JFX-Testrunner", "https://github.com/sialcasa/jfx-testrunner");
    }


    public void onLinkClicked(String linkText) {
        if (libraryLinkMap.containsKey(linkText)) {
            onLinkClickedHandler.accept(libraryLinkMap.get(linkText));
        }
    }

    public ReadOnlyStringProperty librariesLabelTextProperty() {
        return librariesLabelText.getReadOnlyProperty();
    }

}
