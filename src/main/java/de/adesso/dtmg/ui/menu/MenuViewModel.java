package de.adesso.dtmg.ui.menu;

import de.adesso.dtmg.events.*;
import de.adesso.dtmg.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by mohler ofList 15.01.16.
 */
@Singleton
public class MenuViewModel implements ViewModel {

    private final SimpleBooleanProperty consolidateRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty removeDuplicateRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty missingRules = new SimpleBooleanProperty(true);

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
    private Event<AddMissingRulesEvent> addMissingRulesEvent;
    @Inject
    private Event<DeleteRedundantRulesEvent> deleteRedundantRulesEvent;

    @InjectScope
    private RuleScope mdScope;

    public void initialize() {
        consolidateRules.bind(mdScope.consolidateRulesProperty());
        removeDuplicateRules.bind(mdScope.removeDuplicateRulesProperty());
        missingRules.bind(mdScope.missingRulesProperty());
    }

    public boolean getConsolidateRules() {
        return consolidateRules.get();
    }

    public SimpleBooleanProperty consolidateRulesProperty() {
        return consolidateRules;
    }

    public boolean getRemoveDuplicateRules() {
        return removeDuplicateRules.get();
    }

    public SimpleBooleanProperty removeDuplicateRulesProperty() {
        return removeDuplicateRules;
    }

    public boolean getMissingRules() {
        return missingRules.get();
    }

    public SimpleBooleanProperty missingRulesProperty() {
        return missingRules;
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

    public void fileSave() {
        fileSaveEvent.fire(new FileSaveEvent());
    }

    public void fileSaveAs() {
        fileSaveAsEvent.fire(new FileSaveAsEvent());
    }

    public void addMissingRules() {
        addMissingRulesEvent.fire(new AddMissingRulesEvent());
    }

    public void deleteRedundantRules() {
        deleteRedundantRulesEvent.fire(new DeleteRedundantRulesEvent());
    }

    public ObservableValue<? extends Boolean> consolidateRules() {
        return consolidateRules;
    }
}
