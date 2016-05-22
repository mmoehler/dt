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

import de.vandermeer.asciitable.v1.V1_AsciiTable;
import de.vandermeer.asciitable.v1.V1_StandardTableThemes;
import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthFixedColumns;
import de.vandermeer.asciitable.v2.row.ContentRow;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by mmoehler on 21.05.16.
 */
public class TableTest {
    @Test
    public void testCreationV2() throws Exception {
        char[] al = "cccccc".toCharArray();
        V2_AsciiTable t = new V2_AsciiTable(1);
        t.addStrongRule();
        ContentRow row = t.addRow(null,null,null,null,null,"RULES");
        row.setAlignment(al);
        t.addStrongRule();
        row = t.addRow("R01,R02,R03,R04,R05,R06".split("[,]"));
        row.setAlignment(al);
        t.addStrongRule();
        for (int i = 0; i < 5; i++) {
            row = t.addRow(1, 2, 3, 4, 5, 6);
            row.setAlignment(al);
            row = t.addRow(7, 8, 9, 0, 1, 2);
            row.setAlignment(al);
        }
        t.addStrongRule();
        row = t.addRow(null,null,null,null,null,"ACTIONS");
        row.setAlignment(al);
        t.addStrongRule();
        for (int i = 0; i < 2; i++) {
            row = t.addRow(1, 2, 3, 4, 5, 6);
            row.setAlignment(al);
            row = t.addRow(7, 8, Integer.MAX_VALUE, 0, 1, 2);
            row.setAlignment(al);
        }
        t.addStrongRule();

        WidthFixedColumns widths = new WidthFixedColumns();
        for (int i = 0; i < t.getColumnCount(); i++) {
            widths.add(5);
        }
        V2_AsciiTableRenderer r = new V2_AsciiTableRenderer().setWidth(widths);
        r.setTheme(V2_E_TableThemes.PLAIN_7BIT.get());
        RenderedTable renderedTable = r.render(t);
        String s = renderedTable.toString();
        System.out.println(s);
    }

    @Test
    public void testCreationV1() throws Exception {
        V1_AsciiTable t = V1_AsciiTable.newTable(6, 37);
        Assert.assertNotNull(t, "Table instanziation fails!!");
        t.addRow("R01,R02,R03,R04,R05,R06".split("[,]"));
        t.addRow(1, 2, 3, 4, 5, 6);
        t.addRow(7, 8, 9, 0, 1, 2);

        t.setPaddingCharacter('^');

        t.setTheme(V1_StandardTableThemes.PLAIN_7BIT);

        String s = t.render();
        System.out.println(s);

    }
}