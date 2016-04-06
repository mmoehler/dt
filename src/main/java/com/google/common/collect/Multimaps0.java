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

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by moehler on 06.04.2016.
 */
public final class Multimaps0 {

    private final static Multimap EMPTY_MULTI_MAP = new EmptyMultimap<>();

    private Multimaps0() {
    }

    public static <K, V> Multimap<K, V> emptyMultimap() {
        return (Multimap<K, V>) EMPTY_MULTI_MAP;
    }

    static class EmptyMultimap<K, V> extends AbstractMultimap<K, V> implements Multimap<K, V> {
        @Override
        Iterator<Map.Entry<K, V>> entryIterator() {
            return new Iterator<Map.Entry<K, V>>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Map.Entry<K, V> next() {
                    return null;
                }
            };
        }

        @Override
        Map createAsMap() {
            return Collections.emptyMap();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean containsKey(@Nullable Object key) {
            return false;
        }

        @Override
        public Collection<V> removeAll(@Nullable Object key) {
            return Collections.emptyList();
        }

        @Override
        public void clear() {
        }

        @Override
        public Collection get(@Nullable Object key) {
            return null;
        }
    }
}
