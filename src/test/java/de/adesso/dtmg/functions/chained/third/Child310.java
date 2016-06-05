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

package de.adesso.dtmg.functions.chained.third;

import de.adesso.dtmg.functions.chained.second.Model;

/**
 * Created by moehler on 11.05.2016.
 */
public class Child310 extends Model {

    public Child310(String name) {
        super(name);
    }

    private Child310(Builder builder) {
        super(builder.name);
    }

    public static Builder newBuilder() {
        return new Builder(null, null);
    }

    public static Builder newBuilder(Root300.Builder parent, Callback<Child310> callback) {
        return new Builder(parent, callback);
    }

    public static class Builder extends AbstractNestable<Root300.Builder, Child310> {
        String name;
        public Builder(Root300.Builder parent, Callback<Child310> callback) {
            super(parent, callback);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Child310 build() {
            return new Child310(this);
        }
    }

}
