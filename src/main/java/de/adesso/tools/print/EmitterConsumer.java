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

package de.adesso.tools.print;

import com.google.common.io.CharSink;
import de.adesso.tools.exception.LambdaExceptionUtil;
import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthFixedColumns;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by moehler on 23.05.2016.
 */
public class EmitterConsumer {

    public static Consumer<V2_AsciiTable> outputTo(Supplier<CharSink> target) {

        return LambdaExceptionUtil.rethrowConsumer(t -> {
            WidthFixedColumns widths = defineColumnWidth(t);
            String s = renderTable(t, widths);

            if (null == s) throw new IOException();

            final Writer writer = target.get().openBufferedStream();
            writer.write(s);
            writer.flush();

        });

/*

        (t) -> {
            WidthFixedColumns widths = defineColumnWidth(t);
            String s = renderTable(t, widths);

            //System.out.println(">>>>> "+s);

            try {
                target.get().write(s);
                target.get().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        };

*/

    }

    private static String renderTable(V2_AsciiTable table, WidthFixedColumns widths) {
        V2_AsciiTableRenderer r = new V2_AsciiTableRenderer().setWidth(widths);
        r.setTheme(V2_E_TableThemes.PLAIN_7BIT.get());
        RenderedTable renderedTable = r.render(table);
        return renderedTable.toString();
    }

    private static WidthFixedColumns defineColumnWidth(V2_AsciiTable table) {
        WidthFixedColumns widths = new WidthFixedColumns();
        for (int i = 0; i < table.getColumnCount(); i++) {
            int w = 0;
            switch (i) {
                case 1:
                    w = 30;
                    break;
                case 2:
                    w = 12;
                    break;
                default:
                    w = 5;
                    break;
            }
            widths.add(w);
        }
        return widths;
    }
}
