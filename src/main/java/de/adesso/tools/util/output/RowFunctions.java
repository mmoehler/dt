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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.adesso.tools.export.ascii.AsciiRow;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.codepoetics.protonpack.StreamUtils.zip;
import static com.google.common.collect.Iterables.size;
import static de.adesso.tools.functions.MoreCollectors.toAsciiRow;
import static de.adesso.tools.functions.MoreCollectors.toSingleObject;
import static de.adesso.tools.util.output.FieldFunctions.format;
import static java.util.stream.Collectors.toList;

/**
 * Created by mmoehler on 26.05.16.
 */
public final class RowFunctions {

    public static final String SPACE = " ";

    private RowFunctions() {
    }

    public static Function<AsciiRow, AsciiRow> formatRow(TableFormat format) {
        return (rawData) -> {
            List<List<String>> normalized = Stream.of(rawData).map(l -> normalizeFields(format).apply(l.intern())).collect(toSingleObject());
            final int maxSize = normalized.stream().mapToInt(l -> l.size()).max().getAsInt();
            return normalized.stream().map(l -> {
                while (l.size() < maxSize) {
                    l.add(Strings.repeat(SPACE, l.get(0).length()));
                }
                return l;
            }).map(AsciiRow::new).reduce(new AsciiRow(), (a, b) -> (a.isEmpty())
                    ? b
                    : zip(a.stream(), b.stream(), (l, r) -> l + format.columnSeparator.get() + r)
                    .collect(toAsciiRow()));
        };
    }

    private static Function<List<String>, List<List<String>>> normalizeFields(TableFormat format) {
        return (l) -> {

            String s1 = String.format(">>>>> l=%d ---> f=%d", l.size(), size(format.getColumnFormats()));
            System.out.println(s1);
            System.out.println(l);

            Preconditions.checkArgument(size(format.getColumnFormats()) >= l.size(),
                    "Missing %s column format definitions!",
                    l.size() - size(format.getColumnFormats()));

            Iterator<ColumnFormat> itf = format.getColumnFormats().iterator();
            return l.stream()
                    .map(s -> Stream.of(s)
                            .map(format(itf.next()))
                            .collect(toSingleObject()))
                    .collect(toList());
        };
    }

}
