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

package de.adesso.dtmg.export.java.treemethod;

import de.adesso.dtmg.export.java.ClassDescription;
import de.adesso.dtmg.export.java.GeneratorConfiguration;
import de.adesso.dtmg.io.DtEntity;

/**
 * Created by mmoehler on 26.08.16.
 */
public class TreeMethodConfiguration extends GeneratorConfiguration {
    private boolean useOptimization;

    private TreeMethodConfiguration(Builder builder) {
        super();
        classDescription = builder.classDescription;
        decisionTable = builder.decisionTable;
        useOptimization = builder.useOptimization;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TreeMethodConfiguration copy) {
        Builder builder = new Builder();
        builder.classDescription = copy.classDescription;
        builder.decisionTable = copy.decisionTable;
        builder.useOptimization = copy.useOptimization;
        return builder;
    }

    public boolean useOptimizedDecomposition() {
        return useOptimization;
    }

    public static final class Builder {
        private ClassDescription classDescription;
        private DtEntity decisionTable;
        private boolean useOptimization;

        private Builder() {
        }

        public Builder classDescription(ClassDescription val) {
            classDescription = val;
            return this;
        }

        public Builder decisionTable(DtEntity val) {
            decisionTable = val;
            return this;
        }

        public Builder useOptimization(boolean val) {
            useOptimization = val;
            return this;
        }

        public TreeMethodConfiguration build() {
            return new TreeMethodConfiguration(this);
        }
    }
}
