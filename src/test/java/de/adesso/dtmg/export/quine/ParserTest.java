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

import com.beust.jcommander.internal.Lists;
import de.adesso.dtmg.Dump;
import de.adesso.dtmg.export.quine.expressions.Expression;
import de.adesso.dtmg.util.combination.Pair;
import de.adesso.dtmg.util.combination.Permutation;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by moehler on 19.07.2016.
 */
public class ParserTest {

    final static char H = '-';
    final static Tuple2<List<Integer>, String> EMPY_COMB_RESULT = Tuple.of(null,null);

    protected static List<Tuple2<List<Integer>, String>> normalize(final List<Tuple2<List<Integer>, String>> mapped, List<List<Tuple2<List<Integer>, String>>> collected, final Map<String,Boolean> notCombinables) {
        final Map<Long, List<Tuple2<List<Integer>, String>>> grouped = mapped.stream().collect(Collectors.groupingBy((l) -> l._2().chars().filter((c) -> c == 49).count()));
        final List<Tuple2<List<Integer>, String>> result = Lists.newArrayList();

        Dump.dumpMapLongIntegerListString("GROUPED",grouped);

        for (long ii = 0; ii < grouped.keySet().size() - 1; ii++) {
            final long iii = ii;

            // #2 determine possible combinations
            final Permutation<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> permutation = new Permutation<>(grouped.get(iii), grouped.get(iii + 1));
            final List<Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>>> paired = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(permutation.iterator(), Spliterator.ORDERED), false)
                    .collect(Collectors.toList());

            // #4 combine them
            final List<Tuple2<List<Integer>, String>> combined = paired.stream()
                    .map(t -> doCombine(t, notCombinables))
                    .filter(e -> e != EMPY_COMB_RESULT)
                    .distinct()
                    .collect(Collectors.toList());

            if(combined.isEmpty()) {
                return Collections.emptyList();
            }

            //Dump.dumpList1DItems("COMBINED", combined);

            result.addAll(combined);
        }
        collected.add(result);
        return normalize(result,collected,notCombinables);
    }

    private static <T> List<T> concat(List<T> l, List<T> r) {
        List<T> ret = new ArrayList<>(l.size() + r.size());
        ret.addAll(l);
        ret.addAll(r);
        return Collections.unmodifiableList(ret);
    }

    private static Tuple2<List<Integer>, String> doCombine(Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> pair, Map<String,Boolean> cobinationResults) {
        Tuple2<List<Integer>, String> ret;
        String s0 = pair.left._2();
        String s1 = pair.right._2();
        BinaryOperator<Character> comb = (l, r) -> (l != '-' && r != '-' && l != r) ? '-' : l;

        final boolean b = isCombinable(pair);
        System.out.println(String.format(">>> %s -> %s", String.valueOf(pair), String.valueOf(b)));

        if(isCombinable(pair)) {
            char c0[] = s0.toCharArray();
            char c1[] = s1.toCharArray();
            char c2[] = new char[s1.length()];
            for (int i = 0; i < c0.length; i++) {
                c2[i] = comb.apply(c0[i], c1[i]);
            }
            final String s = String.valueOf(c2);
            ret = Tuple.of(concat(pair.left._1(),pair.right._1()),s);
            cobinationResults.put(pair.left._2(), true);
            cobinationResults.put(pair.right._2(), true);

        } else {
            ret = EMPY_COMB_RESULT;
            if(!cobinationResults.containsKey(pair.left._2())) {
                cobinationResults.put(pair.left._2(), false);
            }
            if(!cobinationResults.containsKey(pair.right._2())) {
                cobinationResults.put(pair.right._2(), false);
            }
        }
        return ret;
    }

    private static boolean isCombinable(Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> t) {
        char c0[] = t.left._2().toCharArray();
        char c1[] = t.right._2().toCharArray();
        BiPredicate<String, String> p0 = (sl, sr) -> indicesOf(sl, '-').equals(indicesOf(sr, '-'));
        BiPredicate<Character, Character> p = (l, r) -> (l != '-' && r != '-' && l != r);
        int counter = 0;
        if (p0.test(t.left._2(), t.right._2())) {
            for (int i = 0; i < c0.length; i++) {
                if (p.test(c0[i], c1[i])) {
                    counter++;
                }
            }
        }
        return counter == 1;
    }

    private static List<Integer> indicesOf(String s, char c) {
        final String cs = String.valueOf(c);
        int index = s.indexOf(cs);
        List<Integer> l = new ArrayList<>();
        l.add(index);
        if (index >= 0) {
            for (; index >= 0; index = s.indexOf(cs, index + 1)) {
                l.add(index);
            }
        }
        return l;
    }

    static char c(int i) {
        if (i >= 0 && i <= 9) {
            return (char) (i + 48);
        }
        throw new IndexOutOfBoundsException();
    }

    static int i(char c) {
        if (c >= 48 && c <= 57) {
            return (c - 48);
        }
        throw new IndexOutOfBoundsException();
    }

    static char[] c(int i[]) {
        char ret[] = new char[i.length];
        for (int j = 0; j < i.length; j++) {
            ret[j] = c(i[j]);
        }
        return ret;
    }

    static int[] i(char c[]) {
        return String.valueOf(c).chars().map(a -> i((char) a)).toArray();
    }

    @Test
    public void testCombine() throws Exception {
        String[] i = {
                "0000",
                "0001",
                "0010",
                "1000",
                "0101",
                "0110",
                "1001",
                "1010",
                "0111",
                "1110"
        };

        // #0 Number the entries
        final List<Tuple2<List<Integer>, String>> mapped = Arrays.stream(i)
                .peek(e0 -> System.out.println(Integer.parseInt(e0, 2)))
                .map(e -> {
                    Integer k = new Integer(Integer.parseInt(e, 2));
                    LinkedList<Integer> l = new LinkedList<Integer>();
                    l.add(k);
                    Tuple2<List<Integer>, String> t = Tuple.of(l, e);
                    return t;
                }).collect(Collectors.toList());

        Dump.dumpList1DItems("MAPPED", mapped);

        final List<Tuple2<List<Integer>, String>> result = Lists.newArrayList();

        // >>>>> RECURSION <<<<<
        List<List<Tuple2<List<Integer>, String>>> collected = Lists.newLinkedList();
        Map<String,Boolean> notCombinables = new TreeMap<>();

        normalize(mapped, collected, notCombinables);

        final List<String> notCombined = notCombinables.entrySet().stream()
                .filter(e -> !e.getValue())
                .map(e -> e.getKey())
                .distinct()
                .collect(Collectors.toList());

        Dump.dumpList1DItems("NOTCOMBINABLES", notCombined);

        //Dump.dumpMap("NOTCOMBINED", notCombinables);

        for(List<Tuple2<List<Integer>, String>> l : collected) {
            Dump.dumpList1DItems("RET",l);
        }

        Dump.dumpTableItems("RESULT", collected);
    }

    @Test
    public void testI2C() throws Exception {
        for (int i = 0; i < 255; i++) {
            if (Character.isDigit(i) || '-' == i) {
                final String s = String.format("%s : %03d", String.valueOf((char) i), i);
                System.out.println(s);

            }
        }

        System.out.println("=========================================");

        for (int i = 0; i < 10; i++) {
            char c = c(i);
            System.out.println("c = " + c);
        }

    }

    @Test
    public void testOther() throws Exception {


        for (int c = 0; c < 3; c++) {
            final int k = c;
            IntFunction<Integer> a0 = (r) -> r % 2;
            IntFunction<Integer> a1 = (r) -> ((r / (int) Math.pow(2, k) % (int) Math.pow(2, k)));
            IntFunction<Integer> f = (0 < c) ? a1 : a0;
            for (int r = 0; r < 8; r++) {
                System.out.print(f.apply(r));
            }
            System.out.println();
        }

    }

    @Test
    public void testEval() throws Exception {
        try {
            String source = "a*b+a*c+~b*c";
            //String source = "a*b*c";
            Lexer lexer = new Lexer(source);
            Parser parser = new BantamParser(lexer);
            Expression result = parser.parseExpression();
            final Map<String, Integer> variables = parser.getContext().getVariables();


            int cols = variables.size();
            System.out.println("cols = " + cols);
            int rows = (int) Math.pow(2, variables.size());
            System.out.println("rows = " + rows);
            char[][] data = new char[rows][cols];

            for (int c = cols - 1; c >= 0; c--) {
                final int k = c;
                Function<Integer, Character> a0 = (r) -> c(r % 2);
                Function<Integer, Character> a1 = (r) -> c(((r / (int) Math.pow(2, k) % (int) Math.pow(2, k))));
                Function<Integer, Character> f = (0 < c) ? a1 : a0;
                for (int r = 0; r < rows; r++) {
                    data[r][c] = f.apply(r);
                }
            }

            //Arrays.stream(data).forEach(a -> System.out.println(Arrays.toString(a)));

            // ausmultiplizieren
            for (int i = 0; i < data.length; i++) {
                int[] x = i(data[i]);
                variables.put("a", x[0]);
                variables.put("b", x[1]);
                variables.put("c", x[2]);
                final int eval = result.eval(parser.getContext());
                System.out.println(String.format("%s => %d", Arrays.toString(x), eval));
            }

            final Map<Long, List<char[]>> collect = Arrays.stream(data).collect(Collectors.groupingBy((l) -> String.valueOf(l).chars().filter((i) -> i == c(1)).count()));
            System.out.println();
            Dump.dumpMapLongListChrAr("COL", collect);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}