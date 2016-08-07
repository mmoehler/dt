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

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.adesso.dtmg.export.quine.parser2.ExpParser;
import de.adesso.dtmg.functions.List2DFunctions;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Created by moehler on 27.07.2016.
 */
public class QuineMcCluskey implements Function<String, String> {

    final static Tuple2<List<Integer>, String> EMPY_COMB_RESULT = Tuple.of(null, null);
    public static final int CHR_1 = 49;
    public static final char CHR_DASH = '-';
    public static final String EMPTY_STRING = "";
    public static final String STR_PLUS_SPACED = " + ";
    public static final char CHR_0 = '0';
    public static final char CHR_ASTERICS = '!';
    public static final char CHR_A = 'a';

    @Override
    public String apply(String maxTerm) {
        Map<HashMapKeyAdapter, Boolean> processedTermsCache = new HashMap<>();
        final List<String> terms = parseExpression(maxTerm);

        // #0 Number the entries
        final List<Tuple2<List<Integer>, String>> preparedTerms = terms.stream()
                .map(e -> {
                    Integer k = new Integer(Integer.parseInt(e, 2));
                    LinkedList<Integer> l = new LinkedList<Integer>();
                    l.add(k);
                    Tuple2<List<Integer>, String> t = Tuple.of(l, e);
                    return t;
                }).collect(Collectors.toList());

        final Map<Integer, Tuple2<List<Integer>, String>> primeImplicants = determinePrimeImplicants(preparedTerms, processedTermsCache);
        final List<List<Integer>> matrix = createCalculationMatrix(primeImplicants);
        final List<String> result = performDominanceCheck(primeImplicants, matrix);
        final String minTerm = convertToMinTerm(result);

        return minTerm;
    }

    public Map<Integer, Tuple2<List<Integer>, String>> determinePrimeImplicants(
            final List<Tuple2<List<Integer>, String>> mapped,
            final Map<HashMapKeyAdapter, Boolean> processedTerms) {

        // grouping the input by its count of '1'
        final Map<Long, List<Tuple2<List<Integer>, String>>> grouped = mapped.stream()
                .collect(Collectors.groupingBy((l) -> l._2().chars().filter((c) -> c == CHR_1).count()));

        if (grouped.size() < 2) {
            grouped.values().stream()
                    .flatMap(k -> k.stream())
                    .forEach(l -> processedTerms.put(HashMapKeyAdapter.adapt(l), Boolean.FALSE));

            final List<Tuple2<List<Integer>, String>> primeImplicants = processedTerms.entrySet().stream()
                    .filter(e -> !e.getValue())
                    .map(e -> e.getKey())
                    .distinct()
                    .map(HashMapKeyAdapter::get)
                    .collect(Collectors.toList());

            return StreamUtils.zipWithIndex(primeImplicants.stream())
                    .collect(Collectors.toMap(k -> (int) (k.getIndex()), v -> v.getValue()));

        }

        final List<Tuple2<List<Integer>, String>> result = Lists.newArrayList();

        // determine startpoint
        Iterator<Long> iterator = grouped.keySet().stream().sorted().iterator();
        long leftL = iterator.next();
        for (long ii = 0; ii < grouped.keySet().size() - 1; ii++) {
            long rightL = iterator.next();

            // #2 determine possible combinations
            final Permutation<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> permutation = new Permutation<>(grouped.get(leftL), grouped.get(rightL));
            leftL = rightL;

            final List<Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>>> paired = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(permutation.iterator(), Spliterator.ORDERED), false)
                    .collect(Collectors.toList());

            // #4 combine them
            final List<Tuple2<List<Integer>, String>> combined = paired.stream()
                    .map(t -> doCombine(t, processedTerms))
                    .filter(e -> e != EMPY_COMB_RESULT)
                    .distinct()
                    .collect(Collectors.toList());

            result.addAll(combined);
        }

        if (result.isEmpty()) {
            final List<Tuple2<List<Integer>, String>> primeImplicants = processedTerms.entrySet().stream()
                    .filter(e -> !e.getValue())
                    .map(e -> e.getKey())
                    .distinct()
                    .map(HashMapKeyAdapter::get)
                    .collect(Collectors.toList());

            return StreamUtils.zipWithIndex(primeImplicants.stream())
                    .collect(Collectors.toMap(k -> (int) (k.getIndex()), v -> v.getValue()));
        }

