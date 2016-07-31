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

package de.adesso.dtmg.common.builder;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by moehler on 12.05.2016.
 */
public abstract class AbstractNestable<P, R> implements Nestable<P, R> {

    public static final String ERROR_MESSAGE = "Builder not used as nested builder!";
    private final P parentBuilder;
    private final Callback<R> ownerCallback;

    public AbstractNestable() {
        this.parentBuilder = null;
        this.ownerCallback = null;
    }

    public AbstractNestable(P parentBuilder, Callback ownerCallback) {
        this.parentBuilder = parentBuilder;
        this.ownerCallback = ownerCallback;
    }

    @Override
    public P done() {
        checkNotNull(ownerCallback, ERROR_MESSAGE).call(this.build());
        return checkNotNull(parentBuilder, ERROR_MESSAGE);
    }
}
