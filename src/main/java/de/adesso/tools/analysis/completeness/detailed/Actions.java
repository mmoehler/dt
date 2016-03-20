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
import de.adesso.tools.common.Reserved;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.common.Reserved.*;
import static de.adesso.tools.common.Reserved.isDASH;
import static de.adesso.tools.common.Reserved.isYES;

/**
 * Created by mmoehler on 19.03.16.
 */
public class Actions {


    public static BiFunction<List<String>, List<String>, List<List<String>>> A1 =
            new BiFunction<List<String>, List<String>, List<List<String>>>() {
                @Override
                public List<List<String>> apply(List<String> t, List<String> u) {
                    System.out.println(String.format("A1 invoked with %s, %s!", t, u));
                    return MatrixBuilder.on(t).dim(t.size(), 1).build();
                }
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A2 =
            new BiFunction<List<String>, List<String>, List<List<String>>>() {
                @Override
                public List<List<String>> apply(List<String> t, List<String> u) {
                    System.out.println(String.format("A2 invoked with %s, %s!", t, u));
                    return Collections.emptyList();
                }
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A3 =
            new BiFunction<List<String>, List<String>, List<List<String>>>() {
                @Override
                public List<List<String>> apply(List<String> t, List<String> u) {
                    System.out.println(String.format("A3 invoked with %s, %s!", t, u));
                    return Collections.emptyList();
                }
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A4 =
            new BiFunction<List<String>, List<String>, List<List<String>>>() {
                @Override
                public List<List<String>> apply(List<String> t, List<String> u) {
                    System.out.println(String.format("A4 invoked with %s, %s!", t, u));
                    List<String> processed = StreamUtils.zip(t.stream(), u.stream(), (v, w) -> {
                        switch (v) {
                            case DASH:
                                switch (w) {
                                    case NO:
                                    case YES:
                                        return w;
                                }
                            default:
                                return v;
                        }
                    }).collect(Collectors.toList());
                    return MatrixBuilder.on(processed).dim(processed.size(),1).build();
                }
            };

    public static BiFunction<List<String>, List<String>, List<List<String>>> A5 =
            new BiFunction<List<String>, List<String>, List<List<String>>>() {
                @Override
                public List<List<String>> apply(List<String> rf, List<String> ri) {
                    System.out.println(String.format("A5 invoked with %s, %s!", rf, ri));

                    // Counting the indicator combinations - / Y and - / N.
                    // The count defines the column size of the resulting 2D array

                    long dashes = rf.stream().filter(Reserved::isDASH).count();
                    String[] d = new String[rf.size() * (int) dashes];
                    Arrays.fill(d, SPACE);
                    List<List<String>> ret = MatrixBuilder.on(d).dim(rf.size(), (int) dashes).build();


                    int dashpos = 0;
                    for (int row = 0; row < rf.size(); row++) {
                        String rl = rf.get(row);
                        String rr = ri.get(row);
                        if (rl.equals(rr)) {
                            final int xr = row;
                            ret.stream().forEach(x -> x.set(xr, rr));
                        } else if (isDASH(rl)) {
                            final int xr = row;
                            int pos = dashpos++;
                            System.out.println(pos);
                            List<String> ll = new ArrayList<>();
                            IntStream.range(0, (int) dashes).forEach(i -> {
                                if (i == pos) {
                                    if (isYES(rr)) {
                                        ll.add(NO);
                                    } else {
                                        ll.add(YES);
                                    }
                                } else if (i < pos) {
                                    ll.add(DASH);
                                } else {
                                    ll.add(rr);
                                }
                            });
                            Iterator<String> it = ll.iterator();
                            ret.stream().forEach(x -> x.set(xr, it.next()));
                        }
                    }
                    return ret;
                }
            };

}
