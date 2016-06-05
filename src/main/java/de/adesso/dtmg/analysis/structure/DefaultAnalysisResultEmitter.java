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
import com.google.common.collect.Multimap;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.adesso.dtmg.util.tuple.Tuple3;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by mmoehler ofList 02.04.16.
 */
@Singleton
public class DefaultAnalysisResultEmitter implements AnalysisResultEmitter {
    private BiFunction<List<Indicator>, Integer, Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>>> structuralAnalysisResultEmitter;
    private Function<List<Indexed<List<String>>>, Tuple2<String, List<String>>> formalCompletenessCheckResultEmitter;

    @Override
    public BiFunction<List<Indicator>, Integer, Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>>> emitStructuralAnalysisResult() {
        if (structuralAnalysisResultEmitter == null) {
            structuralAnalysisResultEmitter = new StructuralAnalysisResultEmitter();
        }
        return structuralAnalysisResultEmitter;
    }

    @Override
    public Function<List<Indexed<List<String>>>, Tuple2<String, List<String>>> emitFormalCompletenessCheckResults() {
        if (null == formalCompletenessCheckResultEmitter) {
            formalCompletenessCheckResultEmitter = new FormalCompletenessCheckResultEmitter();
        }
        return formalCompletenessCheckResultEmitter;
    }
}
