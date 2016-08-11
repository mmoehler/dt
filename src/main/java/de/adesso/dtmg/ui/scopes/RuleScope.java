package de.adesso.dtmg.ui.scopes;

import de.adesso.dtmg.ui.menu.RecentItems;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

import javax.inject.Singleton;

@Singleton
public class RuleScope implements Scope {

    private final SimpleBooleanProperty consolidateRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty elseRuleSet = new SimpleBooleanProperty(false);

    public boolean getConsolidateRules() {
        return consolidateRules.get();
    }

    public void setConsolidateRules(boolean consolidateRules) {
        this.consolidateRules.set(consolidateRules);
    }

    public SimpleBooleanProperty consolidateRulesProperty() {
        return consolidateRules;
    }

    public boolean isElseRuleSet() {
        return elseRuleSet.get();
    }

    public void setElseRuleSet(boolean elseRuleSet) {
        this.elseRuleSet.set(elseRuleSet);
    }

    public SimpleBooleanProperty elseRuleProperty() {
        return elseRuleSet;
    }

    private Stage quineMcCluskeyDialog;
    public Stage quineMcCluskeyDialog() { return quineMcCluskeyDialog; }
    public void quineMcCluskeyDialog(Stage dialog) { quineMcCluskeyDialog=dialog; }

    private RecentItems recentItems;
    public RecentItems recentItems() {
        return recentItems;
    }

    public void recentItems(RecentItems recentItems) {
        this.recentItems = recentItems;
    }
}
