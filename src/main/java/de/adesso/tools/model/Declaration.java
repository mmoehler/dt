package de.adesso.tools.model;

/**
 * General state and behaviour of all declarations
 * Created by mohler on 16.01.16.
 */
public class Declaration extends Identity {

    public static final String EMPTY_STRING = "";
    /**
     * sequential numbr of this dicl
     */
    protected String lfdNr = EMPTY_STRING;

    /**
     * Expression of this decl. E.g. 'Partner.age > 32'
     */
    protected String expression = EMPTY_STRING;

    /**
     * Possible indicators of this decl. Currently only {YES,NO} are supported
     */
    protected String possibleIndicators = EMPTY_STRING;

    public Declaration() {
    }

    public Declaration(String lfdNr, String expression, String possibleIndicators) {
        this.lfdNr = lfdNr;
        this.expression = expression;
        this.possibleIndicators = possibleIndicators;
    }

    public String getLfdNr() {
        return lfdNr;
    }

    public void setLfdNr(String lfdNr) {
        this.lfdNr = lfdNr;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getPossibleIndicators() {
        return possibleIndicators;
    }

    public void setPossibleIndicators(String possibleIndicators) {
        this.possibleIndicators = possibleIndicators;
    }

    public int getPossibleIndicatorSize() {
        return (null == this.possibleIndicators || this.possibleIndicators.trim().length()==0)
        ? 0
        : this.possibleIndicators.split("[,;]]").length;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id='" + getId() + '\'' +
                ", lfdNr='" + lfdNr + '\'' +
                ", expression='" + expression + '\'' +
                ", possibleIndicators='" + possibleIndicators + '\'' +
                '}';
    }
}
