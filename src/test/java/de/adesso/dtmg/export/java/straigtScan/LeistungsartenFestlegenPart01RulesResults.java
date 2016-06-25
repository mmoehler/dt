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

import java.util.EnumSet;
import java.util.List;

/**
 * Created by moehler on 16.06.2016.
 */
public enum LeistungsartenFestlegenPart01RulesResults {

    ACTIVITY_ERROR_NO_RESULT(0x30), EXIT_PROCESS(0x20), AUFGABE_LA_FESTGELEGT(0x01), LA_KG_FESTGELEGT(0x01);

    final int priority;

    public static final EnumSet<LeistungsartenFestlegenPart01RulesResults> SUCCESS = EnumSet.of(AUFGABE_LA_FESTGELEGT, LA_KG_FESTGELEGT);

    LeistungsartenFestlegenPart01RulesResults(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }

    public static LeistungsartenFestlegenPart01RulesResults max(List<LeistungsartenFestlegenPart01RulesResults> results) {
        return results.stream()
                .max((a,b) -> a.priority - b.priority)
                .orElseThrow(() -> new IllegalStateException("Missing action results!"));
    }

}
