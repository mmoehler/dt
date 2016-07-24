package de.adesso.dtmg.export.quine.parser.expressions;

import de.adesso.dtmg.export.quine.parser.Context;
import de.adesso.dtmg.export.quine.parser.TokenType;

/**
 * A prefix unary arithmetic expression like "!a" or "-b".
 */
public class PrefixExpression implements Expression {

    private final TokenType mOperator;
    private final Expression mRight;

    public PrefixExpression(TokenType operator, Expression right) {
        mOperator = operator;
        mRight = right;
    }

    public void print(StringBuilder builder) {
        builder.append("(").append(mOperator.punctuator());
        mRight.print(builder);
        builder.append(")");
    }

    @Override
    public int eval(Context e) {
        int r = mRight.eval(e);
        char op = mOperator.punctuator();
        if('~' == op) {
            return (r==1) ? 0 : 1;
        }
        throw new IllegalStateException("'~' expected");
    }
}
