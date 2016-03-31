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
import de.adesso.tools.Dump;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.functions.MatrixFunctions;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by moehler on 31.03.2016.
 */
public class CollectorTest {

    @Test
    public void comparisonCollectorTest() {

        // -- Condition comparison ---------------------------------

        final List<List<String>> inConditions = MatrixBuilder.on("Y,Y,Y,-,-,N,N,N,-,-,-,N,Y,Y,N,N").dim(4, 4).transposed().build();
        final List<List<Indicator>> outConditions = new ArrayList<>();

        for (int i = 0; i < inConditions.size()-1; i++) {
            for (int j = 1; j < inConditions.size(); j++) {
                if(j>i) {
                    final Stream<Indicator> leftStream = inConditions.get(i).stream().map(a -> Indicators.lookup(a));
                    final Stream<Indicator> rightStream = inConditions.get(j).stream().map(a -> Indicators.lookup(a));

                    final List<Indicator> collected = StreamUtils.zip(leftStream, rightStream, Operators.conditionComparison()).collect(Collectors.toList());
                    outConditions.add(collected);
                }
            }
        }

        Dump.dumpTableItems("collected = ",MatrixFunctions.transpose(outConditions));

        // -- Action comparison ---------------------------------
        
        final List<List<String>> inActions = MatrixBuilder.on("-,-,-,-,X,X,X,-,X,-,-,X,-,X,X,-").dim(4, 4).transposed().build();
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



        Dump.dumpTableItems("collected = ",MatrixFunctions.transpose(outActions));


        // first for the last comparison result do the reduction

        final Optional<Indicator> reduced = outConditions.get(outConditions.size() - 1).stream().reduce(Combiners.conditionComparisonResult());

        if(reduced.isPresent()) {
            System.out.println("reduced.get().getCode() = " + reduced.get().getCode());
        } else {
            System.out.println("reduced.get().getCode() = NULL");
        }

        // now for each result do the reduction

        final List<Indicator> reducedConditionIndicators = outConditions.stream()
                .map(c -> c.stream()
                        .reduce(Combiners.conditionComparisonResult()))
                .map(r -> r.get())
                .collect(Collectors.toList());

        Dump.dumpList1DItems("001", reducedConditionIndicators);

        // the same procedure for the actions

        final List<Indicator> reducedActionIndicators = outActions.stream()
                .map(c -> c.stream()
                        .reduce(Combiners.actionComparisonResult()))
                .map(r -> r.get())
                .collect(Collectors.toList());

        Dump.dumpList1DItems("002", reducedActionIndicators);


        // last zip both resultstogether -> reduce the data to the final per rule result

        final List<Indicator> result = StreamUtils.zip(reducedActionIndicators.stream(), reducedConditionIndicators.stream(), Accumulators.combinationResult()).collect(Collectors.toList());

        Dump.dumpList1DItems("RESULT", result);

        Iterator<Indicator> indics = result.iterator();
        System.out.println("                           1234");
        for (int i = 0; i < 3; i++) {
            System.out.print(String.format("STRUCTURE ANALYSIS RULE %2s ", i+1));
            for (int j = 0; j < 4; j++) {
                if(j>i) {
                    System.out.print(indics.next().getCode());
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }




    }

}
