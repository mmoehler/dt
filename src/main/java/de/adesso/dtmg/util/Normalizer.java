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

package de.adesso.dtmg.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by moehler on 02.07.16.
 */
public enum Normalizer {

    INSTANCE;

    private final static String[][] UMLAUT_REPLACEMENTS = {
            {new String("Ä"), "Ae"}, {new String("Ü"), "Ue"},
            {new String("Ö"), "Oe"}, {new String("ä"), "ae"},
            {new String("ü"), "ue"}, {new String("ö"), "oe"},
            {new String("ß"), "ss"}
    };

    private static String replaceGermanUmlauts(String orig) {
        String result = orig;
        for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
            result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
        }
        return result;
    }

    public String toJavaIdentifer(String prefix, String ident) {
        if (ident.length() == 0) {
            return prefix + "_";
        }
        CharacterIterator ci = new StringCharacterIterator(replaceGermanUmlauts(ident.trim()));
        StringBuffer sb = new StringBuffer(prefix);
        for (char c = ci.first(); c != CharacterIterator.DONE; c = ci.next()) {
            if (sb.length() == 0) {
                if (Character.isJavaIdentifierStart(c)) {
                    sb.append(c);
                    continue;
                } else
                    sb.append('_');
            }
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            } else if (c == '>') {
                if (ci.next() == '=') {
                    sb.append("GE");
                } else {
                    sb.append("GT");
                    ci.previous();
                }
            } else if (c == '<') {
                char n = ci.next();
                if (n == '=') {
                    sb.append("LE");
                } else if (n == '>') {
                    sb.append("NE");
                } else {
                    sb.append("LO");
                    ci.previous();
                }
            } else if (c == '!') {
                char n = ci.next();
                if (n == '=') {
                    sb.append("NE");
                } else {
                    sb.append("NOT_");
                    ci.previous();
                }
            } else if (c == '&') {
                char n = ci.next();
                if (n == '&') {
                    sb.append("AND");
                } else {
                    sb.append("AND");
                    ci.previous();
                }
            } else if (c == '|') {
                char n = ci.next();
                if (n == '|') {
                    sb.append("OR");
                } else {
                    sb.append("OR");
                    ci.previous();
                }
            } else if (c == '=') {
                char n = ci.next();
                if (n == '=') {
                    sb.append("EQ");
                } else {
                    sb.append("EQ");
                    ci.previous();
                }
            } else {
                switch (c) {
                    case '%':
                        sb.append("Prozent");
                        break;
                    case '§':
                        sb.append("Paragraph");
                        break;
                    default:
                        sb.append(' ');
                }
            }
        }
        Pattern p = Pattern.compile(" (.)");
        Matcher m = p.matcher(sb.toString());
        sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
