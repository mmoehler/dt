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

package de.adesso.tools.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by moehler on 24.05.2016.
 */
public class StringFunctions {


    private static Function<String, String> determineFormatter(String s, int len, Align align) {
        Function<String, String> formatter = null;
        switch (checkNotNull(align, "Missing align definition!!")) {
            case RIGHT:
                formatter = padIntern(len, (len - s.length()));
                break;
            case CENTER:
                formatter = padIntern(len, (len - s.length()) / 2);
                break;
            case LEFT:
                formatter = padIntern(len, 0);
                break;
            default:
                throw new IllegalArgumentException("Unknown align request!!");
        }
        return formatter;
    }

    private static Function<String, String> padIntern(int len, int pos) {
        return (s) -> {
            char[] c = new char[len];
            Arrays.fill(c, ' ');
            try {
                System.arraycopy(s.toCharArray(), 0, c, pos, s.length());
            } catch (Exception e) {
                System.out.printf("len=%d-%d-%d-%d-%s\n", len, c.length, s.length(), pos, s);
                throw e;
            }
            return String.valueOf(c);
        };
    }

    public static Function<String, String> wrap(int len, Align align) {
        return (s) -> {
            final Stream<String> stream = Splitter.on(' ').omitEmptyStrings().splitToList(s).stream();
            final LinkedList<String> normalized = stream.reduce(new LinkedList<>(), (l, w) -> {

                // FIXME: When Lookup forget to consider the commas! Lets do this tomorrow!!!
                if (((l.isEmpty() ? "" : l.getLast()) + w).length() > len) {

                    String last = l.removeLast();
                    l.add(determineFormatter(last,len,align).apply(last));
                    l.add(w);
                } else {
                    l.add(
                            (l.isEmpty())
                                    ? w
                                    : l.removeLast() + ' ' + w
                    );
                }
                return l;
            }, (BinaryOperator<LinkedList<String>>) (ll, lr) -> {
                ll.addAll(lr);
                return ll;
            });
            return Joiner.on('\n').join(normalized);
        };
    }

    enum Align {
        LEFT,
        RIGHT,
        CENTER;
    }
}
