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

/**
 * Created by moehler on 18.07.2016.
 */
public enum TokenType {
    LEFT_PAREN,
    RIGHT_PAREN,
    PLUS,
    ASTERISK,
    NEGATION,
    NAME,
    EOF;

    /**
     * If the TokenType represents a punctuator (i.e. a token that can split an
     * identifier like '+', this will getVar its text.
     */
    public Character punctuator() {
        switch (this) {
            case LEFT_PAREN:  return '(';
            case RIGHT_PAREN: return ')';
            case PLUS:        return '+';
            case ASTERISK:    return '*';
            case NEGATION:       return '!';
            default:          return null;
        }
    }
}