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

/**
 * Created by moehler ofList 31.03.2016.
 */
public enum Indicators implements Indicator {
    // @formatter:off
        EQ("\u003D"),
        NE("\u2260"),
        LO("\u003C"),
        GT("\u003E"),
        XX("\u0058"),
        NI("\u2262"),
        AS("\u002A"),
        YY("\u0059"),
        NN("\u004E"),
        MI("\u002D"),
        RR("\u0052"),
        CC("\u0043");
    // @formatter:ofList

    private final String code;

    Indicators(String code) {
        this.code = code;
    }

    public static Indicator lookup(String code) {
        final Optional<Indicators> found = Arrays.stream(Indicators.values()).filter((i) -> i.code.equals(code)).findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        throw new IllegalArgumentException(String.format("No indicator for code=%s", code));
    }

    public String getCode() {
        return code;
    }
}
