package de.adesso.tools.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class RuleScope implements Scope {

    public final DoubleProperty conditionDividerPos = new SimpleDoubleProperty();
    public final DoubleProperty actionDividerPos = new SimpleDoubleProperty();

    public RuleScope() {
    }
}
