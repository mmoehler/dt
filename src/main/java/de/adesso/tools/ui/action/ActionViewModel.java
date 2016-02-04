package de.adesso.tools.ui.action;

import de.adesso.tools.events.ActionDeclsUpdatedEvent;
import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ActionViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(ActionViewModel.class);
    public static final String QMARK = "?";
    public static final String EMPTY = "";
    public static final String COND_ROW_HEADER = "C%02d";

    @Inject
    private NotificationCenter notificationCenter;

    private final ObservableList<ActionDeclTableViewModel> decls = FXCollections.observableArrayList();

    private final ObservableList<ObservableList<String>> defns = FXCollections.observableArrayList();

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

    public ObservableList<ObservableList<String>> getDefns() {
        return defns;
    }

    public void onContactsUpdateEvent(@Observes ActionDeclsUpdatedEvent event) {
        updateActionDeclsList();;
    }

    private void updateActionDeclsList() {
        LOG.debug("Update action declarations");
        decls.forEach(x -> x.lfdNrProperty().setValue(String.format("A%02d",decls.indexOf(x))));
    }

    public ObservableList<ObservableList<String>> initializeActionDefnsData(final int requestedColCount, final boolean shouldPopulateData) {
        /*
        final ObservableList<ObservableList<String>> data
                = limitedExpandConditions(this.decls, requestedColCount, !shouldPopulateData);
        this.defns.clear();
        data.stream().forEach(this.defns::add);
        return this.defns;
        */

        return FXCollections.emptyObservableList();
    }

}
