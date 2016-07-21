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

package de.adesso.dtmg.export.quine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A very primitive lexer. Takes a string and splits it into a series of
 * Tokens. Operators and punctuation are mapped to unique keywords. Names,
 * which can be any series of letters, are turned into NAME tokens. All other
 * characters are ignored (except to separate names). Numbers and strings are
 * not supported. This is really just the bare minimum to give the parser
 * something to work with.
 */
public class Lexer implements Iterator<Token> {

    private final Map<Character, TokenType> mPunctuators =
            new HashMap<Character, TokenType>();
    private final String mText;
    private int mIndex = 0;


    /**
     * Creates a new Lexer to tokenize the given string.
     *
     * @param text String to tokenize.
     */
    public Lexer(String text) {
        mIndex = 0;
        mText = text;

        // Register all of the TokenTypes that are explicit punctuators.
        for (TokenType type : TokenType.values()) {
            Character punctuator = type.punctuator();
            if (punctuator != null) {
                mPunctuators.put(punctuator, type);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Token next() {
        while (mIndex < mText.length()) {
            char c = mText.charAt(mIndex++);

            if (mPunctuators.containsKey(c)) {
                // Handle punctuation.
                return new Token(mPunctuators.get(c), Character.toString(c));
            } else if (Character.isLetter(c)) {
                // Handle names.
                int start = mIndex - 1;
                while (mIndex < mText.length()) {
                    if (!Character.isLetter(mText.charAt(mIndex))) break;
                    mIndex++;
                }

                String name = mText.substring(start, mIndex);
                return new Token(TokenType.NAME, name);
            } else {
                // Ignore all other characters (whitespace, etc.)
            }
        }

        // Once we've reached the end of the string, just return EOF tokens. We'll
        // just keeping returning them as many times as we're asked so that the
        // parser's lookahead doesn't have to worry about running out of tokens.
        return new Token(TokenType.EOF, "");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}