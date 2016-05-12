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

package de.adesso.tools.functions.chained.second;

import com.google.common.base.Joiner;

/**
 * Created by moehler on 11.05.2016.
 */
public class Root extends Model {

    private final Child10 child10;
    private final Child20 child20;

    public Root(String name, Child10 child10, Child20 child20) {
        super(name);
        this.child10 = child10;
        this.child20 = child20;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private Root(Builder builder) {
        super(builder.name);
        this.child10 = builder.child10;
        this.child20 = builder.child20;
    }

    @Override
    public String toString() {
        String s0 = "Root300[" + getName() + ']';
        String s1 = '\t' + String.valueOf(child10);
        String s2 = '\t' + String.valueOf(child20);
        return Joiner.on('\n').join(s0,s1,s2);
    }

    static class Builder {
        private String name;
        private Child10 child10;
        private Child20 child20;

        private Child10.Builder child10Builder = Child10.newBuilder().withParentBuilder(this);
        private Child20.Builder child20Builder = Child20.newBuilder().withParentBuilder(this);

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public void withChild10(Child10 child10) {
            this.child10 = child10;
        }

        public void withChild20(Child20 child20) {
            this.child20 = child20;
        }

        public Child10.Builder withChild10() {
            return this.child10Builder;
        }

        public Child20.Builder withChild20() {
            return this.child20Builder;
        }

        public Root build() {
            return new Root(this);
        }
    }

}
