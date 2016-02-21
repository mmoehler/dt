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
    private Event<SimpleCompletenessCheckEvent> simpleCompletenessCheckEvent;
    @Inject
    private Event<AddActionDeclEvent> addActionDeclEvent;
    @Inject
    private Event<RemoveActionDeclEvent> removeActionDeclEvent;
    @Inject
    private Event<RemoveRulesWithoutActionsEvent> removeRulesWithoutActionsEvent;
    @Inject
    private Event<RemoveRuleEvent> removeRuleEvent;
    @Inject
    private Event<InsertConditionDeclEvent> insertConditionDeclEvent;
    @Inject
    private Event<InsertActionDeclEvent> insertActionDeclEvent;
    @Inject
    private Event<InsertRuleDefEvent> insertRuleDefEvent;
    @Inject
    private Event<MoveDeclUpEvent> moveDeclUpEvent;
    @Inject
    private Event<MoveDeclDownEvent> moveDeclDownEvent;

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

    public void simpleCompletenessCheckAction() {
        simpleCompletenessCheckEvent.fire(new SimpleCompletenessCheckEvent());
    }

    public void addActionDecl() {
        addActionDeclEvent.fire(new AddActionDeclEvent());
    }

    public void removeActionDecl() {
        removeActionDeclEvent.fire(new RemoveActionDeclEvent());
    }

    public void removeRulesWithoutAction() {
        removeRulesWithoutActionsEvent.fire(new RemoveRulesWithoutActionsEvent());
    }

    public void removeRule() {
        removeRuleEvent.fire(new RemoveRuleEvent());
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

    public void moveDeclUp() {
        moveDeclUpEvent.fire(new MoveDeclUpEvent());
    }

    public void moveDeclDown() {
        moveDeclDownEvent.fire(new MoveDeclDownEvent());
    }
}
