package de.adesso.dtmg.export.quine.parselets;

import de.adesso.dtmg.export.quine.Parser;
import de.adesso.dtmg.export.quine.Token;
import de.adesso.dtmg.export.quine.TokenType;
import de.adesso.dtmg.export.quine.expressions.Expression;

/**
 * Parses parentheses used to group an expression, like "a * (b + c)".
 */
public class GroupParselet implements PrefixParselet {
    public Expression parse(Parser parser, Token token) {
        Expression expression = parser.parseExpression();
        parser.consume(TokenType.RIGHT_PAREN);
        return expression;
    }
}
