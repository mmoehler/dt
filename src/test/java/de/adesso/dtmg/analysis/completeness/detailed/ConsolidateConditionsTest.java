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

package de.adesso.dtmg.analysis.completeness.detailed;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.adesso.dtmg.util.ObservableList2DBuilder;
import javafx.collections.ObservableList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.adesso.dtmg.analysis.completeness.detailed.ConsolidateConditions.consolidateConditions;

/**
 * Created by mmoehler on 08.05.16.
 */
public class ConsolidateConditionsTest {

    @Test
    public void testApply() throws Exception {
        ObservableList2DBuilder input = ObservableList2DBuilder.observable2DOf("Y,Y,N,N,N,Y").dim(2, 3);
        final ObservableList<ObservableList<String>> actual = consolidateConditions().apply(input.build());
        System.out.println("actual = " + actual);
    }

    @Test
    public void testDetermineCountOfDuplicateRules() throws Exception {
        ObservableList2DBuilder input = ObservableList2DBuilder.observable2DOf("Y,-,N,N,Y,-,N,N").dim(4, 2);
        Map<List<String>,List<Integer>> expected = Maps.newHashMap();
        expected.put(Lists.newArrayList("Y","-"), Lists.newArrayList(0,2));
        expected.put(Lists.newArrayList("N","N"), Lists.newArrayList(1,3));
        final LinkedListMultimap<List<String>, Integer> actual = ConsolidateConditions.determineCountOfDuplicateRules(input.build());
        Assert.assertEquals(actual.asMap(),expected);
    }

    @Test
    public void testDetermineIndicesOfDashedIndicators() throws Exception {
        ObservableList2DBuilder input = ObservableList2DBuilder.observable2DOf("Y,-,N,N,Y,N,N,-").dim(4, 2);
        List<Integer> expected = Lists.newArrayList(1);
        List<Integer> actual = ConsolidateConditions.determineIndicesOfDashedIndicators(input.build(),3);
        Assert.assertEquals(actual,expected);

    }

    @Test(priority = 1)
    public void testRowsWithAllPossibleIndicators() throws Exception {
        ObservableList2DBuilder input = ObservableList2DBuilder.observable2DOf("Y,Y,N,N,Y,N,N,N").dim(4, 2);
        List<Boolean> expected = Lists.newArrayList(false, false, true, false);
        List<Boolean> actual = ConsolidateConditions.rowsWithAllPossibleIndicators(input.build());
        Assert.assertEquals(actual,expected);
    }

    @Test
    public void testUpdateRowsWithAllPossibleIndicators() throws Exception {

    }

    @Test
    public void testConsolidateRules() throws Exception {

    }

    @Test
    public void testCleanupConditions() throws Exception {
        ObservableList2DBuilder input = ObservableList2DBuilder.observable2DOf("Y,Y,N,N,Y,N,Y,N").dim(2, 4);
        final ObservableList<ObservableList<String>> actual =
                consolidateConditions().cleanupConditions(input.build(),0, Collections.emptyList());
        System.out.println("actual = " + actual);

    }
}