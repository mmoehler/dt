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

package de.adesso.dtmg.util.output;

import de.adesso.dtmg.common.builder.AbstractNestable;
import de.adesso.dtmg.common.builder.Callback;

/**
 * Created by mmoehler on 26.05.16.
 */
public class ColumnFormat {
    private final int width;
    private final Align align;

    public ColumnFormat(Align align, int width) {
        this.align = align;
        this.width = width;
    }

    private ColumnFormat(Builder builder) {
        align = builder.align;
        width = builder.width;
    }

    public Align getAlign() {
        return align;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return "ColumnFormat{" +
                "align=" + align +
                ", width=" + width +
                '}';
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(ColumnFormat copy) {
        Builder builder = new Builder();
        builder.align = copy.align;
        builder.width = copy.width;
        return builder;
    }

    public static Builder newBuilder(TableFormat.Builder parentBuilder, Callback<ColumnFormat> ownerCallback) {
        return new Builder(parentBuilder, ownerCallback);
    }

    public static final class Builder extends AbstractNestable<TableFormat.Builder,ColumnFormat>{
        private Align align;
        private int width;

        private Builder() {
        }

        public Builder(TableFormat.Builder parentBuilder, Callback<ColumnFormat> ownerCallback) {
            super(parentBuilder, ownerCallback);
        }

        public Builder align(Align val) {
            align = val;
            return this;
        }

        public Builder width(int val) {
            width = val;
            return this;
        }

        public ColumnFormat build() {
            return new ColumnFormat(this);
        }
    }
}
