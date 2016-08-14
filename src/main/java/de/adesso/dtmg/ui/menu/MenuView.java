package de.adesso.dtmg.ui.menu;

import de.adesso.dtmg.ui.about.AboutView;
import de.adesso.dtmg.ui.tools.QuineMcCluskeyView;
import de.adesso.dtmg.util.DialogHelper;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URI;
import java.nio.file.Paths;

/**
 * Created by mohler ofList 15.01.16.
 */
public class MenuView implements FxmlView<MenuViewModel> {

    @FXML
    private MenuItem consolidateRules;

    @FXML
    private Menu fileOpenRecent;

    @FXML
    private MenuItem addElseRuleMenuItem;


    @InjectViewModel
    private MenuViewModel viewModel;

    @Inject
    private Stage primaryStage;

    public void initialize() {
        consolidateRules.disableProperty().bind(viewModel.consolidateRulesProperty());
        addElseRuleMenuItem.disableProperty().bind(viewModel.elseRuleSetProperty());

        final RecentItems recentItems = viewModel.getRecentItems();
       // fileOpenRecent.setDisable(recentItems.getItems().isEmpty());
        final ToggleGroup recentItemsGroup = new ToggleGroup();
        for (String path : recentItems.getItems()) {
            RadioMenuItem recentFile = new RadioMenuItem(path);
            recentFile.setUserData(Paths.get(path).toUri());
            recentFile.setToggleGroup(recentItemsGroup);
            fileOpenRecent.getItems().add(recentFile);
        }

        recentItemsGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (recentItemsGroup.getSelectedToggle() != null) {
                    URI path =
                            (URI) recentItemsGroup.getSelectedToggle().getUserData();
                    viewModel.fileOpen(path);
                }
            }
        });
    }

    // General

    @FXML
    public void doClose() {
        viewModel.closeAction();
    }

    @FXML
    public void doAddConditionDecl(ActionEvent actionEvent) {
        viewModel.addConditionDeclAction();
    }

    @FXML
    public void doRemoveConditionDecl(ActionEvent actionEvent) {
        viewModel.removeConditionDecl();
    }

    @FXML
    public void doAddRuleDef(ActionEvent actionEvent) {
        viewModel.addRuleDef();
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
    public void doRemoveRuleDef(ActionEvent actionEvent) {
        viewModel.removeRuleAction();
    }

    @FXML
    public void doInsertConditionDecl(ActionEvent actionEvent) {
        viewModel.insertConditionDecl();
    }

    @FXML
    public void doInsertActionDecl(ActionEvent actionEvent) {
        viewModel.insertActionDecl();
    }

    @FXML
    public void doInsertRuleDef(ActionEvent actionEvent) {
        viewModel.insertRuleDef();
    }

    @FXML
    public void doMoveActionDeclUp(ActionEvent actionEvent) {
        viewModel.moveActionDeclUp();
    }

    @FXML
    public void doMoveActionDeclDown(ActionEvent actionEvent) {
        viewModel.moveActionDeclDown();
    }

    @FXML
    public void doMoveConditionDeclUp(ActionEvent actionEvent) {
        viewModel.moveConditionDeclUp();
    }

    @FXML
    public void doMoveConditionDeclDown(ActionEvent actionEvent) {
        viewModel.moveConditionDeclDown();
    }

    @FXML
    public void doMoveRuleLeft(ActionEvent actionEvent) {
        viewModel.moveRuleLeft();
    }

    @FXML
    public void doMoveRuleRight(ActionEvent actionEvent) {
        viewModel.moveRuleRight();
    }

    @FXML
    public void doAddElseRule(ActionEvent actionEvent) {
        viewModel.addElseRule();
    }

    @FXML
    public void doFileNew(ActionEvent actionEvent) {
        viewModel.fileNew();
    }

    @FXML
    public void doFileOpen(ActionEvent actionEvent) {
        viewModel.fileOpen();
    }

    @FXML
    public void doFileSave(ActionEvent actionEvent) {
        viewModel.fileSave();
    }

    @FXML
    public void doFileSaveAs(ActionEvent actionEvent) {
        viewModel.fileSaveAs();
    }

    @FXML
    public void doConsolidateRules(ActionEvent actionEvent) {
        viewModel.consolidateRulesAction();
    }

    @FXML
    public void doFormalCompletenessCheck(ActionEvent actionEvent) {
        viewModel.formalCompletenessCheckAction();
    }

    @FXML
    public void doStructuralAnalysis(ActionEvent actionEvent) {
        viewModel.structuralAnalysisAction();
    }

    @FXML
    public void doCompleteReport(ActionEvent actionEvent) {
        viewModel.completeReportAction();
    }

    @FXML
    public void doAbout() {
        Parent view = FluentViewLoader.fxmlView(AboutView.class).load().getView();
        DialogHelper.showDialog(view, primaryStage, "/about.css");
    }

    public void doQuineMcCluskey(ActionEvent actionEvent) {
        Parent view = FluentViewLoader.fxmlView(QuineMcCluskeyView.class).load().getView();
        Stage dialog = DialogHelper.showDialog(view, primaryStage, "/about.css");
        viewModel.registerQuineMcCluskeyDialog(dialog);
    }

    public void doFileExportAs(ActionEvent actionEvent) {
        viewModel.fileExportAs();
    }

    public void doDocumentDeclaration(ActionEvent actionEvent) {
        viewModel.documentDeclaration();
    }
}
