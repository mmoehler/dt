package de.adesso.tools.ui.action;

import de.adesso.tools.events.ActionDeclsUpdatedEvent;
import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;

public class ActionViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(ActionViewModel.class);

    private final ObservableList<ActionDeclTableViewModel> decls = FXCollections.observableArrayList();

    @InjectScope
    private RuleScope ruleScope;

    public ActionViewModel() {
    }

    public RuleScope getRuleScope() {
        return ruleScope;
    }

    public ObservableList<ActionDeclTableViewModel> getDecls() {
        return decls;
    }

    public void onContactsUpdateEvent(@Observes ActionDeclsUpdatedEvent event) {
        updateActionDeclsList();;
    }

    private void updateActionDeclsList() {
        LOG.debug("Update action declarations");
        decls.forEach(x -> x.lfdNrProperty().setValue(String.format("A%02d",decls.indexOf(x))));
    }
}
