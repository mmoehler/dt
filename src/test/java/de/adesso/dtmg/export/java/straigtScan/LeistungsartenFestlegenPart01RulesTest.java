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

import static de.adesso.dtmg.export.java.straigtScan.LeistungsartenFestlegenPart01RulesResults.*;

/**
 * Created by moehler on 16.06.2016.
 */
public class LeistungsartenFestlegenPart01RulesTest {
    @DataProvider(name = "RuleEvaluationResults")
    public static Object[][] ruleEvaluationResults() {
        return new Object[][]{
                {true,true,false,false,true, EXIT_PROCESS},
                {true,true,false,false,false, LA_KG_FESTGELEGT},
                {false,false,true,false,true, AUFGABE_LA_FESTGELEGT},
                {false,false,true,false,false, LA_KG_FESTGELEGT},
                {false,false,false,true,false, EXIT_PROCESS}
        };
    }

    @Test(dataProvider = "RuleEvaluationResults")
    public void testRules(boolean c0, boolean c1, boolean c2, boolean c3, boolean c4, LeistungsartenFestlegenPart01RulesResults expected) {
        final LeistungsartenFestlegenPart01RulesResults actual = new LeistungsartenFestlegenPart01RulesImpl(c0, c1, c2, c3, c4).apply(null);
        Assert.assertEquals(actual, expected);
    }
}

