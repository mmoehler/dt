package de.adesso.tools.ui.condition;

import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.DeclarationTableViewModel;
import de.adesso.tools.ui.PossibleIndicatorsSupplier;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.StringProperty;

import static java.util.Arrays.asList;

/**
 * Created by mohler on 16.01.16.
 */
public class ConditionDeclTableViewModel implements PossibleIndicatorsSupplier, DeclarationTableViewModel {

    private final static String EMPTY_STRING = "";

    private final String id;

    private final ModelWrapper<ConditionDecl> wrapper = new ModelWrapper<>();

    private boolean someValidation = true;

    public ConditionDeclTableViewModel(ConditionDecl decl) {
        id = decl.getId();
        wrapper.set(decl);
        wrapper.reload();
    }

    public void reset() {
        wrapper.reset();
    }

    public void reloadFromModel() {
        wrapper.reload();
    }

    public void save() {
        if (someValidation) {
            wrapper.commit();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConditionDeclTableViewModel)) {
            return false;
        }
        ConditionDeclTableViewModel other = (ConditionDeclTableViewModel) obj;
        return other.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public StringProperty lfdNrProperty() {
        return wrapper.field("lfdNr", ConditionDecl::getLfdNr, ConditionDecl::setLfdNr);
    }

    @Override
    public StringProperty expressionProperty() {
        return wrapper.field("expression", ConditionDecl::getExpression, ConditionDecl::setExpression);
    }

    @Override
    public StringProperty possibleIndicatorsProperty() {
        return wrapper.field("possibleIndicators", ConditionDecl::getPossibleIndicators, ConditionDecl::setPossibleIndicators);
    }

    @Override
    public String toString() {
        return wrapper.get().toString();
    }

    public boolean isValid() {
        return asList(lfdNrProperty(), expressionProperty(), possibleIndicatorsProperty()).stream()
                .map(i -> i.get() != null && i.get() != EMPTY_STRING)
                .reduce(true, (x, y) -> x && y);

    }

}
