package de.adesso.tools.model;

/**
 * Created by mohler on 16.01.16.
 */
public class ActionDecl extends Declaration {
    public ActionDecl() {
        super();
    }

    public ActionDecl(String lfdNr, String expression, String possibleIndicators) {
        super(lfdNr, expression, possibleIndicators);
    }

    @Override
    public String getLfdNr() {
        return super.getLfdNr();
    }

    @Override
    public void setLfdNr(String lfdNr) {
        super.setLfdNr(lfdNr);
    }

    @Override
    public String getExpression() {
        return super.getExpression();
    }

    @Override
    public void setExpression(String expression) {
        super.setExpression(expression);
    }

    @Override
    public String getPossibleIndicators() {
        return super.getPossibleIndicators();
    }

    @Override
    public void setPossibleIndicators(String possibleIndicators) {
        super.setPossibleIndicators(possibleIndicators);
    }

    @Override
    public int getPossibleIndicatorSize() {
        return super.getPossibleIndicatorSize();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
