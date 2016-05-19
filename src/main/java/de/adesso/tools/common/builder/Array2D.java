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
import java.util.Arrays;

/**
 * Created by moehler on 18.05.2016.
 */
public class Array2D {
    private final String[][] data;
    private final Dimension dimension;

    private Array2D(Builder builder) {
        dimension = builder.dimension;
        data = new String[dimension.rows][dimension.columns];
        for (int i = 0; i < data.length; i++) {
            Arrays.fill(data[i], builder.defaultValue);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void set(int row, int col, String value) {
        data[row][col] = value;
    }

    public String get(int row, int col) {
        return data[row][col];
    }

    public String[][] intern() {
        return data;
    }

    /**
     * {@code Array2D} builder static inner class.
     */
    public static final class Builder {
        private Dimension dimension;
        private String defaultValue;
        private Dimension.Builder dimensionBuilder = Dimension.newBuilder(Builder.this, (o) -> this.dimension(o));

        public Builder() {
        }

        @Nonnull
        public Builder fillWith(@Nonnull String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Nonnull
        public Builder dimension(@Nonnull Dimension val) {
            dimension = val;
            return this;
        }

        @Nonnull
        public Dimension.Builder dimension() {
            return dimensionBuilder;
        }

        @Nonnull
        public Array2D build() {
            return new Array2D(this);
        }
    }
}
