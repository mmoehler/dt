package de.adesso.tools.model;

/**
 * Created by mohler on 16.01.16.
 */
public class Declaration extends Identity {

    /**
     * sequential numbr of this dicl
     */
    private String lfdNr = "";

    /**
     * Expression of this decl. E.g. 'Partner.age > 32'
     */
    private String expression = "";

    /**
     * Possible indicators of this decl. Currently only {Y,N} are supported
     */
    private String possibleIndicators = "";

    public Declaration() {
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
        final StringBuffer sb = new StringBuffer(getClass().getSimpleName()+"{");
        sb.append("id='").append(getId()).append('\'');
        sb.append(", lfdNr='").append(lfdNr).append('\'');
        sb.append(", expression='").append(expression).append('\'');
        sb.append(", possibleIndicators='").append(possibleIndicators).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
