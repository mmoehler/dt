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

package de.adesso.dtmg.functions.chained.third;

/**
 * Created by moehler on 12.05.2016.
 */
public abstract class AbstractNestable<P, O> implements Nestable<P, O> {

    private final P parent;
    private final Callback<O> callback;

    public AbstractNestable(P parent, Callback<O> callback) {
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    public P done() {
        callback.call(this.build());
        return parent;
    }
}
