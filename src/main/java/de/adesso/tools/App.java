package de.adesso.tools;

import de.adesso.tools.events.TriggerShutdownEvent;
import de.adesso.tools.ui.main.MainView;
import de.adesso.tools.ui.main.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.cdi.MvvmfxCdiApplication;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends MvvmfxCdiApplication {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    @Inject
    private ResourceBundle resourceBundle;

    public static void main(String... args) {
        Locale.setDefault(Locale.ENGLISH);
        launch(args);
    }

    //@Inject
    //private Repository repository;

    @Override
    public void initMvvmfx() throws Exception {
    }

    @Override
    public void startMvvmfx(Stage stage) throws Exception {
        LOG.info("Starting the Application");
        MvvmFX.setGlobalResourceBundle(resourceBundle);

        stage.setTitle(resourceBundle.getString("window.title"));

        ViewTuple<MainView, MainViewModel> main = FluentViewLoader.fxmlView(MainView.class).load();


        Scene rootScene = new Scene(main.getView());

        rootScene.getStylesheets().add("/contacts.css");

        stage.setScene(rootScene);
        stage.show();
    }

    /**
     * The shutdown of the application can be triggered by firing the {@link TriggerShutdownEvent} CDI event.
     */
    public void triggerShutdown(@Observes TriggerShutdownEvent event) {
        LOG.info("Application will now shut down");
        Platform.exit();
    }
}
