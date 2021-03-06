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
public class Child10 extends Model {

    //private final Child11 child11;
    //private final Child12 child12;

    public Child10(String name, Child11 child11, Child12 child12) {
        super(name);
        //this.child11 = child11;
        //this.child12 = child12;
    }

    public Child10(Builder builder) {
        super(builder.name);
        //this.child11 = builder.child11;
        //this.child12 = builder.child12;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends NestedBuilder<Root.Builder, Child10> {
        private String name;
//        private Child11 child11;
//        private Child12 child12;


        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Child10 build() {
            return new Child10(this);
        }
    }

}
