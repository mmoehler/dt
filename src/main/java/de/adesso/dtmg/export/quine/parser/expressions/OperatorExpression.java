package de.adesso.dtmg.export.quine.parser.expressions;

import de.adesso.dtmg.export.quine.parser.Context;
import de.adesso.dtmg.export.quine.parser.TokenType;

/**
 * A binary arithmetic expression like "a + b" or "c ^ d".
 */
public class OperatorExpression implements Expression {
    static int[][] or = {
            {0, 1},
            {1, 1},
    };
    static int[][] and = {
            {0, 0},
            {0, 1},
    };
    private final Expression mLeft;
    private final TokenType mOperator;
    private final Expression mRight;

    public OperatorExpression(Expression left, TokenType operator, Expression right) {
        mLeft = left;
        mOperator = operator;
        mRight = right;
    }

    public void print(StringBuilder builder) {
        builder.append("(");
        mLeft.print(builder);
        builder.append(" ").append(mOperator.punctuator()).append(" ");
        mRight.print(builder);
        builder.append(")");
    }

    @Override
    public int eval(Context e) {
        final int l = mLeft.eval(e);
        final int r = mRight.eval(e);
        switch (mOperator.punctuator()) {
            case '+':
                return or[l][r];
            case '*':
                return and[l][r];
            default:
                throw new IllegalStateException("'+' or '*' expected!");
        }
    }
}
