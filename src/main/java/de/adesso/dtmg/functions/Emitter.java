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

package de.adesso.dtmg.functions;

import java.util.Objects;

/**
 * Created by mmoehler on 04.06.16.
 */
public interface Emitter<T,R> {
    R emit(T t);

    default <V> Emitter<T, V> andThen(Emitter<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.emit(emit(t));
    }

    default <V> Emitter<V, R> compose(Emitter<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> emit(before.emit(v));
    }

}
