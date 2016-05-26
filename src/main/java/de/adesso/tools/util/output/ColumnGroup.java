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

package de.adesso.tools.util.output;

import de.adesso.tools.common.builder.AbstractNestable;
import de.adesso.tools.common.builder.Callback;

/**
 * Created by mmoehler on 26.05.16.
 */
public class ColumnGroup {
    final int firstColumn;
    int lastColumn = Integer.MIN_VALUE;
    final int groupWidth;

    private ColumnGroup(Builder builder) {
        firstColumn = builder.firstColumn;
        lastColumn = builder.lastColumn;
        groupWidth = builder.groupWidth;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getFirstColumn() {
        return firstColumn;
    }

    public int getGroupWidth() {
        return groupWidth;
    }

    public int getLastColumn() {
        return lastColumn;
    }

    public static Builder newBuilder(ColumnGroup copy) {
        Builder builder = new Builder();
        builder.firstColumn = copy.firstColumn;
        builder.lastColumn = copy.lastColumn;
        builder.groupWidth = copy.groupWidth;
        return builder;
    }

    static Builder newBuilder(TableFormat.Builder parentBuilder, Callback<ColumnGroup> ownerCallback) {
        Builder builder = new Builder(parentBuilder, ownerCallback);
        return builder;
    }

    public static final class Builder extends AbstractNestable<TableFormat.Builder, ColumnGroup> {
        private int firstColumn;
        private int lastColumn;
        private int groupWidth;

        protected Builder() {
        }

        public Builder(TableFormat.Builder parentBuilder, Callback<ColumnGroup> ownerCallback) {
            super(parentBuilder, ownerCallback);
        }

        public Builder withFirstColumn(int val) {
            firstColumn = val;
            return this;
        }

        public Builder withLastColumn(int val) {
            lastColumn = val;
            return this;
        }

        public Builder withGroupWidth(int val) {
            groupWidth = val;
            return this;
        }

        public ColumnGroup build() {
            return new ColumnGroup(this);
        }
    }
}
