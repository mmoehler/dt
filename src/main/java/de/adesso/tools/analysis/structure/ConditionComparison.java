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
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import static de.adesso.tools.analysis.structure.ComparisonIndicatorConditions.*;

/**
 * Created by moehler on 29.03.2016.
 */
public class ConditionComparison implements BiFunction<String,String,ComparisonIndicatorConditions> {
    private static final Set<String> INDICATORS = new HashSet<>(Arrays.asList("Y","N","-"));
    @Override
    public ComparisonIndicatorConditions apply(String left, String right) {
        checkIndicatorValidity(left);
        checkIndicatorValidity(right);
        if(left.equals(right)) {
            return EQ;
        } else if("-".equals(left)) {
            return GT;
        } else if("-".equals(right)) {
            return LO;
        } else {
            return NE;
        }
    }

    private void checkIndicatorValidity(String s) {
        if(null == s || null != s && !INDICATORS.contains(s)) {
            throw new IllegalArgumentException(String.format("Unknown indicator '%s'", s));
        }
    }
}
