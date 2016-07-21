package de.adesso.dtmg.export.quine.parselets;


import de.adesso.dtmg.export.quine.Parser;
import de.adesso.dtmg.export.quine.Token;
import de.adesso.dtmg.export.quine.expressions.Expression;
import de.adesso.dtmg.export.quine.expressions.PostfixExpression;

/**
 * Generic infix parselet for an unary arithmetic operator. Parses postfix
 * unary "?" expressions.
 */
public class PostfixOperatorParselet implements InfixParselet {
    private final int mPrecedence;

    public PostfixOperatorParselet(int precedence) {
        mPrecedence = precedence;
    }

    public Expression parse(Parser parser, Expression left, Token token) {
        return new PostfixExpression(left, token.getType());
    }

    public int getPrecedence() {
        return mPrecedence;
    }
}