        return determinePrimeImplicants(result, processedTerms);
    }

    public Tuple2<List<Integer>, String> doCombine(
            Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> pair,
            Map<HashMapKeyAdapter, Boolean> cobinationResults) {
        Tuple2<List<Integer>, String> ret;
        String s0 = pair.left._2();
        String s1 = pair.right._2();
        BinaryOperator<Character> comb = (l, r) -> (l != CHR_DASH && r != CHR_DASH && l != r) ? CHR_DASH : l;

        HashMapKeyAdapter kl = HashMapKeyAdapter.adapt(pair.left);
        HashMapKeyAdapter kr = HashMapKeyAdapter.adapt(pair.right);

        if (isCombinable(pair)) {
            char c0[] = s0.toCharArray();
            char c1[] = s1.toCharArray();
            char c2[] = new char[s1.length()];
            for (int i = 0; i < c0.length; i++) {
                c2[i] = comb.apply(c0[i], c1[i]);
            }
            final String s = String.valueOf(c2);
            ret = Tuple.of(concat(pair.left._1(), pair.right._1()), s);
            cobinationResults.put(kl, true);
            cobinationResults.put(kr, true);

        } else {
            ret = EMPY_COMB_RESULT;
            if (!cobinationResults.containsKey(kl)) {
                cobinationResults.put(kl, false);
            }
            if (!cobinationResults.containsKey(kr)) {
                cobinationResults.put(kr, false);
            }
        }
        return ret;
    }

    public boolean isCombinable(Pair<Tuple2<List<Integer>, String>, Tuple2<List<Integer>, String>> t) {
        char c0[] = t.left._2().toCharArray();
        char c1[] = t.right._2().toCharArray();
        BiPredicate<String, String> p0 = (sl, sr) -> indicesOf(sl, CHR_DASH).equals(indicesOf(sr, CHR_DASH));
        BiPredicate<Character, Character> p = (l, r) -> (l != CHR_DASH && r != CHR_DASH && l != r);
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

    public <T> List<T> concat(List<T> l, List<T> r) {
        List<T> ret = new ArrayList<>(l.size() + r.size());
        ret.addAll(l);
        ret.addAll(r);
        return Collections.unmodifiableList(ret);
    }

    public List<Integer> indicesOf(String s, char c) {
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

    public String convertToMinTerm(List<String> result) {
        return result.stream()
                .map(this::toMinTerm)
                .sorted()
                .reduce(EMPTY_STRING, (ll, rr) -> Strings.isNullOrEmpty(ll) ? rr : ll + STR_PLUS_SPACED + rr);
    }

    public String toMinTerm(String l) {
        char[] c = l.toCharArray();
        String ret = new String();
        boolean flag = false;
        for (int i = 0; i < c.length; i++) {
            if (CHR_DASH != c[i]) {
                if (CHR_0 == c[i]) {
                    ret += CHR_ASTERICS;
                }
                ret += (char) (CHR_A + i);
            }
        }
        return ret;
    }

    public List<String> parseExpression(String source) {
        return new ExpParser().parse(source);
    }

    public List<String> performDominanceCheck(Map<Integer, Tuple2<List<Integer>,
            String>> primeImplicants, List<List<Integer>> matrix) {
        List<String> result = new LinkedList<>();
        List<List<Integer>> m = matrix;
        while (!isCompletelyFilledWithZeros(m)) {
            m = performColumnDominanceCheck(primeImplicants, m, result);
            m = performRowDominanceCheck(primeImplicants, m, result);
        }
        return result;
    }

    public List<List<Integer>> performRowDominanceCheck(Map<Integer, Tuple2<List<Integer>,
            String>> primeImplicants, List<List<Integer>> matrix, List<String> result) {

        if (isCompletelyFilledWithZeros(matrix)) return matrix;

        final Map<Long, Tuple2<Long, List<Long>>> rowInfo = StreamUtils.zipWithIndex(matrix.stream()
                .map(r -> StreamUtils.zipWithIndex(r.stream())
                        .filter(c -> c.getValue().intValue() == 1)
                        .collect(Collectors.toList()))
                .map(l -> Tuple.of(l.stream().count(), l.stream().map(y -> y.getIndex()).collect(Collectors.toList()))))
                .filter(k -> !k.getValue()._2().isEmpty())
                .collect(Collectors.toMap(Indexed::getIndex, Indexed::getValue));

        List<List<Integer>> matrixT = List2DFunctions.transpose(matrix);
        final Map<Long, Tuple2<Long, List<Long>>> colInfo = StreamUtils.zipWithIndex(matrixT.stream()
                .map(r -> StreamUtils.zipWithIndex(r.stream())
                        .filter(c -> c.getValue().intValue() == 1)
                        .collect(Collectors.toList()))
                .map(l -> Tuple.of(l.stream().count(), l.stream().map(y -> y.getIndex()).collect(Collectors.toList()))))
                .filter(k -> !k.getValue()._2().isEmpty())
                .collect(Collectors.toMap(Indexed::getIndex, Indexed::getValue));

        // detect row with the maximum coverage of 1's
        final Long nextRow = rowInfo.keySet().stream()
                .max((l, r) -> compareCoverageWithOnes(l, r, rowInfo, colInfo)).get();

        final List<Long> nextCols = rowInfo.get(nextRow)._2().stream().sorted((l, r) -> r.compareTo(l)).collect(Collectors.toList());

        matrix.remove(nextRow);
        result.add(primeImplicants.remove(nextRow.intValue())._2());
        matrixT = List2DFunctions.transpose(matrix);
        for (Long i : nextCols) {
            matrixT.remove(i.intValue());
        }
        matrix = List2DFunctions.transpose(matrixT);
        return matrix;
    }


    public boolean isCompletelyFilledWithZeros(List<List<Integer>> matrix) {
        final Optional<Integer> first = matrix.stream()
                .flatMap(r -> r.stream())
                .filter(c -> c != 0)
                .findFirst();
        return !first.isPresent();
    }

    public int compareCoverageWithOnes(Long l, Long r, Map<Long, Tuple2<Long, List<Long>>> rowInfo,
                                       Map<Long, Tuple2<Long, List<Long>>> colInfo) {
        Tuple2<Long, List<Long>> lRowInfo = rowInfo.get(l);
        final int lOnes = lRowInfo._2().stream().mapToInt(k -> colInfo.get(k)._1().intValue()).sum();
        Tuple2<Long, List<Long>> rRowInfo = rowInfo.get(r);
        final int rOnes = rRowInfo._2().stream().mapToInt(k -> colInfo.get(k)._1().intValue()).sum();
        return lOnes - rOnes;
    }

    public List<List<Integer>> performColumnDominanceCheck(Map<Integer, Tuple2<List<Integer>,
            String>> primeImplicants, List<List<Integer>> matrix, List<String> result) {

        List<List<Integer>> matrixT = List2DFunctions.transpose(matrix);

        // determine row indices of 1 where sum entries is 1
        List<Long> colIndices = StreamUtils.zipWithIndex(matrixT.stream().
                map(a -> a.stream()
                        .mapToInt(Integer::intValue)
                        .sum()))
                .filter(k -> k.getValue().intValue() == 1)
                .map(l -> l.getIndex())
                .distinct()
                .sorted((l, r) -> r.compareTo(l))
                .collect(Collectors.toList());

        List<Integer> rowIndices = colIndices.stream()
                .mapToInt(l -> l.intValue())
                .mapToObj(i -> StreamUtils.zipWithIndex(matrixT.get(i).stream())
                        .filter(h -> h.getValue().intValue() == 1)
                        .map(k -> k.getIndex())
                        .mapToInt(d -> d.intValue())
                        .boxed()
                        .findFirst().get()
                )
                .distinct()
                .sorted((x, y) -> y.compareTo(x))
                .collect(Collectors.toList());

        // detect all indices of 1 of the rows with index in rowIndices
        List<Integer> columnsToNull = rowIndices.stream()
                .flatMap(i -> StreamUtils.zipWithIndex(matrix.get(i).stream())
                        .filter(j -> j.getValue().intValue() == 1)
                        .map(k -> k.getIndex())
                        .mapToInt(l -> l.intValue()).boxed())
                .sorted((x, y) -> y.compareTo(x))
                .distinct()
                .collect(Collectors.toList());

        // remove the detected rows and columns
        for (Integer i : columnsToNull) {
            matrixT.remove(i.intValue());
        }
        List<List<Integer>> matrixN = List2DFunctions.transpose(matrixT);

        for (Integer i : rowIndices) {
            matrixN.remove(i.intValue());
            result.add(primeImplicants.remove(i.intValue())._2());
        }
        return matrixN;
    }

    public List<List<Integer>> createCalculationMatrix(Map<Integer, Tuple2<List<Integer>, String>> primeImplicants) {

        final int rows = primeImplicants.size();
        final int cols = primeImplicants.values().stream()
                .flatMap(e -> e._1().stream())
                .max((l, r) -> l - r)
                .get() + 1;

        final List<List<Integer>> matrix = List2DFunctions.newList2D(rows, cols, 0);

        List<List<Integer>> piVals = primeImplicants.values().stream().
                map(k -> k._1()).collect(Collectors.toList());
        IntStream.range(0, rows)
                .forEach(i -> piVals.get(i)
                        .forEach(j -> matrix.get(i).set(j, 1)));
        return matrix;
    }

}
