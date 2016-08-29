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

import com.google.common.collect.Lists;
import de.adesso.dtmg.util.Dump;
import de.adesso.dtmg.util.Permutation;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.adesso.dtmg.export.quine.utils.ConversionUtils.c;

/**
 * Created by moehler on 27.07.2016.
 */
public class QuineMcCluskeyTest {

    QuineMcCluskey f;

    @BeforeMethod
    public void setup() throws Exception {
        f = new QuineMcCluskey();
    }

    @Test
    public void testApply() throws Exception {
        //final String s = f.apply("!a*!b*!c*!d + !a*b*!c*d + !a*b*c*!d + !a*b*c*d +a*!b*!c*d + a*!b*c*!d + a*b*!c*d + a*b*c*!d + a*b*c*d");
        //final String s = f.apply("a*!b*!c*!d + !a*c*d + a*b*!c + a*b*!d + b*c*d");
        //String si = "!a!b!c!d + !acd + ab!c + ab!d + bcd";
        String si = "a*!b*!c*!d + !a*c*d + a*b*!c + a*b*!d + b*c*d";
        //String si = "!a*!b*!c*!d + !a*b*!c*d + !a*b*c*!d + !a*b*c*d +a*!b*!c*d + a*!b*c*!d + a*b*!c*d + a*b*c*!d + a*b*c*d";
        //String si = "ab!c + a!bd + a!b";
        final String so = f.apply(si);

        System.out.println("INPUT  = " + si);
        System.out.println("OUTPUT = " + so);
    }

    @Test
    public void testDeterminePrimeImplicants() throws Exception {
        Map<HashMapKeyAdapter,Boolean> processedTermsCache = new HashMap<>();

        //final List<String> terms = f.parseExpression("!a*!b*!c*!d + !a*!b*!c*!d + a*!b*c*d + a*!b*c*!d + !a*b*c*d + !a*b*c*!d + !a*!b*c*d");
        //final List<String> terms = f.parseExpression("!a*!b*!c*!d + !a*b*!c*d + !a*b*c*!d + !a*b*c*d +a*!b*!c*d + a*!b*c*!d + a*b*!c*d + a*b*c*!d + a*b*c*d");
        final List<String> terms = f.parseExpression("!a!b!c!d + !acd + ab!c + ab!d + bcd");


        // #0 Number the entries
        final List<Tuple2<List<Integer>, String>> preparedTerms = terms.stream()
                .map(e -> {
                    Integer k = new Integer(Integer.parseInt(e, 2));
                    LinkedList<Integer> l = new LinkedList<Integer>();
                    l.add(k);
                    Tuple2<List<Integer>, String> t = Tuple.of(l, e);
                    return t;
                }).collect(Collectors.toList());


        final Map<Integer, Tuple2<List<Integer>, String>> primeImplicants = f.determinePrimeImplicants(preparedTerms, processedTermsCache);

        Dump.dumpMap("PIT", primeImplicants);
    }

    @Test
    public void testCycle0() throws Exception {
        int cols = 30;
        int rows = (int) Math.pow(2, cols);

        for (int i = 0; i < rows; i++) {
            final int k = (i/4 % 2) ;
            System.out.println(k);
        }
    }


    @Test
    public void testCycle1() throws Exception {

        int cols = 20;
        int rows = (int) Math.pow(2, cols);
        char[][] data = new char[rows][cols];


        for (int c = cols - 1; c >= 0; c--) {
            final int k = c;
            Function<Integer, Character> f0 = (r) -> c((int)(r/Math.pow(2,k) % 2));
            for (int r = 0; r < rows; r++) {
                data[r][c] = f0.apply(r);
            }
        }

        for(char[] d : data) {
            System.out.println(Arrays.toString(d));
        }

    }


    @Test
    public void testPerm1() throws Exception {

        final ArrayList<String> left = Lists.newArrayList("001", "002", "003", "004", "005", "006", "007", "008", "009");
        final ArrayList<String> right = Lists.newArrayList("MT", "KT", "ST", "MF", "KF", "SF");
        Permutation<String,String> p = new Permutation<>(right,left);
        final Iterator<Pair<String, String>> it = p.iterator();
        while(it.hasNext())
            System.out.println(it.next());

    }


    @Test
    public void testDoCombine() throws Exception {

    }

    @Test
    public void testIsCombinable() throws Exception {

    }

    @Test
    public void testConcat() throws Exception {

    }

    @Test
    public void testIndicesOf() throws Exception {

    }

    @Test
    public void testConvertToMinTerm() throws Exception {

    }

    @Test
    public void testToMinTerm() throws Exception {

    }

    @Test
    public void testParseExpression() throws Exception {

    }

    @Test
    public void testPerformDominanceCheck() throws Exception {

    }

    @Test
    public void testPerformRowDominanceCheck() throws Exception {

    }

    @Test
    public void testIsCompletelyFilledWithZeros() throws Exception {

    }

    @Test
    public void testCompareCoverageWithOnes() throws Exception {

    }

    @Test
    public void testPerformColumnDominanceCheck() throws Exception {

    }

    @Test
    public void testCreateCalculationMatrix() throws Exception {

    }
}