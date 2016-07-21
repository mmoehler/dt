package de.adesso.dtmg.export.quine.parselets;

import de.adesso.dtmg.export.quine.Parser;
import de.adesso.dtmg.export.quine.Token;
import de.adesso.dtmg.export.quine.expressions.Expression;
import de.adesso.dtmg.export.quine.expressions.NameExpression;

/**
 * Simple parselet for a named variable like "abc".
 */
public class NameParselet implements PrefixParselet {
    public Expression parse(Parser parser, Token token) {
        parser.getContext().putVar(token.getText());
        return new NameExpression(token.getText());
    }
}
