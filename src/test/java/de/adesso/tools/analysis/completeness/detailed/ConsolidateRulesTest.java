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

package de.adesso.tools.analysis.completeness.detailed;

import com.google.common.collect.Lists;
import de.adesso.tools.common.ObservableList2DBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by mmoehler on 08.05.16.
 */
public class ConsolidateRulesTest {

    @Test
    public void testApply() throws Exception {

    }

    @Test
    public void testDetermineCountOfDuplicateRules() throws Exception {

    }

    @Test
    public void testDetermineIndicesOfDashedIndicators() throws Exception {
        ObservableList2DBuilder input = ObservableList2DBuilder.observable2DOf("Y,-,N,N,Y,N,N,-").dim(4, 2);
        List<Integer> expected = Lists.newArrayList(1);
        List<Integer> actual = ConsolidateRules.determineIndicesOfDashedIndicators(input.build(),3);
        Assert.assertEquals(actual,expected);

    }

    @Test(priority = 1)
    public void testRowsWithAllPossibleIndicators() throws Exception {
        ObservableList2DBuilder input = ObservableList2DBuilder.observable2DOf("Y,Y,N,N,Y,N,N,N").dim(4, 2);
        List<Boolean> expected = Lists.newArrayList(false, false, true, false);
        List<Boolean> actual = ConsolidateRules.rowsWithAllPossibleIndicators(input.build());
        Assert.assertEquals(actual,expected);
    }

    @Test
    public void testUpdateRowsWithAllPossibleIndicators() throws Exception {

    }
}