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

package de.adesso.dtmg.analysis.structure;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple3;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static com.google.common.base.Strings.repeat;

/**
 * Created by mmoehler ofList 01.04.16.
 */
@Singleton
public class StructuralAnalysisResultEmitter implements BiFunction<List<Indicator>, Integer, Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>>> {

    public static final String STRUCTURE_ANALYSIS_RULE = "STRUCTURE ANALYSIS RULE %2s ";

    @Override
    public Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>> apply(List<Indicator> analysisResult, Integer countRules) {

        Multimap<Integer, Integer> compressibleRules = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        Multimap<Integer, Integer> redundantRules = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

        P p = new P();

        String header = IntStream.rangeClosed(1, countRules)
                .mapToObj(i -> Strings.padStart((i>9 && i%10==0)?(String.valueOf(i /10)):(" "), 2, ' '))
                .reduce("", (a, b) -> a + b);

        p.prlnps(header, STRUCTURE_ANALYSIS_RULE.length() + header.length() - 2, ' ');

        Iterator<Indicator> indics = analysisResult.iterator();
        header = IntStream.rangeClosed(1, countRules)
                .mapToObj(i -> Strings.padStart(String.valueOf(i%10), 2, ' '))
                .reduce("", (a, b) -> a + b);

        p.prlnps(header, STRUCTURE_ANALYSIS_RULE.length() + header.length() - 2, ' ');

        for (int i = 0; i < countRules - 1; i++) {
            p.prf(STRUCTURE_ANALYSIS_RULE, ((i + 1) % 10));
            for (int j = 0; j < countRules; j++) {
                if (j > i) {
                    final Indicator c = indics.next();
                    if (Indicators.AS.equals(c)) {
                        compressibleRules.put(i, j);
                    } else if (Indicators.RR.equals(c)) {
                        redundantRules.put(i, j);
                    }
                    p.pr(c.getCode() + " ");
                } else {
                    p.pr(". ");
                }
            }
            p.crlf();
        }
        p.prln("-----------------------------------------------");
        p.prln("R   Redundancy");
        p.prln("C   Contradiction");
        p.prln("<   Inclusion");
        p.prln(">   Inclusion");
        p.prln("-   Exclusion");
        p.prln("X   Clash");
        p.prln("*   Compression Note");
        p.prln("-----------------------------------------------");
        p.crlf();

        return Tuple.of(String.valueOf(p), compressibleRules, redundantRules);
    }

    static class P {
        private final StringBuilder sb = new StringBuilder();

        void pr(String s) {
            sb.append(s);
        }

        void prf(String s, Object... args) {
            sb.append(String.format(s, args));
        }

        void pr(String s, int numReps) {
            sb.append(repeat(s, numReps));
        }

        void prpe(String s, int minLen, char padChr) {
            sb.append(Strings.padEnd(s, minLen, padChr));
        }

        void prps(String s, int minLen, char padChr) {
            sb.append(Strings.padStart(s, minLen, padChr));
        }

        void prln(String s) {
            sb.append(s).append(System.lineSeparator());
        }

        void prfln(String s, Object... args) {
            sb.append(String.format(s, args)).append(System.lineSeparator());
        }

        void prln(String s, int numReps) {
            sb.append(repeat(s, numReps)).append(System.lineSeparator());
        }

        void prlnpe(String s, int minLen, char padChr) {
            sb.append(Strings.padEnd(s, minLen, padChr)).append(System.lineSeparator());
        }

        void prlnps(String s, int minLen, char padChr) {
            sb.append(Strings.padStart(s, minLen, padChr)).append(System.lineSeparator());
        }

        void crlf() {
            crlf(1);
        }

        void crlf(int numReps) {
            for (int i = 0; i < numReps; i++) {
                sb.append(System.lineSeparator());
            }
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }


}
