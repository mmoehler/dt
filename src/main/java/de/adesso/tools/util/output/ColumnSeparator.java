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

import com.google.common.base.Strings;
import de.adesso.tools.common.builder.AbstractNestable;
import de.adesso.tools.common.builder.Callback;

import java.util.function.Supplier;

/**
 * Created by mmoehler on 27.05.16.
 */
public class ColumnSeparator implements Supplier<String> {

    public static ColumnSeparator DEFAULT = ColumnSeparator.newBuilder().character(' ').length(1).build();

    private final int length;
    private final char character;
    private String value;

    private ColumnSeparator(Builder builder) {
        character = builder.character;
        length = builder.length;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public char getCharacter() {
        return character;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String get() {
        if(value == null) {
            value = Strings.repeat(String.valueOf(character),length);
        }
        return value;
    }

    @Override
    public String toString() {
        return "ColumnSeparator{" +
                "character=" + character +
                ", length=" + length +
                '}';
    }

    public static Builder newBuilder(TableFormat.Builder parentBuilder, Callback<ColumnSeparator> ownerCallback) {
        return new Builder(parentBuilder, ownerCallback);
    }

    public static Builder newBuilder(ColumnSeparator copy) {
        Builder builder = new Builder();
        builder.character = copy.character;
        builder.length = copy.length;
        return builder;
    }


    public static final class Builder extends AbstractNestable<TableFormat.Builder,ColumnSeparator> {
        private char character;
        private int length;

        private Builder() {
        }

        public Builder(TableFormat.Builder parentBuilder, Callback<ColumnSeparator> ownerCallback) {
            super(parentBuilder, ownerCallback);
        }

        public Builder character(char val) {
            character = val;
            return this;
        }

        public Builder length(int val) {
            length = val;
            return this;
        }

        public ColumnSeparator build() {
            return new ColumnSeparator(this);
        }
    }
}
