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

package de.adesso.dtmg.export;

import de.adesso.dtmg.analysis.structure.DefaultStructuralAnalysis;
import de.adesso.dtmg.analysis.structure.Indicator;
import de.adesso.dtmg.analysis.structure.StructuralAnalysis;
import de.adesso.dtmg.export.ascii.AsciiRow;
import de.adesso.dtmg.export.ascii.AsciiTable;
import de.adesso.dtmg.export.ascii.Emitter;
import de.adesso.dtmg.export.ascii.Formatter;
import de.adesso.dtmg.functions.MoreCollectors;
import de.adesso.dtmg.model.DecisionTable;
import de.adesso.dtmg.util.output.Align;
import de.adesso.dtmg.util.output.TableFormat;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.adesso.dtmg.functions.Adapters.Matrix.adapt;

/**
 * Created by moehler on 31.05.2016.
 */
public class AsciiExportFunctions {

    public static final String STRUCTURE_ANALYSIS_RULE = "STRUCTURE ANALYSIS RULE %2s ";
    public static final String STR_DOR = ".";

    public static Function<DecisionTable, String> exportDecisionTable(TableFormat format) {
        return dt -> Stream.of(dt)
                .map(normalizeDecisionTable())
                .map(formatDecisionTable(format))
                .collect(MoreCollectors.toSingleObject());
    }

    private static Function<DecisionTable, AsciiTable> normalizeDecisionTable() {
        return dt -> {
            AsciiTable asciiTable = Stream.of(dt).map(new Emitter())
                    .collect(MoreCollectors.toSingleObject());
            return asciiTable;
        };
    }

    private static Function<AsciiTable, String> formatDecisionTable(TableFormat format) {
        return asciiTable -> {
            final int maxLen = 20;
            final int columns = asciiTable.getColumnCount();

            TableFormat applyedFormat = null;

            if (null == format) {

                TableFormat.Builder builder = TableFormat.newBuilder();

                for (int i = 0; i < columns; i++) {
                    switch (i) {
                        case 0:
                            builder.addColumnFormat().width(3).align(Align.LEFT).done();
                            break;
                        case 1:
                            builder.addColumnFormat().width(maxLen + 2).align(Align.LEFT).done();
                            break;
                        case 2:
                            builder.addColumnFormat().width(4).align(Align.RIGHT).done();
                        default:
                            builder.addColumnFormat().width(1).align(Align.CENTER).done();
                            break;
                    }
                }
                applyedFormat = builder.build();
            } else {

                applyedFormat = format;

            }

            String result = new Formatter(applyedFormat).apply(asciiTable);
            return result;

        };
    }

    static Function<List<Indicator>, AsciiTable> f110() {
        return indicators -> {

            // ---------------- args

            int countRules = 16;

            // ---------------------

            AsciiTable asciiTable = new AsciiTable();

            for (int i = 0; i < countRules - 1; i++) {
                AsciiRow r = new AsciiRow();
                Iterator<Indicator> it = indicators.iterator();
                for (int j = 0; j < countRules; j++) {
                    final Indicator c = it.next();
                    r.add((j > i) ? c.getCode() : STR_DOR);
                }

            }

            return null;

        };


    }

    static Function<AsciiTable, String> f200() {
        return null;
    }

    public static Function<DecisionTable, String> exportStructuralAnalysisResult() {
        return dt -> {

            StructuralAnalysis structuralAnalysis = new DefaultStructuralAnalysis();
            List<Indicator> result = structuralAnalysis.apply(adapt(dt.getConditionDefs()), adapt(dt.getActionDefs()));
            final int cols = dt.getConditionDefs().get(0).size();

            final String report = Stream.of(result).map(f110()).map(f200()).collect(MoreCollectors.toSingleObject());

            return report;

            /*

            @Override
            public Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>> apply(List< Indicator > analysisResult, Integer countRules) {


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
                */
        };
    }

    public static Function<DecisionTable, String> exportFormalComletenessCheckResult() {
        return null;
    }


}
