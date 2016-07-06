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

package de.adesso.dtmg.util;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.IntStream;

import static de.adesso.dtmg.functions.MoreCollectors.toImmutableList;

/**
 * Created by moehler on 01.06.2016.
 */
public final class HLists {
    private HLists() {
    }

    public static <A> List<A> addFirst(A a, List<A> l) {
        return ImmutableList.<A>builder().add(a).addAll(l).build();
    }

    public static <A> List<A> addLast(A a, List<A> l) {
        return ImmutableList.<A>builder().addAll(l).add(a).build();
    }

    public static <E> List<E> take(List<E> l, int count) {
        final ImmutableList<E> collect = IntStream.range(0, count)
                .mapToObj(i -> l.get(i)).collect(toImmutableList());
        return collect;
    }

    public static <E> List<E> drop(List<E> l, int count) {
        final ImmutableList<E> collect = IntStream.range(count, l.size())
                .mapToObj(i -> l.get(i)).collect(toImmutableList());
        return collect;
    }

    public static <E> List<List<E>> splitAt(List<E> l, int index) {
        return ImmutableList.<List<E>>builder()
                .add(take(l,index))
                .add(drop(l,index))
                .build();
    }

    public static <E> List<E> paddLeft(int count, E e, List<E> l) {
        final ImmutableList.Builder<E> builder = ImmutableList.<E>builder();
        for (int i = 0; i < count; i++) {
            builder.add(e);
        }
        return builder.addAll(l).build();
    }

}