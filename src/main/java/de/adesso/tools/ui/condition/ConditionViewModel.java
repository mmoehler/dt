package de.adesso.tools.ui.condition;

import de.adesso.tools.events.AddConditionDeclEvent;
import de.adesso.tools.events.RemoveConditionDeclEvent;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.scopes.RuleScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ConditionViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionViewModel.class);

    private final ObservableList<ConditionDeclTableViewModel> decls = FXCollections.observableArrayList();

    private final ObservableList<List<String>> defns = FXCollections.observableArrayList();

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

    public void onAddConditionDecl(@Observes AddConditionDeclEvent event) {
        ObservableList<ConditionDeclTableViewModel> tmp = FXCollections.observableArrayList(this.decls);
        tmp.add(new ConditionDeclTableViewModel(new ConditionDecl()));
        this.decls.clear();

        int i[] = {1};
        tmp.forEach(x -> {
            x.lfdNrProperty().setValue(String.format("C%02d",i[0]++));
            this.decls.add(x);
        });
        LOG.debug("A new condition decl "+this.decls.get(0).getId()+" is added");
    }

    public void onRemoveConditionDecl(@Observes RemoveConditionDeclEvent event) {
        this.decls.forEach(x -> x.save());
        final String reduce = decls.stream().map(a -> a.toString()).reduce("", (a, b) -> a + '\n' + b);
        LOG.debug(reduce + '\n');
    }

    public void prepareConditionDefns(final int requestedColCount, final boolean shouldPopulateData) {

    }
}