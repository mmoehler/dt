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

package de.adesso.dtmg.util;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by moehler on 08.06.2016.
 */
public class RandomDefinitions implements Supplier<List<List<String>>> {

    private static String INDICATORS[] = {"Y", "N", "-"};

    int rows;
    int cols;
    boolean transpose;
    Supplier<String[]> indicators;

    private RandomDefinitions(Builder builder) {
        cols = builder.cols;
        rows = builder.rows;
        indicators = builder.indicators;
        transpose = builder.transpose;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull RandomDefinitions copy) {
        Builder builder = new Builder();
        builder.transpose = copy.transpose;
        builder.cols = copy.cols;
        builder.rows = copy.rows;
        builder.indicators = copy.indicators;
        return builder;
    }

    @Override
    public List<List<String>> get() {
        final List<String> stringList = IntStream.range(0, rows * cols)
                .mapToObj(i -> INDICATORS[ThreadLocalRandom.current().nextInt(0, this.indicators.get().length)])
                .collect(Collectors.toList());

        List2DBuilder builder = List2DBuilder.matrixOf(stringList).dim(rows, cols);
        builder = (transpose) ? (builder.transposed()) : (builder);
        List<List<String>> data = builder.build();

        return data;
    }

    /**
     * {@code RandomDefinitions} builder static inner class.
     */
    public static final class Builder {
        private boolean transpose = false;
        private int cols;
        private int rows;
        private Supplier<String[]> indicators;

        private Builder() {
        }

        /**
         * Sets the {@code transpose} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder transpose() {
            transpose = true;
            return this;
        }

        /**
         * Sets the {@code cols} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code cols} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder cols(int val) {
            cols = val;
            return this;
        }

        /**
         * Sets the {@code rows} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code rows} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder rows(int val) {
            rows = val;
            return this;
        }

        /**
         * Sets the {@code indicators} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code indicators} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder indicators(@Nonnull Supplier<String[]> val) {
            indicators = val;
            return this;
        }

        /**
         * Returns a {@code RandomDefinitions} built from the parameters previously set.
         *
         * @return a {@code RandomDefinitions} built with parameters of this {@code RandomDefinitions.Builder}
         */
        @Nonnull
        public RandomDefinitions build() {
            return new RandomDefinitions(this);
        }
    }
}
