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

import de.adesso.dtmg.export.quine.parser.parselets.*;

/**
 * Extends the generic Parser class with support for parsing the actual Bantam
 * grammar.
 */
public class BantamParser extends Parser {
  public BantamParser(Lexer lexer) {
    super(lexer);
    
    // Register all of the parselets for the grammar.
    
    // Register the ones that need special parselets.
    register(TokenType.NAME,       new NameParselet());
    register(TokenType.LEFT_PAREN, new GroupParselet());

    // Register the simple operator parselets.
    prefix(TokenType.TILDE,     Precedence.PREFIX);

    // For kicks, we'll make "!" both prefix and postfix, kind of like ++.
    infixLeft(TokenType.PLUS,     Precedence.SUM);
    infixLeft(TokenType.ASTERISK, Precedence.PRODUCT);
  }
  
  /**
   * Registers a postfix unary operator parselet for the given token and
   * precedence.
   */
  public void postfix(TokenType token, int precedence) {
    register(token, new PostfixOperatorParselet(precedence));
  }
  
  /**
   * Registers a prefix unary operator parselet for the given token and
   * precedence.
   */
  public void prefix(TokenType token, int precedence) {
    register(token, new PrefixOperatorParselet(precedence));
  }
  
  /**
   * Registers a left-associative binary operator parselet for the given token
   * and precedence.
   */
  public void infixLeft(TokenType token, int precedence) {
    register(token, new BinaryOperatorParselet(precedence, false));
  }
  
  /**
   * Registers a right-associative binary operator parselet for the given token
   * and precedence.
   */
  public void infixRight(TokenType token, int precedence) {
    register(token, new BinaryOperatorParselet(precedence, true));
  }
}