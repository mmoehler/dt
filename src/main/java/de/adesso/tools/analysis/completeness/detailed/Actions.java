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

package de.adesso.tools.analysis.completeness.detailed;

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.collect.ImmutableList;
import de.adesso.tools.common.builder.List2DBuilder;
import de.adesso.tools.util.tuple.Tuple2;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.common.Reserved.*;

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

    public static BiFunction<List<String>, List<String>, List<List<String>>> A5 =
            (rf, ri) -> {
                System.out.println(String.format("A5 invoked with %s, %s!", rf, ri));

                // counting dashes
                long dashCount = rf.stream().filter(s -> isDASH(s)).count();

                List<List<String>> result = List2DBuilder.empty().dim(rf.size(), (int) dashCount).build();

                int countDashesSeen = 0;

                for (int i = 0; i < rf.size(); i++) {
                    String l = rf.get(i);
                    String r = ri.get(i);
                    if (isDASH(l)) {
                        if (countDashesSeen > 0) {
                            for (int j = 0; j < countDashesSeen; j++) {
                                result.get(i).add(DASH);
                            }
                            for (int j = 0; j < i; j++) {
                                result.get(j).add(ri.get(j));
                            }
                        }
                        String s = new String();
                        switch (r) {
                            case YES:
                                s = NO;
                                break;
                            case NO:
                                s = YES;
                                break;
                            case DASH:
                                s = DASH;
                                break;
                            default:
                                // nop
                                ;
                        }
                        result.get(i).add(s);
                        countDashesSeen++;
                    } else {
                        result.get(i).add(l);
                    }
                }
                return result;
            };

    enum I {
        Y("Y", 0), N("N", 1), D("-", 2), E("E", 3);

        final int index;
        final String code;

        I(String code, int index) {
            this.code = code;
            this.index = index;
        }

        static Optional<I> from(String code) {
            return Arrays.stream(I.values())
                    .filter(e -> e.code.equals(code))
                    .findFirst();
        }

        public String code() {
            return code;
        }

        public int index() {
            return index;
        }
    }


    static I T[][] = {
                  /*  Y    N    D    */
            /*Y*/ {I.Y, I.E, I.Y,},
            /*N*/ {I.E, I.N, I.N,},
            /*D*/ {I.N, I.Y, I.D}
    };
    public static BiFunction<List<String>, List<String>, List<List<String>>> _A5 =
            (rf, ri) -> {
                System.out.println(String.format("A5 invoked with %s, %s!", rf, ri));

                // counting dashes
                final long dashCount = rf.stream().filter(s -> isDASH(s)).count();
                final int cols = (int) dashCount;
                final int rows = rf.size();


                final List<List<String>> result = List2DBuilder.empty().dim(rf.size(), (int) dashCount).build();
                int dashesSeen = 0;

                for (int i = 0; i < rows; i++) {

                    String l = rf.get(i);
                    String r = ri.get(i);

                    if (isDASH(l)) {
                        result.set(i,_minus(l,r,cols,dashesSeen));
                        dashesSeen++;
                    } else {
                        fill(result.get(i),minus(l, r),cols);
                    }
                }

                return result;
            };




    public static Function<Tuple2<I,I>, List<String>> minus(int dashCount, int dashesSeen) {
        return (t) -> IntStream.range(0, dashCount)
                .mapToObj(i -> (i < dashesSeen) ? DASH : ((i > dashesSeen) ? t._2().code : minus(t._1(), t._2())))
                .collect(Collectors.toList());

    }

    public static List<String> _minus(String l, String r, int dashCount, int dashesSeen) {
        List<String> ret = new ArrayList<>(dashCount);
        for (int i = 0; i < dashCount; i++) {
            if(i<dashesSeen) {
                ret.add(DASH);
            } else if(i>dashesSeen) {
                ret.add(r);
            } else {
                ret.add(minus(l,r));
            }
        }
        return ImmutableList.<String>builder().addAll(ret).build();
    }


    public static <T> void dumpTableItems(String msg, List<List<T>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    static List<String> add(List<String> l, int dashCount, String s) {
        for (int i = 0; i < dashCount; i++) {
            l.add(DASH);
        }
        l.add(s);
        return l;
    }

    static String minus(String l, String r) {
        return T[I.from(l).get().index][I.from(r).get().index].code;
    }

    static String minus(I l, I r) {
        return T[l.index][r.index].code;
    }


    static void fill(List<String> l, String s, int cols) {
        for (int i = 0; i < cols; i++) {
            l.add(s);
        }
    }


}
