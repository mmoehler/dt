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

/**
 * Created by moehler on 27.07.2016.
 */
public class IntIndexed {
    public static IntIndexed index(int index, int value) {
        return new IntIndexed(index, value);
    }

    private final int index;
    private final int value;

    private IntIndexed(int index, int value) {
        this.index = index;
        this.value = value;
    }

    /**
     * @return The indexed value.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return The value indexed.
     */
    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntIndexed that = (IntIndexed) o;

        if (index != that.index) return false;
        return value == that.value;

    }

    @Override
    public int hashCode() {
        int result = (int) (index ^ (index >>> 32));
        result = 31 * result + value;
        return result;
    }
}
