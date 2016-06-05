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

package de.adesso.dtmg.export.odf;


import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.list.List;
import org.testng.annotations.Test;

/**
 * Created by mmoehler on 05.06.16.
 */
public class ODFExportTest {

    @Test
    public void testDoit() throws Exception {
        TextDocument outputOdt;
        try {
            outputOdt = TextDocument.newTextDocument();



            // add image
            //outputOdt.newImage(new URI("odf-logo.png"));

            // add paragraph
            outputOdt.addParagraph("Hello World, Hello Simple ODF!");

            Font font1Base = new Font("Coutier", StyleTypeDefinitions.FontStyle.REGULAR, 12, Color.RED, StyleTypeDefinitions.TextLinePosition.REGULAR);
            outputOdt.getParagraphByIndex(0,true).setFont(font1Base);

            // add list
            outputOdt.addParagraph("The following is a list.");
            List list = outputOdt.addList();
            String[] items = {"item1", "item2", "item3"};
            list.addItems(items);

            // add table
            Table table = outputOdt.addTable(2, 2);
            Cell cell = table.getCellByPosition(0, 0);
            cell.setStringValue("Hello World!");

            outputOdt.save("HelloWorld-01.odt");
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }
    }
}