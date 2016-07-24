package de.adesso.dtmg.export.quine.parser.parselets;


import de.adesso.dtmg.export.quine.parser.Parser;
import de.adesso.dtmg.export.quine.parser.Token;
import de.adesso.dtmg.export.quine.parser.expressions.Expression;
import de.adesso.dtmg.export.quine.parser.expressions.PrefixExpression;

/**
 * Generic prefix parselet for an unary arithmetic operator. Parses prefix
 * unary "-", "+", "~", and "!" expressions.
 */
public class PrefixOperatorParselet implements PrefixParselet {
    private final int mPrecedence;

    public PrefixOperatorParselet(int precedence) {
        mPrecedence = precedence;
    }

    public Expression parse(Parser parser, Token token) {
        // To handle right-associative operators like "^", we allow a slightly
        // lower precedence when parsing the right-hand side. This will let a
        // parselet with the same precedence appear on the right, which will then
        // take *this* parselet's result as its left-hand argument.
        Expression right = parser.parseExpression(mPrecedence);

        return new PrefixExpression(token.getType(), right);
    }

    public int getPrecedence() {
        return mPrecedence;
    }
}