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


import com.google.common.collect.Sets;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by mmoehler on 12.06.16.
 */
public class ClassDescription {
    private final String targetpath;
    private final String packagename;
    private final String classname;
    private final Set<Modifier> modifiers = Sets.newHashSet();
    private final String _extends;
    private final List<String> _implements = new LinkedList<>();

    private ClassDescription(Builder builder) {
        classname = builder.classname;
        targetpath = builder.sorceroot;
        packagename = builder.packagename;
        modifiers.addAll(builder.modifiers);
        _extends = builder._extends;
        _implements.addAll(builder._implements);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(ClassDescription copy) {
        Builder builder = new Builder();
        builder.classname = copy.classname;
        builder.sorceroot = copy.targetpath;
        builder.packagename = copy.packagename;
        builder.modifiers = copy.modifiers;
        builder._extends = copy._extends;
        builder._implements.addAll(copy._implements);

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

    public String getExtends() {
        return _extends;
    }

    public List<String> getImplements() {
        return _implements;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public static final class Builder {
        private String classname = "AbstractRules";
        private String sorceroot = "./src/main/java";
        private String packagename = "de.adesso";
        private Set<Modifier> modifiers = Sets.newHashSet();
        private String _extends;
        private List<String> _implements = new LinkedList<>();

        private Builder() {
        }

        public Builder classname(String val) {
            classname = val;
            return this;
        }

        public Builder _extends(String val) {
            _extends = val;
            return this;
        }

        public Builder _implements(String val) {
            _implements.add(val);
            return this;
        }

        public Builder modifier(Modifier val) {
            modifiers.add(val);
            return this;
        }


        public Builder sourceroot(String val) {
            sorceroot = val;
            return this;
        }

        public Builder packagename(String val) {
            packagename = val;
            return this;
        }

        public ClassDescription build() {
            return new ClassDescription(this);
        }
    }
}
