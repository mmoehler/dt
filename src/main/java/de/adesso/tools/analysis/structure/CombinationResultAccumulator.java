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
 * Created by moehler ofList 31.03.2016.
 */
public class CombinationResultAccumulator implements BinaryOperator<Indicator> {
    private static final ImmutableTable<Indicator, Indicator, Indicator> RULES =
            new ImmutableTable.Builder<Indicator, Indicator, Indicator>()
                    .put(EQ, EQ, RR)
                    .put(EQ, LO, AS)
                    .put(EQ, GT, AS)
                    .put(EQ, NE, AS)
                    .put(EQ, NI, MI)
                    .put(EQ, XX, XX)
                    .put(NE, EQ, CC)
                    .put(NE, LO, LO)
                    .put(NE, GT, GT)
                    .put(NE, NE, MI)
                    .put(NE, NI, MI)
                    .put(NE, XX, XX)
                    .build();
    private static final EnumSet<Indicators> A_INDICATORS = EnumSet.of(EQ, NE);
    private static final EnumSet<Indicators> C_INDICATORS = EnumSet.of(EQ, LO, GT, NE, NI, XX);

    private static void checkIndicators(Indicator actionResult, Indicator conditionResult) {
        Optional<Indicator> forbidden = Optional.ofNullable(
                (!A_INDICATORS.contains(actionResult))
                        ? actionResult
                        : ((!C_INDICATORS.contains(conditionResult))
                        ? conditionResult
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
