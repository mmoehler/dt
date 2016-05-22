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

import com.google.common.collect.ImmutableTable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BinaryOperator;

import static de.adesso.tools.analysis.structure.Indicators.*;

/**
 * Created by moehler ofList 29.03.2016.
 */
public class ConditionComparisonResultCombiner implements BinaryOperator<Indicator> {
    private static final EnumSet<Indicators> L_INDICATORS = EnumSet.of(EQ, LO, GT, NE, XX, NI);
    private static final EnumSet<Indicators> R_INDICATORS = EnumSet.of(EQ, LO, GT, NE);
    private static final ImmutableTable<Indicator, Indicator, Indicator> RULES =
            new ImmutableTable.Builder<Indicator, Indicator, Indicator>()
                    .put(EQ, EQ, EQ)
                    .put(EQ, LO, LO)
                    .put(EQ, GT, GT)
                    .put(EQ, NE, NE)
                    .put(NE, EQ, NE)
                    .put(NE, LO, NI)
                    .put(NE, GT, NI)
                    .put(NE, NE, NI)
                    .put(LO, EQ, LO)
                    .put(LO, LO, LO)
                    .put(LO, GT, XX)
                    .put(LO, NE, NI)
                    .put(GT, EQ, GT)
                    .put(GT, LO, XX)
                    .put(GT, GT, GT)
                    .put(GT, NE, NI)
                    .put(XX, EQ, XX)
                    .put(XX, LO, XX)
                    .put(XX, GT, XX)
                    .put(XX, NE, NI)
                    .put(NI, EQ, NI)
                    .put(NI, NE, NI)
                    .put(NI, LO, NI)
                    .put(NI, GT, NI)

                    .build();

    private static void checkIndicators(Indicator left, Indicator right) {
        //noinspection SuspiciousMethodCalls
        @SuppressWarnings("SuspiciousMethodCalls") Optional<Indicator> forbidden = Optional.ofNullable(
                (!L_INDICATORS.contains(left))
                        ? left
                        : ((!R_INDICATORS.contains(right))
                        ? right
                        : null));


        if (forbidden.isPresent()) {
            throw new IllegalArgumentException(String.format("Illegal indicator: %s!", forbidden.get().getCode()));
        }
    }

    @Override
    public Indicator apply(Indicator left, Indicator right) {
        checkIndicators(left, right);
        return RULES.get(left, right);
    }
}
