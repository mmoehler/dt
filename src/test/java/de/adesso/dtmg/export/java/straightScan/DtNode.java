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

package de.adesso.dtmg.export.java.straightScan;

import com.codepoetics.protonpack.Indexed;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by moehler on 09.06.2016.
 */
public class DtNode implements Visitable {
    final int index;
    final List<Indexed<String>> data;
    DtNode yes;
    DtNode no;

    private DtNode(Builder builder) {
        data = builder.data;
        index = builder.index;
        yes = builder.yes;
        no = builder.no;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull DtNode copy) {
        Builder builder = new Builder();
        builder.data = copy.data;
        builder.index = copy.index;
        builder.yes = copy.yes;
        builder.no = copy.no;
        return builder;
    }

    @Override
    public String toString() {
        return "DtNode{" +
                "index=" + index +
                ", no=" + no +
                ", yes=" + yes +
                '}';
    }

    /**
     * {@code DtNode} builder static inner class.
     */
    public static final class Builder {
        private List<Indexed<String>> data;
        private int index;
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
        public Builder data(@Nonnull List<Indexed<String>> val) {
            data = val;
            return this;
        }

        /**
         * Sets the {@code index} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code index} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder index(int val) {
            index = val;
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
