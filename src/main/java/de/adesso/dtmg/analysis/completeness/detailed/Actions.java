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

package de.adesso.dtmg.analysis.completeness.detailed;

import com.codepoetics.protonpack.StreamUtils;
import de.adesso.dtmg.Reserved;
import de.adesso.dtmg.util.List2DBuilder;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.dtmg.Reserved.*;

/**
 * Created by mmoehler ofList 19.03.16.
 */
public class Actions {


    public static BiFunction<List<String>, List<String>, List<List<String>>> A1 =
            (t, u) -> {
                System.out.println(String.format("A1 invoked with %s, %s!", t, u));
                List<List<String>> ret = List2DBuilder.matrixOf(t).dim(1, t.size()).transposed().build();
                return ret;
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A2 =
            (t, u) -> {
                System.out.println(String.format("A2 invoked with %s, %s!", t, u));
                return Collections.emptyList();
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A3 =
            (t, u) -> {
                System.out.println(String.format("A3 invoked with %s, %s!", t, u));
                return Collections.emptyList();
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A4 =
            (t, u) -> {
                System.out.println(String.format("A4 invoked with %s, %s!", t, u));
                List<String> processed = StreamUtils.zip(t.stream(), u.stream(), (v, w) -> {
                    switch (v) {
                        case DASH:
                            switch (w) {
                                case NO:
                                    return YES;
                                case YES:
                                    return NO;
                            }
                        default:
                            return v;
                    }
                }).collect(Collectors.toList());
                return List2DBuilder.matrixOf(processed).dim(processed.size(), 1).build();
            };
    static Indicator ProcessingRules[][] = {
                  /*  Y    N    D    */
            /*Y*/ {Indicator.Y, null, Indicator.Y,},
            /*N*/ {null, Indicator.N, Indicator.N,},
            /*D*/ {Indicator.N, Indicator.Y, Indicator.D}
    };
    public static BiFunction<List<String>, List<String>, List<List<String>>> A5 =
            (rf, ri) -> {
                System.out.println(String.format("A5 invoked with %s, %s!", rf, ri));

                // counting dashes
                final long dashCount = rf.stream().filter(Reserved::isDASH).count();
                final int cols = (int) dashCount;
                final int rows = rf.size();


                final List<List<String>> result = new ArrayList<>(rf.size());
                int dashesSeen = 0;
                Function<Tuple2<Indicator, Indicator>, List<String>> matchingProcessor;
                for (int i = 0; i < rows; i++) {

                    Indicator l = Indicator.from(rf.get(i));
                    Indicator r = Indicator.from(ri.get(i));
                    Tuple2<Indicator, Indicator> differenz = Tuple.of(l, r);

                    if (isDASH(l.code)) {
                        matchingProcessor = dashFirstIndicators(cols, dashesSeen);
                        dashesSeen++;
                    } else {
                        matchingProcessor = dashSecondOrSameIndicators(cols, dashesSeen);
                    }
                    result.add(matchingProcessor.apply(differenz));
                }

                return result;
            };

    private static Supplier<IllegalStateException> exceptionSupplier(String message) {
        return () -> new IllegalStateException(message);
    }

    private static Supplier<IllegalStateException> exceptionSupplier(String format, Object... args) {
        return () -> new IllegalStateException(String.format(format, args));
    }

    private static Function<Tuple2<Indicator, Indicator>, List<String>> dashSecondOrSameIndicators(int dashCount, int dashesSeen) {
        return (t) -> IntStream.range(0, dashCount)
                .mapToObj(i -> t._1().code)
                .collect(Collectors.toList());
    }

    private static Function<Tuple2<Indicator, Indicator>, List<String>> dashFirstIndicators(int dashCount, int dashesSeen) {
        return (t) -> IntStream.range(0, dashCount)
                .mapToObj(i -> (i < dashesSeen) ? DASH : ((i > dashesSeen) ? t._2().code : minus(t._1(), t._2())))
                .collect(Collectors.toList());
    }

    private static String minus(Indicator l, Indicator r) {
        Optional<Indicator> optional = Optional.ofNullable(ProcessingRules[l.index][r.index]);
        return optional.orElseThrow(exceptionSupplier("Illegal combination of - Subtrahed: %s and Minuend: %s!", l, r)).code();
    }

    enum Indicator {
        Y("Y", 0), N("N", 1), D("-", 2);

        final int index;
        final String code;

        Indicator(String code, int index) {
            this.code = code;
            this.index = index;
        }

        static Indicator from(String code) {
            return Arrays.stream(Indicator.values())
                    .filter(e -> e.code.equals(code))
                    .findFirst()
                    .orElseThrow(exceptionSupplier("Unknown code < %s >!", code));
        }

        public String code() {
            return code;
        }

        public int index() {
            return index;
        }
    }

}
