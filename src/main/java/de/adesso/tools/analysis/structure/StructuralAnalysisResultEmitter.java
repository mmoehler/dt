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

package de.adesso.tools.analysis.structure;

import com.google.common.base.Strings;
import de.adesso.tools.Dump;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static com.google.common.base.Strings.repeat;

/**
 * Created by mmoehler on 01.04.16.
 */
@Singleton
public class StructuralAnalysisResultEmitter implements BiFunction<List<Indicator>, Integer, String> {

    public static final String STRUCTURE_ANALYSIS_RULE = "STRUCTURE ANALYSIS RULE %2s ";

    @Override
    public String apply(List<Indicator> analysisResult, Integer countRules) {

        Dump.dumpList1DItems("ANALYSIS-RESULTS", analysisResult);
        Dump.dumpList1DItems("COUNT-RULES", Arrays.asList(String.valueOf(countRules)));

        P p = new P();
        Iterator<Indicator> indics = analysisResult.iterator();
        String header = IntStream.rangeClosed(1, countRules)
                .mapToObj(i -> Strings.padStart(String.valueOf(i), 2, ' '))
                .reduce("", (a, b) -> a + b);

        p.prlnps(header, STRUCTURE_ANALYSIS_RULE.length()+header.length()-2, ' ');
        for (int i = 0; i < countRules-1; i++) {
            p.prf(STRUCTURE_ANALYSIS_RULE, ((i + 1)%10));
            for (int j = 0; j < countRules; j++) {
                if (j > i) {
                    p.pr(indics.next().getCode() + " ");
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

        return p.toString();
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
