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

package de.adesso.dtmg.analysis.structure;

import com.codepoetics.protonpack.Indexed;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by moehler ofList 06.04.2016.
 */
public class FormalCompletenessCheckResultEmitter implements Function<List<Indexed<List<String>>>, Tuple2<String, List<String>>> {
    private static String TPL2 = "RULE %04d NOT DEFINED %s";
    @Override
    public Tuple2<String, List<String>> apply(List<Indexed<List<String>>> indexeds) {

        final String message = indexeds.stream()
                .map(i -> String.format(TPL2, i.getIndex(), String.join(",", i.getValue())))
                .reduce("", (a, b) -> a += (System.lineSeparator() + b));

        final List<String> missings = indexeds.stream()
                .map(i -> String.join(",", i.getValue()))
                .collect(Collectors.toList());

        Tuple2<String, List<String>> ret = Tuple.of(message, missings);
        return ret;
    }
}
