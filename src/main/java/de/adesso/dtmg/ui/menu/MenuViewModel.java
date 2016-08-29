package de.adesso.dtmg.ui.menu;

import de.adesso.dtmg.events.*;
import de.adesso.dtmg.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.prefs.Preferences;

/**
 * Created by mohler ofList 15.01.16.
 */
@Singleton
public class MenuViewModel implements ViewModel {

    private final SimpleBooleanProperty consolidateRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty removeDuplicateRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty missingRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty elseRuleSet = new SimpleBooleanProperty(false);

    @Inject
    private Event<TriggerShutdownEvent> shouldCloseEvent;
    @Inject
    private Event<RemoveConditionDeclEvent> removeConditionDeclEvent;
    @Inject
    private Event<AddConditionDeclEvent> addConditionDeclEvent;
    @Inject
    private Event<AddRuleDefEvent> addRuleDefEvent;
    @Inject
    private Event<FormalCompletenessCheckEvent> formalCompletenessCheckEvent;
    @Inject
    private Event<AddActionDeclEvent> addActionDeclEvent;
    @Inject
    private Event<RemoveActionDeclEvent> removeActionDeclEvent;
    @Inject
    private Event<RemoveRuleEvent> removeRuleEvent;
    @Inject
    private Event<InsertConditionDeclEvent> insertConditionDeclEvent;
    @Inject
    private Event<InsertActionDeclEvent> insertActionDeclEvent;
    @Inject
    private Event<InsertRuleDefEvent> insertRuleDefEvent;
    @Inject
    private Event<MoveActionDeclUpEvent> moveActionDeclUpEvent;
    @Inject
    private Event<MoveActionDeclDownEvent> moveActionDeclDownEvent;
    @Inject
    private Event<MoveConditionDeclUpEvent> moveConditionDeclUpEvent;
    @Inject
    private Event<MoveConditionDeclDownEvent> moveConditionDeclDownEvent;
    @Inject
    private Event<MoveRuleLeftEvent> moveRuleLeftEvent;
    @Inject
    private Event<MoveRuleRightEvent> moveRuleRightEvent;
    @Inject
    private Event<AddElseRuleEvent> addElseRuleEvent;
    @Inject
    private Event<ConsolidateRulesEvent> consolidateRulesEvent;
    @Inject
    private Event<CompleteReportEvent> completeReportEvent;
    @Inject
    private Event<StructuralAnalysisEvent> structuralAnalysisEvent;
    @Inject
    private Event<FileNewEvent> fileNewEvent;
    @Inject
    private Event<FileOpenEvent> fileOpenEvent;
    @Inject
    private Event<FileSaveAsEvent> fileSaveAsEvent;
    @Inject
    private Event<FileSaveEvent> fileSaveEvent;
    @Inject
    private Event<FileExportAsEvent> fileExportAsEvent;
    @Inject
    private Event<DocumentDeclarationEvent> documentDeclarationEvent;

    @Inject
    private Event<GenerateUsingLineMaskEvent> generateUsingLineMaskEvent;
    @Inject
    private Event<GenerateUsingVeinottEvent> generateUsingVeinottEvent;
    @Inject
    private Event<GenerateUsingStraightScanEvent> generateUsingStraightScanEvent;
    @Inject
    private Event<GenerateUsingTreeMethodEvent> generateUsingTreeMethodEvent;


    private RecentItems recentItems;

    @InjectScope
    private RuleScope mdScope;


    public void initialize() {
        consolidateRules.bind(mdScope.consolidateRulesProperty());
        elseRuleSet.bind(mdScope.elseRuleProperty());
        recentItems = new RecentItems(10, Preferences.userRoot().node("dtmg/recent.files"));
        mdScope.recentItems(recentItems);
    }

    public RecentItems getRecentItems() {
        return recentItems;
    }

    public boolean getConsolidateRules() {
        return consolidateRules.get();
    }

    public SimpleBooleanProperty consolidateRulesProperty() {
        return consolidateRules;
    }

    public boolean getElseRuleSet() {
        return elseRuleSet.get();
    }

