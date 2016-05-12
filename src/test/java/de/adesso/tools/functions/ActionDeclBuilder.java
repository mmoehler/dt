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

import de.adesso.tools.functions.chained.first.AbstractSubBuilder;
import de.adesso.tools.functions.chained.first.Callback;
import de.adesso.tools.model.ActionDecl;

/**
 * Created by mmoehler ofList 06.03.16.
 */
public class ActionDeclBuilder<C> extends AbstractSubBuilder<ActionDecl, C> {
    private final String lfdNr;
    private String expression;
    private String indicators;

    public ActionDeclBuilder(String lfdNr, C caller, Callback<ActionDecl> callback) {
        super(caller, callback);
        this.lfdNr = lfdNr;
    }

    @Override
    public ActionDecl build() {
        return new ActionDecl(lfdNr, expression, indicators);
    }

    public ActionDeclBuilder<C> withExpression(String expression) {
        this.expression = expression;
        return this;
    }

    public C withIndicators(String possibleIndicators) {
        this.indicators = possibleIndicators;
        getCallback().call(build());
        return getCaller();
    }

}
