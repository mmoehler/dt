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

package de.adesso.dtmg.export.java.treemethod;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by moehler on 25.08.2016.
 */
public class DtCell implements Comparable<DtCell> {
    DtCellType type;
    int row;
    int col;

    private DtCell(Builder builder) {
        col = builder.col;
        type = builder.type;
        row = builder.row;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull DtCell copy) {
        Builder builder = new Builder();
        builder.col = copy.col;
        builder.type = copy.type;
        builder.row = copy.row;
        return builder;
    }

    public boolean typeOf(DtCellType t) {
        return t == type;
    }

    @Override
    public int compareTo(DtCell o) {
        return this.type.weight - o.type.weight;
    }

    public int col() {
        return col;
    }

    public int row() {
        return row;
    }

    public DtCellType type() {
        return type;
    }

    @Override
    public String toString() {
        return type.code+String.format("[%d:%d]",row,col);
    }

    public List<DtCell> canonical() {
        return (typeOf(DtCellType.I))
                ? Lists.newArrayList(newBuilder(this).type(DtCellType.Y).build(), newBuilder(this).type(DtCellType.N).build())
                : Lists.newArrayList(newBuilder(this).build());
    }


    /**
     * {@code DtCell} builder static inner class.
     */
    public static final class Builder {
        private int col;
        private DtCellType type;
        private int row;

        private Builder() {
        }

        /**
         * Sets the {@code col} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code col} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder col(int val) {
            col = val;
            return this;
        }

        /**
         * Sets the {@code type} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code type} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder type(@Nonnull DtCellType val) {
            type = val;
            return this;
        }

        /**
         * Sets the {@code row} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code row} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder row(int val) {
            row = val;
            return this;
        }

        /**
         * Returns a {@code DtCell} built from the parameters previously set.
         *
         * @return a {@code DtCell} built with parameters of this {@code DtCell.Builder}
         */
        @Nonnull
        public DtCell build() {
            return new DtCell(this);
        }
    }
}
