/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.adesso.dtmg.export.quine.parser;


import de.adesso.dtmg.export.quine.parser.expressions.Expression;

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
