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

package de.adesso.tools.model;

import javax.annotation.Nonnull;

/**
 * Raw declaration of the condition of a rule
 * Created by mohler on 15.01.16.
 */
public class ConditionDecl extends Declaration {

    private ConditionDecl(Builder builder) {
        setLfdNr(builder.lfdNr);
        setExpression(builder.expression);
        setPossibleIndicators(builder.possibleIndicators);
    }

    public ConditionDecl() {
        super();
    }

    public ConditionDecl(String lfdNr, String expression, String possibleIndicators) {
        super(lfdNr, expression, possibleIndicators);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull ConditionDecl copy) {
        Builder builder = new Builder();
        builder.lfdNr = copy.lfdNr;
        builder.expression = copy.expression;
        builder.possibleIndicators = copy.possibleIndicators;
        return builder;
    }

    @Override
    public String getLfdNr() {
        return super.getLfdNr();
    }

    @Override
    public void setLfdNr(String lfdNr) {
        super.setLfdNr(lfdNr);
    }

    @Override
    public String getExpression() {
        return super.getExpression();
    }

    @Override
    public void setExpression(String expression) {
        super.setExpression(expression);
    }

    @Override
    public String getPossibleIndicators() {
        return super.getPossibleIndicators();
    }

    @Override
    public void setPossibleIndicators(String possibleIndicators) {
        super.setPossibleIndicators(possibleIndicators);
    }

    @Override
    public int getPossibleIndicatorSize() {
        return super.getPossibleIndicatorSize();
    }

    @Override
    public String toString() {
        return super.toString();
    }


    /**
     * {@code Declaration} builder static inner class.
     */
    public static final class Builder {
        private String lfdNr;
        private String expression;
        private String possibleIndicators;

        private Builder() {
        }

        /**
         * Sets the {@code lfdNr} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code lfdNr} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withLfdNr(@Nonnull String val) {
            lfdNr = val;
            return this;
        }

        /**
         * Sets the {@code expression} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code expression} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withExpression(@Nonnull String val) {
            expression = val;
            return this;
        }

        /**
         * Sets the {@code possibleIndicators} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code possibleIndicators} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withPossibleIndicators(@Nonnull String val) {
            possibleIndicators = val;
            return this;
        }

        /**
         * Returns a {@code Declaration} built from the parameters previously set.
         *
         * @return a {@code Declaration} built with parameters of this {@code Declaration.Builder}
         */
        @Nonnull
        public ConditionDecl build() {
            return new ConditionDecl(this);
        }

    }
}
