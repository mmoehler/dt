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

package de.adesso.dtmg.export.java;

/**
 * Created by mmoehler on 12.06.16.
 */
public class ConcreteRules extends AbstractRules<String,String> {
    @Override
    protected String actions00(String input) {
        return null;
    }

    @Override
    protected boolean condition00(String input) {
        return false;
    }

    @Override
    protected boolean condition01(String input) {
        return false;
    }

    @Override
    protected boolean condition02(String input) {
        return false;
    }

    @Override
    protected String actions01(String input) {
        return null;
    }

    @Override
    protected String actions02(String input) {
        return null;
    }

    @Override
    protected String actions03(String input) {
        return null;
    }

    @Override
    protected String actions04(String input) {
        return null;
    }
}
