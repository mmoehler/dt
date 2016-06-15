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

import com.google.common.base.Joiner;

import java.util.List;

/**
 * Created by moehler on 15.06.2016.
 */
public class PrinterTroubleshootingRulesImpl extends PrinterTroubleshootingRules<Void,String> {
    private final boolean c0,c1,c2;

    public PrinterTroubleshootingRulesImpl(boolean c0, boolean c1, boolean c2) {
        this.c0 = c0;
        this.c1 = c1;
        this.c2 = c2;
    }


    @Override
    protected boolean condition00(Void input) {
        return c0;
    }

    @Override
    protected boolean condition01(Void input) {
        return c1;
    }

    @Override
    protected boolean condition02(Void input) {
        return c2;
    }

    @Override
    protected String actions00(Void input) {
        return "A0";
    }

    @Override
    protected String actions01(Void input) {
        return "A1";
    }

    @Override
    protected String actions02(Void input) {
        return "A2";
    }

    @Override
    protected String actions03(Void input) {
        return "A3";
    }

    @Override
    protected String actions04(Void input) {
        return "A4";
    }

    @Override
    protected String reduceResults(List<String> results) {
        return Joiner.on(',').skipNulls().join(results);
    }

    public static void main(String[] args) {
        final String result = new PrinterTroubleshootingRulesImpl(true, false, true).apply(null);
        System.out.println("result = \n" + result);
    }


}
