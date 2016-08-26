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

package de.adesso.dtmg.io.builder;

import de.adesso.dtmg.model.ConditionDecl;

/**
 * Created by mmoehler ofList 06.03.16.
 */
public class ConditionDeclBuilder<C> extends AbstractSubBuilder<ConditionDecl, C> {
    private final String lfdNr;
    private String expression;
    private String documentation;
    private String indicators;

    public ConditionDeclBuilder(String lfdNr, C caller, Callback<ConditionDecl> callback) {
        super(caller, callback);
        this.lfdNr = lfdNr;
    }

    @Override
    public ConditionDecl build() {
        return new ConditionDecl(lfdNr, expression, documentation, indicators);
    }

    public ConditionDeclBuilder<C> withExpression(String expression) {
        this.expression = expression;
        return this;
    }

    public ConditionDeclBuilder<C> withdocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }


    public C withIndicators(String possibleIndicators) {
        this.indicators = possibleIndicators;
        getCallback().call(build());
        return getCaller();
    }

}
