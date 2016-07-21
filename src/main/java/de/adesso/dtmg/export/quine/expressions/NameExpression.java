package de.adesso.dtmg.export.quine.expressions;

import de.adesso.dtmg.export.quine.Context;

/**
 * A simple variable name expression like "abc".
 */
public class NameExpression implements Expression {
    private final String mName;

    public NameExpression(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void print(StringBuilder builder) {
        builder.append(mName);
    }

    @Override
    public int eval(Context e) {
        return e.getVar(mName);
    }
}
