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

package de.adesso.tools.util.output;

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import de.adesso.tools.functions.MoreCollectors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 26.05.16.
 */
public final class RowFunctions {

    public static final String SPACE = " ";

    private RowFunctions() {
    }

    public static Function<List<String>, List<String>> formatRow(TableFormat format) {
        return (rawData) -> {
            List<List<String>> normalized = Stream.of(rawData).map(l -> RowFunctions.normalize(format).apply(l)).collect(MoreCollectors.toSingleObject());
            final int maxSize = normalized.stream().mapToInt(l -> l.size()).max().getAsInt();
            return normalized.stream().map(l -> {
                while (l.size() < maxSize) {
                    l.add(Strings.repeat(SPACE, l.get(0).length()));
                }
                return l;
            }).reduce(new ArrayList<>(), (a, b) -> (a.isEmpty())
                    ? b
                    : StreamUtils.zip(a.stream(), b.stream(), (l, r) -> l + format.columnSeparator.get() + r)
                    .collect(Collectors.toList()));
        };
    }

    private static Function<List<String>, List<List<String>>> normalize(TableFormat format) {
        return (l) -> {
            Preconditions.checkArgument(Iterables.size(format.getColumnFormats()) >= l.size(),
                    "Missing %s column format definitions!",
                    l.size() - Iterables.size(format.getColumnFormats()));
            Iterator<ColumnFormat> itf = format.getColumnFormats().iterator();
            return l.stream()
                    .map(s -> Stream.of(s)
                            .map(FieldFunctions.format(itf.next()))
                            .collect(MoreCollectors.toSingleObject()))
                    .collect(Collectors.toList());
        };
    }

}
