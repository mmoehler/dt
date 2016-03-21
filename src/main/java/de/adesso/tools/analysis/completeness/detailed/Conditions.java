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

import de.adesso.tools.util.tuple.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.adesso.tools.common.Reserved.isDASH;
import static de.adesso.tools.common.Reserved.isNO;
import static de.adesso.tools.common.Reserved.isYES;

/**
 * Created by mmoehler on 19.03.16.
 */
public class Conditions {

    /**
     * B1 Gibt es für mindestens eine Bedingung das Anzeigerpaar YES/NO bzw. NO/YES ?
     */
    public static Function<List<Tuple2<String, String>>,Integer> B1 = new Function<List<Tuple2<String, String>>, Integer>() {
        @Override
        public Integer apply(List<Tuple2<String, String>> pairs) {
            final Optional<Tuple2<String, String>> optB1 = pairs.stream().filter(
                    x -> isNO(x._1()) && isYES(x._2()) || isNO(x._2()) && isYES(x._1()))
                    .findFirst();
            return optB1.isPresent() ? 1 : 0;
        }
    };

    /**
     * B2 Sind die Anzeigerpaare für alle Bedingungen identisch?
     */
    public static Function<List<Tuple2<String, String>>,Integer> B2 = new Function<List<Tuple2<String, String>>, Integer>() {
        @Override
        public Integer apply(List<Tuple2<String, String>> pairs) {
            return pairs.stream().allMatch(Predicate.isEqual(pairs.get(0))) ? 1 : 0;
        }
    };

    /**
     * B3 Gibt es für mindestens eine Bedingung das Anzeigerpaar -/YES bzw. -/NO ?
     */
    public static Function<List<Tuple2<String, String>>,Integer> B3 = new Function<List<Tuple2<String, String>>, Integer>() {
        @Override
        public Integer apply(List<Tuple2<String, String>> pairs) {
            final Optional<Tuple2<String, String>> optB1 = pairs.stream().filter(
                    x -> isDASH(x._1()) && ( isNO(x._2()) || isYES(x._2())))
                    .findFirst();
            return optB1.isPresent() ? 1 : 0;
        }
    };

    /**
     * B4 Steht in mehreren Bedingungen ein Anzeigerpaar des Types -/YES, -/NO ?
     */
    public static Function<List<Tuple2<String, String>>,Integer> B4 = new Function<List<Tuple2<String, String>>, Integer>() {
        @Override
        public Integer apply(List<Tuple2<String, String>> pairs) {
            return pairs.stream().filter(
                    x -> isDASH(x._1()) && ( isNO(x._2()) || isYES(x._2())))
                    .count() > 1 ? 1 : 0;
        }
    };

}
