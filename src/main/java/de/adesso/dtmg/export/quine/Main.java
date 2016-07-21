package de.adesso.dtmg.export.quine;


import de.adesso.dtmg.export.quine.expressions.Expression;

public class Main {
  public static void main(String[] args) {
    // Function call.
    test("c*~a+a*~b*d+a*~b*~c*~d", "(((c * (~a)) + ((a * (~b)) * d)) + (((a * (~b)) * (~c)) * (~d)))");


    // Show the results.
    if (sFailed == 0) {
      System.out.println("Passed all " + sPassed + " tests.");
    } else {
      System.out.println("----");
      System.out.println("Failed " + sFailed + " out of " +
          (sFailed + sPassed) + " tests.");
    }
  }
  
  /**
   * Parses the given chunk of code and verifies that it matches the expected
   * pretty-printed result.
   */
  public static void test(String source, String expected) {
    Lexer lexer = new Lexer(source);
    Parser parser = new BantamParser(lexer);
    
    try {
      Expression result = parser.parseExpression();
      StringBuilder builder = new StringBuilder();
      result.print(builder);
      String actual = builder.toString();
      
      if (expected.equals(actual)) {
        sPassed++;
      } else {
        sFailed++;
        System.out.println("[FAIL] Expected: " + expected);
        System.out.println("         Actual: " + actual);
      }
    } catch(ParseException ex) {
      sFailed++;
      System.out.println("[FAIL] Expected: " + expected);
      System.out.println("          Error: " + ex.getMessage());
    }
  }
  
  private static int sPassed = 0;
  private static int sFailed = 0;
}
