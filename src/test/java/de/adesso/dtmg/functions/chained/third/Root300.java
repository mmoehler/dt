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

import com.google.common.base.Joiner;
import de.adesso.dtmg.functions.chained.second.Model;

/**
 * Created by moehler on 11.05.2016.
 */
public class Root300 extends Model {

    private final Child310 child310;
    private final Child320 child320;

    public Root300(String name, Child310 child310, Child320 child320) {
        super(name);
        this.child310 = child310;
        this.child320 = child320;
    }

    private Root300(Builder builder) {
        super(builder.name);
        this.child310 = builder.child310;
        this.child320 = builder.child320;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        String s0 = "Root300[" + getName() + ']';
        String s1 = '\t' + String.valueOf(child310);
        String s2 = '\t' + String.valueOf(child320);
        return Joiner.on('\n').join(s0, s1, s2);
    }

    public Child310 getChild310() {
        return child310;
    }

    public Child320 getChild320() {
        return child320;
    }

    static class Builder {
        private String name;
        private Child310 child310;
        private Child320 child320;

        private Child310.Builder child310Builder = Child310.newBuilder(this, this::withChild310);
        private Child320.Builder child320Builder = Child320.newBuilder(this, this::withChild320);

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withChild310(Child310 child310) {
            this.child310 = child310;
            return this;
        }

        public Builder withChild320(Child320 child320) {
            this.child320 = child320;
            return this;
        }

        public Child310.Builder withChild310() {
            return this.child310Builder;
        }

        public Child320.Builder withChild320() {
            return this.child320Builder;
        }

        public Root300 build() {
            return new Root300(this);
        }
    }

}