    public SimpleBooleanProperty elseRuleSetProperty() {
        return elseRuleSet;
    }

    public void closeAction() {
        shouldCloseEvent.fire(new TriggerShutdownEvent());
    }

    public void removeRuleAction() {
        removeRuleEvent.fire(new RemoveRuleEvent());
    }

    public void addConditionDeclAction() {
        addConditionDeclEvent.fire(new AddConditionDeclEvent());
    }

    public void addRuleDef() {
        addRuleDefEvent.fire(new AddRuleDefEvent());
    }

    public void addActionDecl() {
        addActionDeclEvent.fire(new AddActionDeclEvent());
    }

    public void removeActionDecl() {
        removeActionDeclEvent.fire(new RemoveActionDeclEvent());
    }

    public void insertConditionDecl() {
        insertConditionDeclEvent.fire(new InsertConditionDeclEvent());
    }

    public void insertActionDecl() {
        insertActionDeclEvent.fire(new InsertActionDeclEvent());
    }

    public void insertRuleDef() {
        insertRuleDefEvent.fire(new InsertRuleDefEvent());
    }

    public void removeConditionDecl() {
        removeConditionDeclEvent.fire(new RemoveConditionDeclEvent());
    }

    public void moveActionDeclUp() {
        moveActionDeclUpEvent.fire(new MoveActionDeclUpEvent());
    }

    public void moveActionDeclDown() {
        moveActionDeclDownEvent.fire(new MoveActionDeclDownEvent());
    }

    public void moveConditionDeclUp() {
        moveConditionDeclUpEvent.fire(new MoveConditionDeclUpEvent());
    }

    public void moveConditionDeclDown() {
        moveConditionDeclDownEvent.fire(new MoveConditionDeclDownEvent());
    }

    public void moveRuleLeft() {
        moveRuleLeftEvent.fire(new MoveRuleLeftEvent());
    }

    public void moveRuleRight() {
        moveRuleRightEvent.fire(new MoveRuleRightEvent());
    }

    public void addElseRule() {
        addElseRuleEvent.fire(new AddElseRuleEvent());
    }

    public void formalCompletenessCheckAction() {
        formalCompletenessCheckEvent.fire(new FormalCompletenessCheckEvent());
    }

    public void consolidateRulesAction() {
        consolidateRulesEvent.fire(new ConsolidateRulesEvent());
    }

    public void completeReportAction() {
        completeReportEvent.fire(new CompleteReportEvent());
    }

    public void structuralAnalysisAction() {
        structuralAnalysisEvent.fire(new StructuralAnalysisEvent());
    }

    public void fileNew() {
        fileNewEvent.fire(new FileNewEvent());
    }

    public void fileOpen() {
        fileOpenEvent.fire(new FileOpenEvent());
    }

    public void fileOpen(URI uri) {
        fileOpenEvent.fire(new FileOpenEvent(uri));
    }

    public void fileSave() {
        fileSaveEvent.fire(new FileSaveEvent());
    }

    public void fileSaveAs() {
        fileSaveAsEvent.fire(new FileSaveAsEvent());
    }

    public void fileExportAs() {
        fileExportAsEvent.fire(new FileExportAsEvent());
    }

    public ObservableValue<? extends Boolean> consolidateRules() {
        return consolidateRules;
    }

    public void registerQuineMcCluskeyDialog(Stage dialog) {
        mdScope.quineMcCluskeyDialog(dialog);
    }


    public void documentDeclaration() {
        documentDeclarationEvent.fire(new DocumentDeclarationEvent());
    }

    public void generateUsingLineMask() {
        generateUsingLineMaskEvent.fire(new GenerateUsingLineMaskEvent());
    }

    public void generateUsingVeinott() {
        generateUsingVeinottEvent.fire(new GenerateUsingVeinottEvent());
    }

    public void generateUsingStraightScan() {
        generateUsingStraightScanEvent.fire(new GenerateUsingStraightScanEvent());
    }

    public void generateUsingTreeMethod() {
        generateUsingTreeMethodEvent.fire(new GenerateUsingTreeMethodEvent());
    }
}
