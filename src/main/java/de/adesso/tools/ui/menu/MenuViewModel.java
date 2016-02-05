package de.adesso.tools.ui.menu;

import de.adesso.tools.events.*;
import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * Created by mohler on 15.01.16.
 */
public class MenuViewModel implements ViewModel {

    private final ReadOnlyBooleanWrapper removeItemDisabled = new ReadOnlyBooleanWrapper();
    @Inject
    private Event<TriggerShutdownEvent> shouldCloseEvent;
    @Inject
    private Event<RemoveConditionDeclEvent> removeConditionDeclEvent;
    @Inject
    private Event<AddConditionDeclEvent> addConditionDeclEvent;
    @Inject
    private Event<AddConditionDefEvent> addConditionDefEvent;
    @Inject
    private Event<SimpleCompletenessCheckEvent> simpleCompletenessCheckEvent;
    @Inject
    private Event<AddActionDeclEvent> addActionDeclEventEvent;

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

    public void removeConditionDeclAction() {
        removeConditionDeclEvent.fire(new RemoveConditionDeclEvent());
    }

    public void addConditionDeclAction() {
        addConditionDeclEvent.fire(new AddConditionDeclEvent());
    }

    public void addConditionDef() {
        addConditionDefEvent.fire(new AddConditionDefEvent());
    }

    public void simpleCompletenessCheckAction() {
        simpleCompletenessCheckEvent.fire(new SimpleCompletenessCheckEvent());
    }

    public void addActionDef() {
        addActionDeclEventEvent.fire(new AddActionDeclEvent());
    }
}
