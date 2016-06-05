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

package de.adesso.dtmg.export.ascii;

import com.google.common.collect.Lists;
import de.adesso.dtmg.functions.MoreCollectors;
import de.adesso.dtmg.util.output.TableFormat;
import de.adesso.dtmg.util.output.TableFunctions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 29.05.16.
 */
public class Formatter implements Function<AsciiTable, String> {


    public static final char CHR_ASTERISK = '*';
    private final TableFormat format;


    public Formatter(TableFormat format) {
        this.format = format;
    }

    @Override
    public String apply(AsciiTable asciiTable) {
            final int minLen = format.calculateRowLength();

        List<Supplier<String>> rows = Lists.newLinkedList();

        AsciiTable.AsciiRows headerRows = asciiTable.getHeaderRows();
        List<AsciiRow> formattedHeader = Stream.of(headerRows.intern())
                .map(TableFunctions.formatTable(format))
                .collect(MoreCollectors.toSingleObject());
        formattedHeader.forEach(rows::add);

        SeparatorRow.Builder conditions = SeparatorRow.newBuilder().minLength(minLen).title("*CONDITIONS").padChar(CHR_ASTERISK);
        rows.add(conditions.build());

        AsciiTable.AsciiRows conditionAsciiRows = asciiTable.getConditionRows();
        List<AsciiRow> formattedConditions = Stream.of(conditionAsciiRows.intern())
                .map(TableFunctions.formatTable(format))
                .collect(MoreCollectors.toSingleObject());
        formattedConditions.forEach(rows::add);

        SeparatorRow.Builder actions = SeparatorRow.newBuilder().minLength(minLen).title("*ACTIONS").padChar(CHR_ASTERISK);
        rows.add(actions.build());

        AsciiTable.AsciiRows actionAsciiRows = asciiTable.getActionRows();
        List<AsciiRow> formattedActions = Stream.of(actionAsciiRows.intern())
                .map(TableFunctions.formatTable(format))
                .collect(MoreCollectors.toSingleObject());
        formattedActions.forEach(rows::add);

        SeparatorRow.Builder endt = SeparatorRow.newBuilder().minLength(minLen).title("*ENDT").padChar(CHR_ASTERISK);
        rows.add(endt.build());

        return rows.stream()
                .map(r -> r.get())
                .reduce("", (a, b) -> (a.length() == 0) ? b : a.concat(System.lineSeparator()).concat(b));
    }
}
