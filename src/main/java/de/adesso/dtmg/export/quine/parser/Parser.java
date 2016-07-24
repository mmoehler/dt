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
import de.adesso.dtmg.export.quine.parser.parselets.InfixParselet;
import de.adesso.dtmg.export.quine.parser.parselets.PrefixParselet;

import java.util.*;


public class Parser {

    private final Iterator<Token> mTokens;
    private final List<Token> mRead = new ArrayList<Token>();
    private final Map<TokenType, PrefixParselet> mPrefixParselets = new HashMap<TokenType, PrefixParselet>();
    private final Map<TokenType, InfixParselet> mInfixParselets = new HashMap<TokenType, InfixParselet>();
    private final Context context = new ContextImpl();


    public Parser(Iterator<Token> tokens) {
        mTokens = tokens;
    }

    public Context getContext() {
        return context;
    }

    public void register(TokenType token, PrefixParselet parselet) {
        mPrefixParselets.put(token, parselet);
    }

    public void register(TokenType token, InfixParselet parselet) {
        mInfixParselets.put(token, parselet);
    }

    public Expression parseExpression(int precedence) {
        Token token = consume();
        PrefixParselet prefix = mPrefixParselets.get(token.getType());

        if (prefix == null) throw new ParseException("Could not parse \"" +
                token.getText() + "\".");

        Expression left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = consume();

            InfixParselet infix = mInfixParselets.get(token.getType());
            left = infix.parse(this, left, token);
        }

        return left;
    }

    public Expression parseExpression() {
        return parseExpression(0);
    }

    public boolean match(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            return false;
        }

        consume();
        return true;
    }

    public Token consume(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            throw new RuntimeException("Expected token " + expected +
                    " and found " + token.getType());
        }

        return consume();
    }

    public Token consume() {
        // Make sure we've read the token.
        lookAhead(0);

        return mRead.remove(0);
    }

    private Token lookAhead(int distance) {
        // Read in as many as needed.
        while (distance >= mRead.size()) {
            mRead.add(mTokens.next());
        }

        // Get the queued token.
        return mRead.get(distance);
    }

    private int getPrecedence() {
        InfixParselet parser = mInfixParselets.get(lookAhead(0).getType());
        if (parser != null) return parser.getPrecedence();

        return 0;
    }

}
