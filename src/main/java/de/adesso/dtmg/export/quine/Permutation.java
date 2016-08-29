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


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by moehler on 20.07.2016.
 */
public class Permutation<L, R> implements Iterable<Pair<L, R>> {

    final Iterable<L> ps;
    final Iterable<R> qs;

    public Permutation(Iterable<L> ps, Iterable<R> qs) {
        this.ps = ps;
        this.qs = qs;
    }

    @Override
    public Iterator<Pair<L, R>> iterator() {
        return new Iterator<Pair<L, R>>() {
            Iterator<L> pIterator = ps.iterator();
            L currentP = null;
            Iterator<R> qIterator = qs.iterator();
            Pair<L, R> next = null;

            @Override
            public boolean hasNext() {
                while (next == null && qIterator != null) {
                    if (currentP == null) {
                        currentP = pIterator.hasNext() ? pIterator.next() : null;
                    }
                    if (currentP != null) {
                        if (qIterator.hasNext()) {
                            next = new Pair<>(currentP, qIterator.next());
                        } else {
                            qIterator = qs.iterator();
                            currentP = null;
                        }
                    } else {
                        qIterator = null;
                    }
                }
                return next != null;
            }

            @Override
            public Pair<L, R> next() {
                if (hasNext()) {
                    Pair<L, R> nxt = next;
                    next = null;
                    return nxt;
                }
                throw new NoSuchElementException();
            }
        };
    }
}