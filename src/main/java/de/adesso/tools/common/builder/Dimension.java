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

package de.adesso.tools.common.builder;

import javax.annotation.Nonnull;

/**
 * Created by moehler on 18.05.2016.
 */
public class Dimension {
    int rows, columns;

    private Dimension(Builder builder) {
        rows = builder.rows;
        columns = builder.columns;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Array2D.Builder parent, Callback<Dimension> callback) {
        return new Builder(parent, callback);
    }

    public static class Builder extends AbstractNestable<Array2D.Builder, Dimension>{
        private int rows;
        private int columns;

        public Builder() {
            super(null, null);
        }

        public Builder(Array2D.Builder parent, Callback<Dimension> callback) {
            super(parent, callback);
        }

        @Nonnull
        public Builder rows(int val) {
            rows = val;
            return this;
        }

        @Nonnull
        public Builder columns(int val) {
            columns = val;
            return this;
        }

        @Nonnull
        public Dimension build() {
            return new Dimension(this);
        }
    }
}
