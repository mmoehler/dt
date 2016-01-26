package de.adesso.tools.ui.condition;

import de.adesso.tools.events.AddConditionDeclEvent;
import de.adesso.tools.events.AddConditionDefEvent;
import de.adesso.tools.events.RemoveConditionDeclEvent;
import de.adesso.tools.events.SimpleCompletenessCheckEvent;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.scopes.RuleScope;
import de.adesso.tools.util.tuple.Tuple3;
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

import static de.adesso.tools.ui.condition.analysis.ConditionCompletenessCheck.*;
import static de.adesso.tools.util.func.DtOps.*;

@Singleton
public class ConditionViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionViewModel.class);
    public static final String QMARK = "?";
    public static final String EMPTY = "";
    public static final String COND_ROW_HEADER = "C%02d";

    @Inject
    private NotificationCenter notificationCenter;

    private final ObservableList<ConditionDeclTableViewModel> decls = FXCollections.observableArrayList();

    private final ObservableList<ObservableList<String>> defns = FXCollections.observableArrayList();

    @InjectScope
    private RuleScope ruleScope;

    public ConditionViewModel() {
    }

    public RuleScope getRuleScope() {
        return ruleScope;
    }

    public ObservableList<ConditionDeclTableViewModel> getDecls() {
        return decls;
    }

    public ObservableList<ObservableList<String>> getDefns() {
        return defns;
    }

    public void onAddConditionDecl(@Observes AddConditionDeclEvent event) {
        // #1 Always replace the table content with new created content
        ObservableList<ConditionDeclTableViewModel> tmp = FXCollections.observableArrayList(this.decls);
        tmp.add(new ConditionDeclTableViewModel(new ConditionDecl()));
        this.decls.clear();

        int i[] = {1};
        tmp.forEach(x -> {
            x.lfdNrProperty().setValue(String.format(COND_ROW_HEADER, i[0]++));
            this.decls.add(x);
        });

        // #2 If there are at least one condition defined, then this must also be updated
        if(!this.defns.isEmpty()) {
            ObservableList<ObservableList<String>> newDefns = copyMatrixWithAddedRow(this.defns, () -> QMARK, () -> EMPTY_STRING);
            this.defns.clear();
            newDefns.stream().forEach(this.defns::add);
        }
    }

    public void onRemoveConditionDecl(@Observes RemoveConditionDeclEvent event) {
        final String reduce = defns.stream().map(a -> a.toString()).reduce(EMPTY, (a, b) -> a + '\n' + b);
        LOG.debug(reduce + '\n');
    }

    public void onAddConditionDef(@Observes AddConditionDefEvent event) {
        ObservableList<ObservableList<String>> newDefns = copyMatrixWithAddedColumn(this.defns, () -> QMARK);
        this.defns.clear();
        this.publish(ConditionViewModelNotifications.CONDITIONDEF_ADD.name(), newDefns);
    }

    public ObservableList<ObservableList<String>> initializeConditionDefnsData(final int requestedColCount, final boolean shouldPopulateData) {
        final ObservableList<ObservableList<String>> data
                = limitedExpandConditions(this.decls, requestedColCount, !shouldPopulateData);
        this.defns.clear();
        data.stream().forEach(this.defns::add);
        return this.defns;
    }

    public void onSimpleCompletenessCheck(@Observes SimpleCompletenessCheckEvent event) {
        final Tuple3<Boolean, Integer, Integer> result = isFormalComplete(this.decls, this.defns);
        final String message = String.valueOf(result);
        notificationCenter.publish(ConditionViewModelNotifications.PREPARE_CONSOLE.name(), message);
    }

}