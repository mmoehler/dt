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

import com.codepoetics.protonpack.StreamUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by moehler on 31.03.2016.
 */
public class StructuralAnalysis implements BiFunction<List<List<String>>, List<List<String>>, List<Indicator>> {

    final static Function<List<List<String>>, List<Indicator>> conditionProcessor = (inConditions) -> {
        final List<List<Indicator>> outConditions = new ArrayList<>();

        for (int i = 0; i < inConditions.size()-1; i++) {
            for (int j = 1; j < inConditions.size(); j++) {
                if(j>i) {
                    final Stream<Indicator> leftStream = inConditions.get(i).stream().map(a -> Indicators.lookup(a));
                    final Stream<Indicator> rightStream = inConditions.get(j).stream().map(a -> Indicators.lookup(a));
                    final List<Indicator> collected = StreamUtils
                            .zip(leftStream, rightStream, Operators.conditionComparison())
                            .collect(Collectors.toList());
                    outConditions.add(collected);
                }
            }
        }
        final List<Indicator> reducedConditionIndicators = outConditions.stream()
                .map(c -> c.stream()
                        .reduce(Combiners.conditionComparisonResult()))
                .map(r -> r.get())
                .collect(Collectors.toList());

        return reducedConditionIndicators;
    };

    final static Function<List<List<String>>, List<Indicator>> actionProcessor = (inActions) -> {
        final List<List<Indicator>> outActions = new ArrayList<>();
        for (int i = 0; i < inActions.size()-1; i++) {
            for (int j = 1; j < inActions.size(); j++) {
                if(j>i) {
                    final Stream<Indicator> leftStream = inActions.get(i).stream().map(Indicators::lookup);
                    final Stream<Indicator> rightStream = inActions.get(j).stream().map(Indicators::lookup);
                    final List<Indicator> collected = StreamUtils.zip(leftStream, rightStream, Operators.actionComparison() ).collect(Collectors.toList());
                    outActions.add(collected);
                }
            }
        }
        final List<Indicator> reducedActionIndicators = outActions.stream()
                .map(c -> c.stream()
                        .reduce(Combiners.actionComparisonResult()))
                .map(r -> r.get())
                .collect(Collectors.toList());

        return reducedActionIndicators;
    };

    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    @Override
    public List<Indicator> apply(List<List<String>> conditions, List<List<String>> actions) {

        List<Indicator> conditionResults = Collections.emptyList();
        List<Indicator> actionResults = Collections.emptyList();
        try {
            conditionResults = pool.submit(() -> conditionProcessor.apply(conditions)).get(1, SECONDS);
            actionResults = pool.submit(() -> actionProcessor.apply(actions)).get(1, SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            pool.shutdownNow();
        }

        final List<Indicator> result = StreamUtils
                .zip(actionResults.stream(), conditionResults.stream(), Accumulators.combinationResult())
                .collect(Collectors.toList());

        return result;
    }
}
