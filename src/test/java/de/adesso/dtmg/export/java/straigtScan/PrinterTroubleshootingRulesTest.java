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

package de.adesso.dtmg.export.java.straigtScan;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by moehler on 15.06.2016.
 */


public class PrinterTroubleshootingRulesTest {

    @DataProvider(name = "RuleEvaluationResults")
    public static Object[][] ruleEvaluationResults() {
        return new Object[][]{
                {true, true, true, "A1,A2,A3"},
                {true, true, false, "A3,A4"},
                {true, false, true, "A0,A1,A2"},
                {true, false, false, "A4"},
                {false, true, true, "A2,A3"},
                {false, true, false, "A3"},
                {false, false, true, "A2"},
                {false, false, false, ""}
        };
    }

    @Test(dataProvider = "RuleEvaluationResults")
    public void testRules(boolean c0, boolean c1, boolean c2, String expected) {
        final String actual = new PrinterTroubleshootingRulesImpl(c0, c1, c2).apply(null);
        Assert.assertEquals(actual, expected);
    }
}
