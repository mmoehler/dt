package de.adesso.dtmg.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Singleton;

@Singleton
public class RuleScope implements Scope {

    private final SimpleBooleanProperty consolidateRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty removeDuplicateRules = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty missingRules = new SimpleBooleanProperty(true);

    public boolean getConsolidateRules() {
        return consolidateRules.get();
    }

    public void setConsolidateRules(boolean consolidateRules) {
        this.consolidateRules.set(consolidateRules);
    }

    public SimpleBooleanProperty consolidateRulesProperty() {
        return consolidateRules;
    }

    public boolean getRemoveDuplicateRules() {
        return removeDuplicateRules.get();
    }

    public void setRemoveDuplicateRules(boolean removeDuplicateRules) {
        this.removeDuplicateRules.set(removeDuplicateRules);
    }

    public SimpleBooleanProperty removeDuplicateRulesProperty() {
        return removeDuplicateRules;
    }

    public boolean getMissingRules() {
        return missingRules.get();
    }

    public void setMissingRules(boolean missingRules) {
        this.missingRules.set(missingRules);
    }

    public SimpleBooleanProperty missingRulesProperty() {
        return missingRules;
    }
}
