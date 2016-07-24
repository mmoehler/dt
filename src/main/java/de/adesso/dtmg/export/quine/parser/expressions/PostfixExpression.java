package de.adesso.dtmg.export.quine.parser.expressions;

import de.adesso.dtmg.export.quine.parser.Context;
import de.adesso.dtmg.export.quine.parser.TokenType;

/**
 * A postfix unary arithmetic expression like "a!".
 */
public class PostfixExpression implements Expression {
    private final Expression mLeft;
    private final TokenType mOperator;

    public PostfixExpression(Expression left, TokenType operator) {
        mLeft = left;
        mOperator = operator;
    }

    public void print(StringBuilder builder) {
        builder.append("(");
        mLeft.print(builder);
        builder.append(mOperator.punctuator()).append(")");
    }

    @Override
    public int eval(Context e) {
        throw new UnsupportedOperationException("PostfixExpression::eval(Context)");
    }
}
