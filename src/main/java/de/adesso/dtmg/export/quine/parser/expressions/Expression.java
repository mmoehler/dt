package de.adesso.dtmg.export.quine.parser.expressions;

import de.adesso.dtmg.export.quine.parser.Context;

/**
 * Interface for all expression AST node classes.
 */
public interface Expression {
  /**
   * Pretty-print the expression to a string.
   */
  void print(StringBuilder builder);

  int eval(Context e);

}
