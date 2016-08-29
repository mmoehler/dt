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

package de.adesso.dtmg.export.quine;

import de.adesso.dtmg.util.tuple.Tuple2;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by mmoehler on 22.07.16.
 */
public class HashMapKeyAdapter implements Supplier<Tuple2<List<Integer>, String>> {
    private final Tuple2<List<Integer>, String> adaptee;

    private HashMapKeyAdapter(Tuple2<List<Integer>, String> adaptee) {
        this.adaptee = adaptee;
    }

    public static HashMapKeyAdapter adapt(Tuple2<List<Integer>, String> adaptee) {
        return new HashMapKeyAdapter(adaptee);
    }

    @Override
    public Tuple2<List<Integer>, String> get() {
        return this.adaptee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashMapKeyAdapter that = (HashMapKeyAdapter) o;

        return adaptee != null ? adaptee._2().equals(that.adaptee._2()) : that.adaptee == null;

    }

    @Override
    public int hashCode() {
        return adaptee != null ? adaptee._2().hashCode() : 0;
    }
}
