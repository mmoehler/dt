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

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JStatement;
import de.adesso.dtmg.export.java.ClassDescription;
import de.adesso.dtmg.io.DtEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by mmoehler on 29.08.16.
 */
public class GeneratorConfiguration {
    private final Map<String,JMethod> stubs = new HashMap<>();
    private final LinkedList<JStatement> stack = new LinkedList<>();
    protected ClassDescription classDescription;
    protected DtEntity decisionTable;

    public GeneratorConfiguration() {
        decisionTable = builder.decisionTable;
        classDescription = builder.classDescription;
    }

    public ClassDescription getClassDescription() {
        return classDescription;
    }

    public DtEntity getDecisionTable() {
        return decisionTable;
    }

    public LinkedList<JStatement> stack() {
        return stack;
    }

    public Map<String, JMethod> stubs() {
        return stubs;
    }

    public JMethod stub(String name) {
        return stubs.get(name);
    }
}
