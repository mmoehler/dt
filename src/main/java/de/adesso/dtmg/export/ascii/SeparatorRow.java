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

package de.adesso.dtmg.export.ascii;

import com.google.common.base.Strings;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by mmoehler on 29.05.16.
 */
public class SeparatorRow implements Supplier<String> {

    private static final String EMPTY_STRING = "";
    private static final char CHR_SPACE = ' ';

    private String title;
    private Character padChar;
    private Integer minLength;

    private SeparatorRow(Builder builder) {
        title = builder.title.orElse(EMPTY_STRING);
        minLength = builder.minLength.orElseThrow(() -> new IllegalArgumentException("Missing 'minLength'!!"));
        padChar = builder.padChar.orElse(CHR_SPACE);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(SeparatorRow copy) {
        Builder builder = new Builder();
        builder.minLength = Optional.ofNullable(copy.minLength);
        builder.title = Optional.ofNullable(copy.title);
        builder.padChar = Optional.ofNullable(copy.padChar);
        return builder;
    }

    @Override
    public String get() {
        return Strings.padEnd(title, minLength, padChar);
    }

    public static final class Builder {
        private Optional<Integer> minLength = Optional.empty();
        private Optional<String> title = Optional.empty();
        private Optional<Character> padChar = Optional.empty();

        private Builder() {
        }

        public Builder minLength(Integer val) {
            minLength = Optional.ofNullable(val);
            return this;
        }

        public Builder title(String val) {
            title = Optional.ofNullable(val);
            return this;
        }

        public Builder padChar(char val) {
            padChar = Optional.ofNullable(val);
            return this;
        }

        public SeparatorRow build() {
            return new SeparatorRow(this);
        }
    }
}
