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

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by moehler on 09.06.2016.
 */
public class DtNode implements Visitable {
    public int conditionIndex;
    public List<List<DtCell>> data;
    public DtNode yes;
    public DtNode no;

    private DtNode(Builder builder) {
        conditionIndex = builder.conditionIndex;
        data = builder.data;
        yes = builder.yes;
        no = builder.no;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull DtNode copy) {
        Builder builder = new Builder();
        builder.conditionIndex = copy.conditionIndex;
        builder.data = copy.data;
        builder.yes = copy.yes;
        builder.no = copy.no;
        return builder;
    }

    public boolean isDontCare() {
        return data.stream().flatMap(l -> l.stream()).allMatch(n -> n.typeOf(DtCellType.I));
    }

    public int getConditionIndex() {
        return conditionIndex;
    }

    @Override
    public String toString() {
        return "DtNode{" +
                ", conditionIndex=" + getConditionIndex() +
                ", yes=" + ((null==yes) ? "<?>" : yes) +
                ", no=" + ((null==no) ? "<?>" : no) +
                '}';
    }

    /**
     * {@code DtNode} builder static inner class.
     */
    public static final class Builder {
        public int conditionIndex;
        private List<List<DtCell>> data;
        private DtNode yes;
        private DtNode no;

        private Builder() {
        }

        /**
         * Sets the {@code data} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code data} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder data(@Nonnull List<List<DtCell>> val) {
            data = val;
            return this;
        }

        @Nonnull
        public Builder conditionIndex(@Nonnull int val) {
            conditionIndex = val;
            return this;
        }

        /**
         * Sets the {@code yes} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code yes} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder yes(@Nonnull DtNode val) {
            yes = val;
            return this;
        }

        /**
         * Sets the {@code no} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code no} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder no(@Nonnull DtNode val) {
            no = val;
            return this;
        }

        /**
         * Returns a {@code DtNode} built from the parameters previously set.
         *
         * @return a {@code DtNode} built with parameters of this {@code DtNode.Builder}
         */
        @Nonnull
        public DtNode build() {
            return new DtNode(this);
        }
    }
}
