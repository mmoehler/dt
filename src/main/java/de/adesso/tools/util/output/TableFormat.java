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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by mmoehler on 26.05.16.
 */
public class TableFormat {
    private ImmutableList<ColumnFormat> columnFormats;
    private ImmutableList<ColumnGroup> columnGroups;
    public ColumnSeparator columnSeparator = ColumnSeparator.DEFAULT;

    private TableFormat(Builder builder) {
        columnFormats = ImmutableList.<ColumnFormat>builder()
                .addAll(builder.columnFormats)
                .build();
        columnGroups = ImmutableList.<ColumnGroup>builder()
                .addAll(builder.columnGroups)
                .build();
    }

    public ImmutableList<ColumnFormat> getColumnFormats() {
        return columnFormats;
    }

    public ImmutableList<ColumnGroup> getColumnGroups() {
        return columnGroups;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TableFormat copy) {
        Builder builder = new Builder();
        builder.columnFormats = copy.columnFormats;
        builder.columnGroups = copy.columnGroups;
        return builder;
    }

    public static final class Builder {
        private List<ColumnFormat> columnFormats = Lists.newLinkedList();
        private List<ColumnGroup> columnGroups = Lists.newLinkedList();
        private ColumnSeparator columnSeparator = ColumnSeparator.DEFAULT;
        private ColumnGroup.Builder columnGroupBuilder = ColumnGroup.newBuilder(Builder.this, Builder.this::addColumnGroup);
        private ColumnFormat.Builder columnFormatBuilder = ColumnFormat.newBuilder(Builder.this, Builder.this::addColumnFormat);
        private ColumnSeparator.Builder columnSeparatorBuilder = ColumnSeparator.newBuilder(Builder.this, Builder.this::columnSeparator);

        private Builder() {
        }

        public ColumnFormat.Builder addColumnFormat() {
            return columnFormatBuilder;
        }

        public ColumnGroup.Builder addColumnGroup() {
            return this.columnGroupBuilder;
        }

        public ColumnGroup.Builder columnSeparatorBuilder() {
            return this.columnSeparatorBuilder();
        }


        public Builder addColumnFormat(ColumnFormat val) {
            columnFormats.add(val);
            return this;
        }

        public Builder addColumnGroup(ColumnGroup val) {
            columnGroups.add(val);
            return this;
        }

        public Builder columnFormats(List<ColumnFormat> val) {
            columnFormats = val;
            return this;
        }

        public Builder columnGroups(List<ColumnGroup> val) {
            columnGroups = val;
            return this;
        }

        public Builder columnSeparator(ColumnSeparator val) {
            columnSeparator = val;
            return this;
        }


        public TableFormat build() {
            return new TableFormat(this);
        }
    }
}
