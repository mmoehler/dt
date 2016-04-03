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

import com.codepoetics.protonpack.Indexed;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by mmoehler on 02.04.16.
 */
@Singleton
public class DefaultAnalysisResultEmitter implements AnalysisResultEmitter {
    private static String TPL2 = "RULE %04d NOT DEFINED %s";
    private BiFunction<List<Indicator>, Integer, String> structuralAnalysisResultEmitter;
    private Function<List<Indexed<List<String>>>, String>  formalCompletenessCheckResultEmitter;

    @Override
    public BiFunction<List<Indicator>, Integer, String> emitStructuralAnalysisResult() {
        if (structuralAnalysisResultEmitter == null) {
            structuralAnalysisResultEmitter = new StructuralAnalysisResultEmitter();
        }
        return structuralAnalysisResultEmitter;
    }

    @Override
    public Function<List<Indexed<List<String>>>, String> emitFormalCompletenessCheckResults() {
        if (null == formalCompletenessCheckResultEmitter) {
            formalCompletenessCheckResultEmitter = (l) ->
                    l.stream()
                            .map(i -> String.format(TPL2, i.getIndex(), String.join(",", i.getValue())))
                            .reduce("", (a, b) -> a += (System.lineSeparator() + b));
        }
        return formalCompletenessCheckResultEmitter;
    }
}
