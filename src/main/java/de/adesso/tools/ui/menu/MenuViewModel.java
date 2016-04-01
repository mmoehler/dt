package de.adesso.tools.ui.menu;

import de.adesso.tools.events.*;
import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by mohler on 15.01.16.
 */
@Singleton
public class MenuViewModel implements ViewModel {

    private final ReadOnlyBooleanWrapper removeItemDisabled = new ReadOnlyBooleanWrapper();
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

    @InjectScope
    private RuleScope mdScope;

    public void initialize() {
        //removeItemDisabled.bind(mdScope.selectedContactProperty().isNull());
    }

    public ReadOnlyBooleanProperty removeItemDisabledProperty() {
        return removeItemDisabled.getReadOnlyProperty();
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
}
