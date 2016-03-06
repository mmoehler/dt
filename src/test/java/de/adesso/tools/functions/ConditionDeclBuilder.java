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

package de.adesso.tools.functions;

/**
 * Created by mmoehler on 06.03.16.
 */
class ConditionDeclBuilder<T> {
        private final String lfdNr;
        private final T caller;
        private final Callback callback;
        private String expression;

        public ConditionDeclBuilder(String lfdNr, T caller, Callback callback) {
            this.lfdNr = lfdNr;
            this.caller = caller;
            this.callback = callback;
        }

        public ConditionDeclBuilder<T> withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public T withIndicators(String possibleIndicators) {
            this.callback.handleCallback(this.lfdNr, this.expression, possibleIndicators);
            return this.caller;
        }

        interface Callback {
            void handleCallback(String lfdNr, String expression, String indicators);
        }

    }
