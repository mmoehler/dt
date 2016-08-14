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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.dtmg.export.quine.utils.ConversionUtils.c;
import static java.lang.Math.pow;

/**
 * Created by moehler on 28.07.2016.
 */
public class ExpParser {
    public List<String> parse(String exp) {
        final int countVars = (int) exp.chars()
                .filter(c -> Character.isLetter(c))
                .distinct()
                .count();
        System.out.println("countVars = " + countVars);

        return Splitter.on('+').trimResults()
                .splitToList(exp)
                .stream()
                .flatMap(k -> toBinaryForm(k, countVars).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> toBinaryForm(String s, int countVars) {

        char[] r = new char[countVars];
        Arrays.fill(r, '-');
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; i++) {
            switch (c[i]) {
                case '*':
                case ' ':
                    break;
                case '!':
                case '~':
                    i++;
                    r[c[i] - 'a'] = '0';
                    break;
                default:
                    r[c[i] - 'a'] = '1';
            }
        }

        int[] pos = indicesOf(r, '-');

        if(0 == pos.length) {
            return Lists.newArrayList(String.valueOf(r));
        }

        List<String> ret = denormalizeTerm(r, pos, countVars);

        return ret;
    }

    List<String> denormalizeTerm(char[] r, int[] pos, int cVars) {
        List<String> ret = Lists.newLinkedList();
        Iterator<Integer> intF[] = new Iterator[pos.length];
        for (int i = 0; i < pos.length; i++) {
            intF[i] = cycle(i).iterator();
        }

        int countDashes = pos.length;
        for (int j = 0; j < pow(2, countDashes); j++) {
            Iterator<Iterator<Integer>> itP = Arrays.stream(intF).iterator();
            char[] r0 = new char[cVars];
            for (int i = (r.length-1); i >= 0; i--) {
                boolean contains = Ints.contains(pos, i);
                r0[i] = (contains)
                        ? c(itP.next().next())
                        : r[i];
            }
            ret.add(String.valueOf(r0));
        }

        return ret;
    }

    private static int[] indicesOf(char[] array, char target) {
        ArrayList<Integer> l = Lists.newArrayList();
        for (int index = indexOf(array, target, 0, array.length);
             index >= 0;
             index = indexOf(array, target, index + 1, array.length)) {
            l.add(index);
        }
        return Ints.toArray(l);
    }

    private static int indexOf(
            char[] array, char target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    public static IntStream cycle(final int period) {
        return IntStream.iterate(0, i -> i + 1).map(idx -> (int) (idx / pow(2, period) % 2));
    }
}
