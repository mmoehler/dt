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

import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by moehler on 24.05.2016.
 */
public class StringFunctions {


    public static Function<String, List<String>> split() {
        return (s) -> Splitter.on(Pattern.compile("[ ]+")).trimResults().splitToList(s);
    }

    public static Function<List<String>, List<String>> normalize(final int len) {
        return (l) -> l.stream().reduce(new LinkedList<>(), accumulator(len), combiner());
    }


    public static Function<String, String> justify(final int len, final Align align) {
        return (str) -> align.get().apply(str.trim(),len);
    }

    private static BiFunction<LinkedList<String>, String, LinkedList<String>> accumulator(final int len) {
        return (l, w) -> {
            w = w.trim();
            final String lookup1 = (l.isEmpty() ? "" : l.getLast()) + ' ' + w;
            final String nextElement = (lookup1.length() > len) ? w : (l.isEmpty()) ? w : l.removeLast() + ' ' + w;
            l.add(nextElement);
            return l;
        };
    }

    private static BinaryOperator<LinkedList<String>> combiner() {
        return (ll, lr) ->
                Stream.concat(ll.stream(), lr.stream())
                        .collect(Collectors.toCollection(LinkedList<String>::new));
    }

    enum Align implements Supplier<BiFunction<String, Integer, String>> {
        RIGHT(
                () -> (s,l) -> {
                    char[] c = new char[l];
                    Arrays.fill(c,' ');
                    for (int i = s.length()-1, j = c.length - 1; i >= 0 ;) {
                        c[j--] = s.charAt(i--);
                    }
                    return String.valueOf(c);
                }
        ),
        LEFT(
                () -> (s,l) -> {
                    char[] c = new char[l];
                    Arrays.fill(c,' ');
                    for (int i = 0; i < s.length(); i++) {
                        c[i] = s.charAt(i);
                    }
                    return String.valueOf(c);
                }
        ),
        CENTER(
                () -> (s,l) -> {
                    char[] c = new char[l];
                    Arrays.fill(c,' ');
                    int ofs = (l -s.length())/2;
                    for (int i = 0; i < s.length(); i++) {
                        c[ofs++] = s.charAt(i);
                    }
                    return String.valueOf(c);
                }
        );

        private final Supplier<BiFunction<String, Integer, String>> delegate;

        Align(Supplier<BiFunction<String, Integer, String>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public BiFunction<String, Integer, String> get() {
            return this.delegate.get();
        }
    }

    private static BiFunction<String,Integer,String> rightJustified(String text, int len) {
        return (s,l) -> {
            char[] c = new char[l];
            Arrays.fill(c,' ');
            for (int i = s.length()-1, j = c.length - 1; i >= 0 ;) {
                c[j--] = s.charAt(i--);
            }
            return String.valueOf(c);
        };
    }
}
