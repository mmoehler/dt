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

package de.adesso.dtmg.export.java.treemethod;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by moehler on 22.08.2016.
 */
public class Permutation<T> implements Iterable<T[]> {
    protected final T[] t;

    /**
     * Creates an implicit Iterable collection of all permutations of a string
     *
     * @param string String to be permuted
     * @see Iterable
     * @see #iterator
     */
    public Permutation(T[] t) {
        this.t = t;
    }

    /**
     * Constructs and sequentially returns the permutation values
     */
    @Override
    public Iterator<T[]> iterator() {

        return new Iterator<T[]>() {

            T[] array = t;
            int length = t.length;
            int[] index = (length == 0) ? null : new int[length];

            @Override
            public boolean hasNext() {
                return index != null;
            }

            @Override
            public T[] next() {

                if (index == null) throw new NoSuchElementException();

                for (int i = 1; i < length; ++i) {
                    T swap = array[i];
                    System.arraycopy(array, 0, array, 1, i);
                    array[0] = swap;
                    for (int j = 1; j < i; ++j) {
                        index[j] = 0;
                    }
                    if (++index[i] <= i) {
                        return Arrays.copyOf(array, array.length);
                    }
                    index[i] = 0;
                }
                index = null;
                return Arrays.copyOf(array, array.length);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Spliterator<T[]> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.DISTINCT + Spliterator.NONNULL + Spliterator.IMMUTABLE);
    }

    public Stream<T[]> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Stream<T[]> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
