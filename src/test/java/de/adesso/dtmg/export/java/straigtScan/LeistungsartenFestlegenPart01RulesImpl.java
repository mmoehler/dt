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

import java.util.List;

/**
 * Created by moehler on 16.06.2016.
 */
public class LeistungsartenFestlegenPart01RulesImpl
        extends LeistungsartenFestlegenPart01Rules<Object
        ,LeistungsartenFestlegenPart01RulesResults> {

    public final boolean c0,c1,c2,c3,c4;

    public LeistungsartenFestlegenPart01RulesImpl(boolean c0, boolean c1, boolean c2, boolean c3, boolean c4) {
        this.c0 = c0;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
    }

    @Override
    protected LeistungsartenFestlegenPart01RulesResults actions00(Object input) {
        return LeistungsartenFestlegenPart01RulesResults.AUFGABE_LA_FESTGELEGT;
    }

    @Override
    protected LeistungsartenFestlegenPart01RulesResults actions01(Object input) {
        return LeistungsartenFestlegenPart01RulesResults.LA_KG_FESTGELEGT;
    }

    @Override
    protected LeistungsartenFestlegenPart01RulesResults actions02(Object input) {
        return LeistungsartenFestlegenPart01RulesResults.EXIT_PROCESS;
    }

    @Override
    protected boolean condition00(Object input) {
        return c0;
    }

    @Override
    protected boolean condition01(Object input) {
        return c1;
    }

    @Override
    protected boolean condition02(Object input) {
        return c2;
    }

    @Override
    protected boolean condition03(Object input) {
        return c3;
    }

    @Override
    protected boolean condition04(Object input) {
        return c4;
    }

    @Override
    protected LeistungsartenFestlegenPart01RulesResults reduceResults(List<LeistungsartenFestlegenPart01RulesResults> results) {
        return (null != results && !results.isEmpty()) ? LeistungsartenFestlegenPart01RulesResults.max(results) : LeistungsartenFestlegenPart01RulesResults.ACTIVITY_ERROR_NO_RESULT;
    }

    public static void main(String[] args) {
        LeistungsartenFestlegenPart01RulesResults results = new LeistungsartenFestlegenPart01RulesImpl(true,true,false,false,true).apply(null);
        System.out.println("results = " + results);
/*
        results = new LeistungsartenFestlegenPart01RulesImpl("00010").apply(null);
        System.out.println("results = " + results);
*/
    }


}
