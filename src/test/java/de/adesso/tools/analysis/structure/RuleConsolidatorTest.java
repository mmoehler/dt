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

import com.google.common.collect.Lists;
import de.adesso.tools.Dump;
import de.adesso.tools.common.ObservableList2DBuilder;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by mmoehler on 13.05.16.
 */
public class RuleConsolidatorTest {

    @Test
    public void testMergeRules() throws Exception {
        ObservableList<ObservableList<String>> actions = ObservableList2DBuilder.observable2DOf(
                "X,-,X,-,X," +
                        "-,-,-,-,X," +
                        "X,X,X,X,X," +
                        "X,-,X,-,X").dim(4, 5).build();

        ObservableList<ObservableList<String>> conditions = ObservableList2DBuilder.observable2DOf(
                "Y,Y,Y,Y,Y," +
                        "Y,Y,Y,Y,N," +
                        "Y,Y,N,N,Y," +
                        "Y,N,Y,N,Y").dim(4, 5).build();

        List<List<Integer>> indices = Lists.newArrayList(
                Lists.newArrayList(0,2),
                Lists.newArrayList(1,3)
        );

        ObservableList<ObservableList<String>> part0 = ObservableList2DBuilder.observable2DOf(
                "Y," +
                        "Y," +
                        "-," +
                        "Y").dim(4, 1).build();

        ObservableList<ObservableList<String>> part1 = ObservableList2DBuilder.observable2DOf(
                "Y," +
                        "Y," +
                        "-," +
                        "N").dim(4, 1).build();

        ObservableList<ObservableList<ObservableList<String>>> consolidated = FXCollections.observableArrayList();
        consolidated.add(part0);
        consolidated.add(part1);

        Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> actual = new RuleConsolidator().mergeRules(conditions, actions, indices, consolidated);

        Dump.dumpTableItems("C",actual._1());
        Dump.dumpTableItems("A",actual._2());

        ObservableList<ObservableList<String>> expActions = ObservableList2DBuilder.observable2DOf(
                        "X,-,X," +
                        "-,-,X," +
                        "X,X,X," +
                        "X,-,X").dim(4, 3).build();

        ObservableList<ObservableList<String>> expConditions = ObservableList2DBuilder.observable2DOf(
                        "Y,Y,Y," +
                        "Y,Y,N," +
                        "-,-,Y," +
                        "Y,N,Y").dim(4, 3).build();

        assertThat("Condition Consolidation fails!",actual._1(), equalTo(expConditions));
        assertThat("Action Consolidation fails!",actual._2(), equalTo(expActions));
    }

    @Test
    public void testConsolidateConditions() throws Exception {
        ObservableList<ObservableList<String>> part0 = ObservableList2DBuilder.observable2DOf(
                "Y,Y," +
                        "Y,Y," +
                        "Y,N," +
                        "Y,Y").dim(4, 2).transposed().build();

        ObservableList<ObservableList<String>> part1 = ObservableList2DBuilder.observable2DOf(
                "Y,Y," +
                        "Y,Y," +
                        "Y,N," +
                        "N,N").dim(4, 2).transposed().build();



        ObservableList<ObservableList<ObservableList<String>>> parts = FXCollections.observableArrayList();
        parts.add(part0);
        parts.add(part1);


        ObservableList<ObservableList<String>> exp0 = ObservableList2DBuilder.observable2DOf(
                "Y," +
                        "Y," +
                        "-," +
                        "Y").dim(4, 1).build();

        ObservableList<ObservableList<String>> exp1 = ObservableList2DBuilder.observable2DOf(
                "Y," +
                        "Y," +
                        "-," +
                        "N").dim(4, 1).build();

        ObservableList<ObservableList<ObservableList<String>>> expected = FXCollections.observableArrayList();
        expected.add(exp0);
        expected.add(exp1);


        ObservableList<ObservableList<ObservableList<String>>> actual = new RuleConsolidator().consolidateConditions(parts);

        assertThat(actual, equalTo(expected));


    }

    @Test
    public void testDetermineConditions4Consolidation() throws Exception {
        ObservableList<ObservableList<String>> input1 = ObservableList2DBuilder.observable2DOf(
                        "Y,Y,Y,Y,Y," +
                        "Y,Y,Y,Y,N," +
                        "Y,Y,N,N,Y," +
                        "Y,N,Y,N,Y").dim(4, 5).transposed().build();

        List<List<Integer>> input2 = Lists.newArrayList(
                Lists.newArrayList(0,2),
                Lists.newArrayList(1,3)
        );

        ObservableList<ObservableList<String>> tmp0 = ObservableList2DBuilder.observable2DOf(
                "Y,Y," +
                        "Y,Y," +
                        "Y,N," +
                        "Y,Y").dim(4, 2).transposed().build();

        ObservableList<ObservableList<String>> tmp1 = ObservableList2DBuilder.observable2DOf(
                "Y,Y," +
                        "Y,Y," +
                        "Y,N," +
                        "N,N").dim(4, 2).transposed().build();



        ObservableList<ObservableList<ObservableList<String>>> expected = FXCollections.observableArrayList();
        expected.add(tmp0);
        expected.add(tmp1);

        ObservableList<ObservableList<ObservableList<String>>> actual = new RuleConsolidator().determineConditions4Consolidation(input1, input2);

        assertThat(actual, equalTo(expected));

    }

    @Test
    public void testDetectIndicesOfDupplicateActionCombinations() throws Exception {
        ObservableList<ObservableList<String>> input = ObservableList2DBuilder.observable2DOf(
                                "X,-,X,-,X," +
                                "-,-,-,-,X," +
                                "X,X,X,X,X," +
                                "X,-,X,-,X").dim(4, 5).transposed().build();
        List<List<Integer>> actuals = new RuleConsolidator().detectIndicesOfDupplicateActionCombinations(input);

        List<List<Integer>> expected = Lists.newArrayList(
                Lists.newArrayList(0,2),
                Lists.newArrayList(1,3)
        );

        assertThat(actuals, equalTo(expected));
    }

    @Test
    public void testTransposeConditions() throws Exception {
        ObservableList<ObservableList<String>> input = ObservableList2DBuilder.observable2DOf("Y,N,Y,N,Y,N,Y,N,Y,N,Y,N,Y,N,Y,N").dim(4, 4).build();
        ObservableList<ObservableList<String>> expected = ObservableList2DBuilder.observable2DOf("Y,Y,Y,Y,N,N,N,N,Y,Y,Y,Y,N,N,N,N").dim(4, 4).build();
        ObservableList<ObservableList<String>> actuals = new RuleConsolidator().transposeActions(input);
        assertThat(actuals, equalTo(expected));

    }

    @Test
    public void testTransposeActions() throws Exception {
        ObservableList<ObservableList<String>> input = ObservableList2DBuilder.observable2DOf("X,-,X,-,X,-,X,-,X,-,X,-,X,-,X,-").dim(4, 4).build();
        ObservableList<ObservableList<String>> expected = ObservableList2DBuilder.observable2DOf("X,X,X,X,-,-,-,-,X,X,X,X,-,-,-,-").dim(4, 4).build();

        ObservableList<ObservableList<String>> actuals = new RuleConsolidator().transposeActions(input);

        assertThat(actuals, equalTo(expected));
    }

    @Test
    public void testIndicesOfDuplicateActions() throws Exception {

    }
}