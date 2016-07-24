package de.adesso.dtmg.export.quine.parser.parselets;

import de.adesso.dtmg.export.quine.parser.Parser;
import de.adesso.dtmg.export.quine.parser.Token;
import de.adesso.dtmg.export.quine.parser.expressions.Expression;
import de.adesso.dtmg.export.quine.parser.expressions.NameExpression;

/**
 * Simple parselet for a named variable like "abc".
 */
public class NameParselet implements PrefixParselet {
    public Expression parse(Parser parser, Token token) {
        parser.getContext().putVar(token.getText());
        return new NameExpression(token.getText());
    }
}
