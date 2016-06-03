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

package de.adesso.tools.export.ascii;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 28.05.16.
 */
public class AsciiRow implements Iterable<String>, Supplier<String> {
    public static final String EMPTY_STRING = "";
    private List<String> data;

    @Override
    public Iterator<String> iterator() {
        return data.iterator();
    }

    public AsciiRow(int initialSize) {
        this.data = new ArrayList<>(initialSize);
    }

    public AsciiRow(AsciiRow original) {
        this.data = original.data;
    }

    public AsciiRow(List<String> data) {
        this.data = data;
    }

    public AsciiRow() {
        this.data = new ArrayList<>();
    }

    public boolean add(String s) {
        return data.add(s);
    }

    public int size() {
        return data.size();
    }

    public boolean addAll(Collection<? extends String> c) {
        return data.addAll(c);
    }

    public boolean addAll(AsciiRow c) {
        return data.addAll(c.intern());
    }


    public List<String> intern() {
        return Collections.unmodifiableList(data);
    }

    public String get(int index) {
        return data.get(index);
    }

    public Stream<String> stream() {
        return data.stream();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AsciiRow asciiRow = (AsciiRow) o;

        return data != null ? data.equals(asciiRow.data) : asciiRow.data == null;

    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @Override
    public String get() {
        return (data.isEmpty()) ? EMPTY_STRING : (data.get(0));
    }
}
