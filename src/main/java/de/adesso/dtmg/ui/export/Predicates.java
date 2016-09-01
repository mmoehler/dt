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

package de.adesso.dtmg.ui.export;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by moehler on 30.08.2016.
 */
public class Predicates {
    public static <T> Predicate<T> memoize(Predicate<T> delegate) {
        return (delegate instanceof MemoizingPredicate)
                ? delegate
                : new MemoizingPredicate<T>(checkNotNull(delegate));
    }

    public static class MemoizingPredicate<T> implements Predicate<T> {
        final Predicate<T> delegate;
        transient volatile boolean initialized;
        // "value" does not need to be volatile; visibility piggy-backs
        // on volatile read of "initialized".
        transient boolean value;

        MemoizingPredicate(Predicate<T> delegate) {
            this.delegate = delegate;
        }

        @Override public boolean test(T t) {
            // A 2-field variant of Double Checked Locking.
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        boolean tmp = delegate.test(t);
                        value = tmp;
                        initialized = true;
                        return tmp;
                    }
                }
            }
            return value;
        }

        @Override public String toString() {
            return "Predicates.memoize(" + delegate + ")";
        }

    }

}
