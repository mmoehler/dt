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
public class ClassDescription {
    private final String targetpath;
    private final String packagename;
    private final String classname;
    private final boolean optimized;

    private ClassDescription(Builder builder) {
        classname = builder.classname;
        targetpath = builder.sourceroot;
        packagename = builder.packagename;
        optimized = builder.optimized;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(ClassDescription copy) {
        Builder builder = new Builder();
        builder.classname = copy.classname;
        builder.sourceroot = copy.targetpath;
        builder.packagename = copy.packagename;
        builder.optimized = copy.optimized;
        return builder;
    }

    public String getClassname() {
        return classname;
    }

    public String getPackagename() {
        return packagename;
    }

    public String getSourceRoot() {
        return targetpath;
    }

    public boolean isOptimized() {
        return optimized;
    }

    @Override
    public String toString() {
        return "ClassDescription{" +
                "classname='" + classname + '\'' +
                ", targetpath='" + targetpath + '\'' +
                ", packagename='" + packagename + '\'' +
                ", optimized=" + optimized +
                '}';
    }

    public static final class Builder {
        private String classname = "AbstractRules";
        private String sourceroot = "./src/main/java";
        private String packagename = "de.adesso";
        private boolean optimized = true;

        private Builder() {
        }

        public Builder classname(String val) {
            classname = val;
            return this;
        }

        public Builder sourceroot(String val) {
            sourceroot = val;
            return this;
        }

        public Builder packagename(String val) {
            packagename = val;
            return this;
        }

        public Builder optimized(boolean val) {
            optimized = val;
            return this;
        }

        public ClassDescription build() {
            return new ClassDescription(this);
        }
    }
}
