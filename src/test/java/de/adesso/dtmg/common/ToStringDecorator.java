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

package de.adesso.dtmg.common;

import java.util.function.Function;

/**
 * Created by moehler on 14.06.2016.
 */
public class ToStringDecorator<T> implements Stringable {
    private final T delegate;
    private final Function<T, String> transformer;

    public static <O> Stringable decorate(O t, Function<O, String> f) {
        return new ToStringDecorator(t,f);
    }

    private ToStringDecorator(T delegate, Function<T, String> transformer) {
        this.delegate = delegate;
        this.transformer = transformer;
    }

    @Override
    public String asString() {
        return transformer.apply(delegate);
    }

    public T getDelegate() {
        return delegate;
    }
}
