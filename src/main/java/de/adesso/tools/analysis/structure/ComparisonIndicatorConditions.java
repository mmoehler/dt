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

import java.util.function.BinaryOperator;

/**
 * Created by moehler on 29.03.2016.
 */
public enum ComparisonIndicatorConditions  implements BinaryOperator<ComparisonIndicatorConditions> {
    // @formatter:off
        EQ(0,"\u003D"),
        NE(1,"\u2260"),
        LO(2,"\u003C"),
        GT(3,"\u003E"),
        XX(4,"\u0058"),
        NI(5,"\u2262"),
        AS(6,"\u002A"),
        MI(7,"\u002D"),
        RR(8,"\u0052"),
        CC(9,"\u0043");
        // @formatter:on

    private final static ComparisonIndicatorConditions[][] JOIN_RULES_CONDITION = {
            // @formatter:off
                //EQ,NE,LO,GT
                //------------------
                 {EQ,LO,GT,NE},// EQ
                 {NE,NI,NI,NI},// NE
                 {LO,LO,XX,NI},// LO
                 {GT,XX,GT,NI},// GT
                 {XX,XX,XX,NI},// XX
                 {NI,NI,NI,NI},// NI
                // @formatter:on
    };

    private final static ComparisonIndicatorConditions[][] JOIN_RULES_ACTIONS = {
            // @formatter:off
                //EQ,NE
                //------------------
                 {EQ,NE},// EQ
                 {NE,NE},// NE
                // @formatter:on
    };


    private final int id;
    private final String code;

    ComparisonIndicatorConditions(int id, String code) {
        this.id= id;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    @Override
    public ComparisonIndicatorConditions apply(ComparisonIndicatorConditions left, ComparisonIndicatorConditions right) {
        return JOIN_RULES_CONDITION[left.id][right.id];
    }
}
