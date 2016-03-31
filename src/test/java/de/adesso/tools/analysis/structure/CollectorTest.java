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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by moehler on 31.03.2016.
 */
public class CollectorTest {

    @Test
    public void comparisonCollectorTest() {
        final List<List<String>> input = MatrixBuilder.on("Y,Y,Y,-,-,N,N,N,-,-,-,N,Y,Y,N,N").dim(4, 4).transposed().build();
        final List<List<Indicator>> output = new ArrayList<>();
        for (int i = 0; i < input.size()-1; i++) {
            for (int j = 1; j < input.size(); j++) {
                if(j>i) {
                    final Stream<Indicator> leftStream = input.get(i).stream().map(a -> ConditionIndicator.lookup(a));
                    final Stream<Indicator> rightStream = input.get(j).stream().map(a -> ConditionIndicator.lookup(a));

                    final List<Indicator> collected = StreamUtils.zip(leftStream, rightStream, (l, r) -> l.apply(r)).collect(Collectors.toList());
                    output.add(collected);
                }
            }
        }

        Dump.dumpTableItems("collected = ",MatrixFunctions.transpose(output));

    }

}
