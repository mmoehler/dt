package de.adesso.tools.ui;

import javafx.beans.property.StringProperty;

/**
 * Created by mohler on 06.02.16.
 */
public interface DeclarationTableViewModel {
    String getId();

    StringProperty lfdNrProperty();

    StringProperty expressionProperty();

    StringProperty possibleIndicatorsProperty();

    boolean isValid();
}
