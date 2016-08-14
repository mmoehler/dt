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

import com.google.common.collect.Sets;
import de.adesso.dtmg.util.Dump;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.IntStream;

import static de.adesso.dtmg.export.quine.utils.ConversionUtils.c;
import static java.lang.Math.pow;

/**
 * Created by mmoehler on 29.07.16.
 */
public class ExpParserTest {

    @Test
    public void testParse() throws Exception {
        PrimitiveIterator.OfInt it0 = cycle(0).iterator();
        PrimitiveIterator.OfInt it1 = cycle(1).iterator();
        PrimitiveIterator.OfInt it2 = cycle(2).iterator();
        for (int i = 0; i < 10; i++) {
            System.out.printf("%d.%d.%d\n", it0.nextInt(), it1.nextInt(), it2.nextInt());
        }

    }

    @Test
    public void testIndexOf() {
        int[] pos = {0,3};
        HashSet<Integer> set = Sets.newHashSet(0,3);
        char[] r = "-33-".toCharArray();
        char[] r0 = new char[4];
        Iterator<Integer> intF[] = new Iterator[pos.length];
        for (int i = 0; i < pos.length; i++) {
            intF[i] = cycle(i).iterator();
        }
        for (int ii = 0; ii < Math.pow(2,pos.length); ii++) {


            Iterator<Iterator<Integer>> itP = Arrays.stream(intF).iterator();
            for (int i = (4-1); i >= 0; i--) {
                boolean contains = contains(pos, i);
                r0[i] = (contains)
                        ? c(itP.next().next())
                        : r[i];
            }
            System.out.println(String.valueOf(r0));

        }
    }

    public static boolean contains(int[] a, int i) {
        for (int j = 0; j < a.length; j++) {
            if(a[j]==i) return true;
        }
        return false;
    }

    public static IntStream cycle(final int period) {
        return IntStream.iterate(0, i -> i + 1).map(idx -> (int) (idx / pow(2, period) % 2));
    }

    @Test
    public void testDenormalize() throws Exception {
        String exp = "-10-";
        List<String> list = new ExpParser().denormalizeTerm(exp.toCharArray(), new int[]{0,3},4);
        Dump.dumpList1DItems("Denormalized Terms", list);
    }
}