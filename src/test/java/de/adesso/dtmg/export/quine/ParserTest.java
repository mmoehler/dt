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
import com.codepoetics.protonpack.StreamUtils;
import de.adesso.dtmg.Dump;
import de.adesso.dtmg.export.quine.parser.BantamParser;
import de.adesso.dtmg.export.quine.parser.Lexer;
import de.adesso.dtmg.export.quine.parser.Parser;
import de.adesso.dtmg.export.quine.parser.expressions.Expression;
import de.adesso.dtmg.functions.List2DFunctions;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Created by moehler on 19.07.2016.
 */
public class ParserTest {

    final static char H = '-';
    final static Tuple2<List<Integer>, String> EMPY_COMB_RESULT = Tuple.of(null,null);

    protected static List<Tuple2<List<Integer>, String>> determinePrimeImplicants(
            final List<Tuple2<List<Integer>, String>> mapped,
            final Map<HashMapKeyAdapter,Boolean> processedTerms) {

        // grouping the input by its count of '1'
        final Map<Long, List<Tuple2<List<Integer>, String>>> grouped = mapped.stream()
                .collect(Collectors.groupingBy((l) -> l._2().chars().filter((c) -> c == 49).count()));

        final List<Tuple2<List<Integer>, String>> result = Lists.newArrayList();

        for (long ii = 0; ii < grouped.keySet().size() - 1; ii++) {
            final long iii = ii;

            // #2 determine possible combinations
            final Permutation<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> permutation = new Permutation<>(grouped.get(iii), grouped.get(iii + 1));
            final List<Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>>> paired = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(permutation.iterator(), Spliterator.ORDERED), false)
                    .collect(Collectors.toList());

            // #4 combine them
            final List<Tuple2<List<Integer>, String>> combined = paired.stream()
                    .map(t -> doCombine(t, processedTerms))
                    .filter(e -> e != EMPY_COMB_RESULT)
                    .distinct()
                    .collect(Collectors.toList());

            // #5 if nothing more to combine filter out the prime implicants and return them
            if(combined.isEmpty()) {

                final List<Tuple2<List<Integer>,String>> primeImplicants = processedTerms.entrySet().stream()
                        .filter(e -> !e.getValue())
                        .map(e -> e.getKey())
                        .distinct()
                        .map(HashMapKeyAdapter::get)
                        .collect(Collectors.toList());

                return primeImplicants;
            }
            result.addAll(combined);
        }
        return determinePrimeImplicants(result,processedTerms);
    }

    private static <T> List<T> concat(List<T> l, List<T> r) {
        List<T> ret = new ArrayList<>(l.size() + r.size());
        ret.addAll(l);
        ret.addAll(r);
        return Collections.unmodifiableList(ret);
    }

    private static Tuple2<List<Integer>, String> doCombine(
            Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> pair,
            Map<HashMapKeyAdapter,Boolean> cobinationResults) {
        Tuple2<List<Integer>, String> ret;
        String s0 = pair.left._2();
        String s1 = pair.right._2();
        BinaryOperator<Character> comb = (l, r) -> (l != '-' && r != '-' && l != r) ? '-' : l;

        final boolean b = isCombinable(pair);
        System.out.println(String.format(">>> %s -> %s", String.valueOf(pair), String.valueOf(b)));

        HashMapKeyAdapter kl = HashMapKeyAdapter.adapt(pair.left);
        HashMapKeyAdapter kr = HashMapKeyAdapter.adapt(pair.right);

        if(isCombinable(pair)) {
            char c0[] = s0.toCharArray();
            char c1[] = s1.toCharArray();
            char c2[] = new char[s1.length()];
            for (int i = 0; i < c0.length; i++) {
                c2[i] = comb.apply(c0[i], c1[i]);
            }
            final String s = String.valueOf(c2);
            ret = Tuple.of(concat(pair.left._1(),pair.right._1()),s);
            cobinationResults.put(kl, true);
            cobinationResults.put(kr, true);

        } else {
            ret = EMPY_COMB_RESULT;
            if(!cobinationResults.containsKey(kl)) {
                cobinationResults.put(kl, false);
            }
            if(!cobinationResults.containsKey(kr)) {
                cobinationResults.put(kr, false);
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
    public void testQuineMcCluskey() throws Exception {
        String[] terms = {
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
        final List<Tuple2<List<Integer>, String>> preparedTerms = Arrays.stream(terms)
                .map(e -> {
                    Integer k = new Integer(Integer.parseInt(e, 2));
                    LinkedList<Integer> l = new LinkedList<Integer>();
                    l.add(k);
                    Tuple2<List<Integer>, String> t = Tuple.of(l, e);
                    return t;
                }).collect(Collectors.toList());

        Map<HashMapKeyAdapter,Boolean> processedTerms = new HashMap<>();
        final List<Tuple2<List<Integer>, String>> primeImplicants = determinePrimeImplicants(preparedTerms, /*collected,*/ processedTerms);

        Dump.dumpList1DItems("PRIMEIMPLICANTS", primeImplicants);

        // determine dimensions of the prime implicants - min term matrix
        List<List<Integer>> matrix = createCalculationMatrix(primeImplicants);

        // perform COLUMN-DOMINANZ-CHECK
        matrix = performColumnDominanceCheck(primeImplicants,matrix);

        Dump.dumpList1DItems("NULLEDMATRIX",matrix);

        // perform ROW-DOMINANZ-CHECK



        // detect row with the most 1's

        long index = StreamUtils.zipWithIndex(matrix.stream().map(a -> a.stream().mapToInt(Integer::intValue).sum()))
                .collect(Collectors.maxBy((a, b) -> a.getValue().compareTo(b.getValue()))).get().getIndex();

        System.out.println("index = " + index);

        /** rowIndices.add((int)index); */



        /**************

        String result = rowIndices.stream().map(i -> primeImplicants.get(i)._2())
                .map(this::toMinTerm)
                .reduce("",(ll,rr) -> Strings.isNullOrEmpty(ll) ? rr : ll + " + " + rr);

        System.out.println("result = " + result);

        **************/

    }

    public List<List<Integer>> performColumnDominanceCheck(List<Tuple2<List<Integer>, String>> primeImplicants, List<List<Integer>> matrix) {
        List<List<Integer>> matrixT = List2DFunctions.transpose(matrix);
        Dump.dumpTableItems("MATRIX INITIALIZED TRANSPOSED", matrixT);

        // determine row indices of 1 where sum entries is 1
        List<Long> colIndices = StreamUtils.zipWithIndex(matrixT.stream().map(a -> a.stream().mapToInt(Integer::intValue).sum()))
                .filter(k -> k.getValue().intValue() == 1)
                .map(l -> l.getIndex())
                .collect(Collectors.toList());

        Dump.dumpList1DItems("COL INDICES", colIndices);

        List<Integer> rowIndices = colIndices.stream()
                .mapToInt(l -> l.intValue())
                .mapToObj(i -> StreamUtils.zipWithIndex(matrixT.get(i).stream())
                        .filter(h -> h.getValue().intValue() == 1)
                        .map(k -> k.getIndex())
                        .mapToInt(d -> d.intValue())
                        .boxed()
                        .findFirst().get()
                )
                .sorted((x,y) -> y.compareTo(x))
                .collect(Collectors.toList());

        Dump.dumpList1DItems("ROW INDICES", rowIndices);

        // detect all indices of 1 of the rows with index in rowIndices
        List<Integer> columnsToNull = rowIndices.stream()
                .flatMap(i -> StreamUtils.zipWithIndex(matrix.get(i).stream())
                        .filter(j -> j.getValue().intValue() == 1)
                        .map(k -> k.getIndex())
                        .mapToInt(l -> l.intValue()).boxed())
                .sorted((x,y) -> y.compareTo(x))
                .collect(Collectors.toList());

        Dump.dumpList1DItems("COLSTONULL", columnsToNull);

        // remove the detected rows and columns
        //List<List<Integer>> matrix0 = List2DFunctions.transpose(matrix);
        for(Integer i : columnsToNull) {
            matrixT.remove(i.intValue());
        }
        List<List<Integer>> matrixN = List2DFunctions.transpose(matrixT);

        Dump.dumpList1DItems("PI",primeImplicants);

        for(Integer i : rowIndices) {
            matrixN.remove(i.intValue());
            primeImplicants.remove(i.intValue());
        }
        return matrixN;
    }

    private List<List<Integer>> createCalculationMatrix(List<Tuple2<List<Integer>, String>> primeImplicants) {
        final int rows = primeImplicants.size();
        final int cols = primeImplicants.stream().flatMap(e -> e._1().stream()).max((l, r) -> l - r).get()+1;

        final List<List<Integer>> matrix = List2DFunctions.newList2D(rows, cols, 0);
        Dump.dumpTableItems("MATRIX NORMAL", matrix);

        List<List<Integer>> piVals = primeImplicants.stream().map(k -> k._1()).collect(Collectors.toList());
        IntStream.range(0,rows).forEach(i -> piVals.get(i).forEach(j -> matrix.get(i).set(j,1)));
        Dump.dumpTableItems("MATRIX NORMAL INITIALIZED", matrix);
        return matrix;
    }

    public String toMinTerm(String l) {
        char[] c = l.toCharArray();
        String ret = new String();
        boolean flag = false;
        for (int i = 0; i < c.length; i++) {
            if('-' != c[i]) {
                if(flag) {
                    ret += '*';
                } else {
                    flag = !flag;
                }
                if('0' == c[i]) {
                    ret += '!';
                }
                ret += (char)('a'+i);
            }
        }
        return ret;
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