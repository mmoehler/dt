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

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 27.05.16.
 */
public class AsciiTable {
    private final AsciiRows conditionRows = new AsciiRows();
    private final AsciiRows actionRows = new AsciiRows();
    private final AsciiRows headerRows = new AsciiRows();
    private final AsciiRows footerRows = new AsciiRows();

    public AsciiRows getActionRows() {
        return actionRows;
    }

    public AsciiRows getConditionRows() {
        return conditionRows;
    }

    public AsciiRows getHeaderRows() {
        return headerRows;
    }

    public AsciiRows getFooterRows() {
        return footerRows;
    }

    public int getColumnCount() {
        return conditionRows.data.get(0).size();
    }

    public int getRowLength() {
        return getConditionRows().getRowLength();
    }

    static class AsciiRows implements Consumer<AsciiRow> {
        private final List<AsciiRow> data = Lists.newArrayList();

        @Override
        public void accept(AsciiRow row) {
            this.data.add(row);
        }

        public List<AsciiRow> intern() {
            return data;
        }

        public Stream<AsciiRow> stream() {
            return data.stream();
        }


        public int getRowLength() {
            return data.get(0).stream().mapToInt(s -> s.length()).sum() + ((data.size() - 1));
        }
    }


}
