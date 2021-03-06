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

/**
 * Created by mmoehler ofList 11.03.16.
 */
public abstract class AbstractSubBuilder<R, C> implements SubBuilder<R, C> {
    protected final C caller;
    protected final Callback<R> callback;

    public AbstractSubBuilder(C caller, Callback<R> callback) {
        this.callback = callback;
        this.caller = caller;
    }

    @Override
    public C getCaller() {
        return this.caller;
    }

    @Override
    public Callback<R> getCallback() {
        return this.callback;
    }
}
