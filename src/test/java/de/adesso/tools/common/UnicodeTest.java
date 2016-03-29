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

package de.adesso.tools.common;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * Created by moehler on 29.03.2016.
 */
public class UnicodeTest {


    enum COMPARISON_INDICATOR implements BinaryOperator<COMPARISON_INDICATOR>{
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

        private final static COMPARISON_INDICATOR[][] JOIN_RULES_I_ALL = {
                //EQ,NE,LO,GT
                //------------------
                 {EQ,LO,GT,NE},// EQ
                 {NE,NI,NI,NI},// NE
                 {LO,LO,XX,NI},// LO
                 {GT,XX,GT,NI},// GT
                 {XX,XX,XX,NI},// XX
                 {NI,NI,NI,NI},// NI
        };

        private final int id;
        private final String code;

        COMPARISON_INDICATOR(int id, String code) {
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
        public COMPARISON_INDICATOR apply(COMPARISON_INDICATOR left, COMPARISON_INDICATOR right) {
            return JOIN_RULES_I_ALL[left.id][right.id];
        }
    }

    @Test
    public void testPrintUnicode() {
        System.out.println("\u2260"); // NE
        System.out.println("\u2262"); // NI
        System.out.println("\u003C"); // LO
        System.out.println("\u003E"); // GT
        System.out.println("\u003D"); // EQ
        System.out.println("\u002A"); // AS
        System.out.println("\u002D"); // MI
        System.out.println("\u0058"); // XX
        System.out.println("\u0052"); // RR
        System.out.println("\u0043"); // CC
    }
}
