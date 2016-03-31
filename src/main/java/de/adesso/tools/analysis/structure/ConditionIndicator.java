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

import java.util.Arrays;
import java.util.Optional;

import static de.adesso.tools.analysis.structure.ComparisonIndicatorConditions.*;

/**
 * Created by moehler on 31.03.2016.
 */
public enum ConditionIndicator implements Indicator {
    // @formatter:off
        YY(0,"\u0059"),
        NN(1,"\u004E"),
        MI(2,"\u002D");
        // @formatter:on

    private final static ComparisonIndicatorConditions[][] RULES = {
            // @formatter:off
                //YY,NN,MI
                //------------------
                 {EQ,NE,LO},// YY
                 {NE,EQ,LO},// NN
                 {GT,GT,EQ},// MI
                // @formatter:on
    };

    private final int id;
    private final String code;

    ConditionIndicator(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public static Indicator lookup(String code) {
        final Optional<ConditionIndicator> found = Arrays.stream(ConditionIndicator.values()).filter((i) -> i.code.equals(code)).findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        throw new IllegalArgumentException(String.format("No indicator for code=%s", code));
    }

    public static Indicator lookup(int id) {
        final Optional<ConditionIndicator> found = Arrays.stream(ConditionIndicator.values()).filter((i) -> i.id == id).findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        throw new IllegalArgumentException(String.format("No indicator for id=%d", id));
    }

    @Override
    public Indicator apply(Indicator other) {
        return RULES[this.getId()][other.getId()];
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public int getId() {
        return this.id;
    }


}
