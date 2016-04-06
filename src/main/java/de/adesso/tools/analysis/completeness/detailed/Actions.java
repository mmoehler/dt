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
import de.adesso.tools.common.MatrixBuilder;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static de.adesso.tools.common.Reserved.*;

/**
 * Created by mmoehler on 19.03.16.
 */
public class Actions {


    public static BiFunction<List<String>, List<String>, List<List<String>>> A1 =
            (t, u) -> {
                System.out.println(String.format("A1 invoked with %s, %s!", t, u));
                List<List<String>> ret = MatrixBuilder.on(t).dim(1, t.size()).transposed().build();
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
                return MatrixBuilder.on(processed).dim(processed.size(), 1).build();
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A5 =
            (rf, ri) -> {
                System.out.println(String.format("A5 invoked with %s, %s!", rf, ri));

                // counting dashes
                long dashCount = rf.stream().filter(s -> isDASH(s)).count();

                List<List<String>> result = MatrixBuilder.empty().dim(rf.size(), (int) dashCount).build();

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

    public static <T> void dumpTableItems(String msg, List<List<T>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }
}
