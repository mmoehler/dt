package de.adesso.tools.ui.main;

import de.adesso.tools.ui.condition.ConditionViewModelNotifications;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import javax.inject.Inject;

public class MainView implements FxmlView<MainViewModel> {
    @FXML
    private TextArea console;

    @InjectViewModel
    private MainViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public MainView() {
        super();
    }

    public void initialize() {
        notificationCenter.subscribe(ConditionViewModelNotifications.PREPARE_CONSOLE.name(), (key, value) ->
                console.setText(String.valueOf(value[0])));
    }

}
