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
import java.util.function.BinaryOperator;

import static de.adesso.tools.analysis.structure.Indicators.*;


/**
 * Created by moehler ofList 31.03.2016.
 */
public class ActionComparisonOperator implements BinaryOperator<Indicator> {
    private static final EnumSet<Indicators> INDICATORS = EnumSet.of(XX, MI);
    private static final ImmutableTable<Indicator, Indicator, Indicator> RULES =
            new ImmutableTable.Builder<Indicator, Indicator, Indicator>()
                    .put(XX, XX, EQ)
                    .put(XX, MI, NE)
                    .put(MI, XX, NE)
                    .put(MI, MI, EQ)
                    .build();

    private static void checkIndicators(Indicator... indicators) {
        for (Indicator i : indicators) {
            //noinspection SuspiciousMethodCalls
            if (!INDICATORS.contains(i)) {
                throw new IllegalArgumentException(String.format("Illegal indicator: %s!", i.getCode()));
            }
        }
    }

    @Override
    public Indicator apply(Indicator left, Indicator right) {
        checkIndicators(left, right);
        return RULES.get(left, right);
    }
}
