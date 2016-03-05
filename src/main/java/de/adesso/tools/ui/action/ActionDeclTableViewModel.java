package de.adesso.tools.ui.action;

import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.ui.DeclarationTableViewModel;
import de.adesso.tools.ui.PossibleIndicatorsSupplier;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.StringProperty;

import static java.util.Arrays.asList;

/**
 * The {@link ActionDeclTableViewModel} of the actions part of the decisionTable
 * Created by mohler on 16.01.16.
 */
public class ActionDeclTableViewModel implements PossibleIndicatorsSupplier, DeclarationTableViewModel {

    private final static String EMPTY_STRING = "";

    private final String id;

    private final ModelWrapper<ActionDecl> wrapper = new ModelWrapper<>();

    public ActionDeclTableViewModel(ActionDecl decl) {
        id = decl.getId();
        wrapper.set(decl);
        wrapper.reload();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ActionDeclTableViewModel)) {
            return false;
        }
        ActionDeclTableViewModel other = (ActionDeclTableViewModel) obj;
        return other.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    public String getId() {
        return id;
    }

    public StringProperty lfdNrProperty() {
        return wrapper.field("lfdNr", ActionDecl::getLfdNr, ActionDecl::setLfdNr);
    }


    public StringProperty expressionProperty() {
        return wrapper.field("expression", ActionDecl::getExpression, ActionDecl::setExpression);
    }


    public StringProperty possibleIndicatorsProperty() {
        return wrapper.field("possibleIndicators", ActionDecl::getPossibleIndicators, ActionDecl::setPossibleIndicators);
    }

    @Override
    public String toString() {
        return wrapper.get().toString();
    }


    public boolean isValid() {
            return asList(lfdNrProperty(), expressionProperty(), possibleIndicatorsProperty()).stream()
                    .map(i -> i.get() != null && !EMPTY_STRING.equals(i.get()))
                    .reduce(true, (x,y) -> x && y);

    }

}
