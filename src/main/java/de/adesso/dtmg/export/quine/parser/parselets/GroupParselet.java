package de.adesso.dtmg.export.quine.parser.parselets;

import de.adesso.dtmg.export.quine.parser.Parser;
import de.adesso.dtmg.export.quine.parser.Token;
import de.adesso.dtmg.export.quine.parser.TokenType;
import de.adesso.dtmg.export.quine.parser.expressions.Expression;

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
