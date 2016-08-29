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

package de.adesso.dtmg.export.java.straightscan;

import com.sun.codemodel.JMethod;
import de.adesso.dtmg.export.java.Visitable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mmoehler on 29.08.16.
 */
public class RuleDefinition implements Visitable {
    private final Map<String, JMethod> stubs = new HashMap<>();
    private final List<String> conditionDefns;
    private final List<String> actionDefns;

    private RuleDefinition(Builder builder) {
        actionDefns = builder.actionDefns;
        conditionDefns = builder.conditionDefns;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(RuleDefinition copy) {
        Builder builder = new Builder();
        builder.actionDefns = copy.actionDefns;
        builder.conditionDefns = copy.conditionDefns;
        return builder;
    }

    public List<String> getActionDefns() {
        return actionDefns;
    }

    public List<String> getConditionDefns() {
        return conditionDefns;
    }

    public static final class Builder {
        private List<String> actionDefns;
        private List<String> conditionDefns;

        private Builder() {
        }

        public Builder actionDefns(List<String> val) {
            actionDefns = val;
            return this;
        }

        public Builder conditionDefns(List<String> val) {
            conditionDefns = val;
            return this;
        }

        public RuleDefinition build() {
            return new RuleDefinition(this);
        }
    }
}
