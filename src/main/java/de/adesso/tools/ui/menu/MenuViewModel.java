package de.adesso.tools.ui.menu;

import de.adesso.tools.events.AddConditionDeclEvent;
import de.adesso.tools.events.RemoveConditionDeclEvent;
import de.adesso.tools.events.TriggerShutdownEvent;
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
public class MenuViewModel  implements ViewModel {

    @Inject
    private Event<TriggerShutdownEvent> shouldCloseEvent;

    @Inject
    private Event<RemoveConditionDeclEvent> removeConditionDeclEvent;

    @Inject
    private Event<AddConditionDeclEvent> addConditionDeclEvent;

    @InjectScope
    private RuleScope mdScope;

    private final ReadOnlyBooleanWrapper removeItemDisabled = new ReadOnlyBooleanWrapper();


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
}
