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

package de.adesso.dtmg.functions.chained.second;

/**
 * Created by moehler on 11.05.2016.
 */
public class Child20 extends Model {
//    private final Child21 child21;
//    private final Child22 child22;

    public Child20(String name, Child21 child21, Child22 child22) {
        super(name);
//        this.child21 = child21;
//        this.child22 = child22;
    }

    public Child20(Builder builder) {
        super(builder.name);
//        this.child21 = builder.child21;
//        this.child22 = builder.child22;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends NestedBuilder<Root.Builder, Child20> {
        private String name;
//        private Child21 child21;
//        private Child22 child22;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Child20 build() {
            return new Child20(this);
        }
    }
}
