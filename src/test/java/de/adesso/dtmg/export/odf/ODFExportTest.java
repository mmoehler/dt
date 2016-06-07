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


import com.google.common.base.Strings;
import de.adesso.dtmg.common.builder.List2DBuilder;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.list.List;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mmoehler on 05.06.16.
 */
public class ODFExportTest {

    @Test
    public void testDoit() throws Exception {

        String ind[] = {"Y", "N", "-"};


        final java.util.List<String> stringList = IntStream.range(0, 16 * 16).mapToObj(i -> ind[ThreadLocalRandom.current().nextInt(0, 3)]).collect(Collectors.toList());

        java.util.List<java.util.List<String>> data = (List2DBuilder.matrixOf(stringList).dim(16, 16).build());


        TextDocument outputOdt;
        try {
            outputOdt = TextDocument.newTextDocument();


            // add image
            //outputOdt.newImage(new URI("odf-logo.png"));

            // add paragraph
            outputOdt.addParagraph("Hello World, Hello Simple ODF!");

            Font font1Base = new Font("Monospaced", StyleTypeDefinitions.FontStyle.REGULAR, 8, Color.RED, StyleTypeDefinitions.TextLinePosition.REGULAR);
            outputOdt.getParagraphByIndex(0, true).setFont(font1Base);

            // add list
            outputOdt.addParagraph("The following is a list.");
            List list = outputOdt.addList();
            String[] items = {"item1", "item2", "item3"};
            list.addItems(items);

            // add table

            Table table = ODFTableEmitter.emit(outputOdt, data);
            table.getColumnList().forEach(c -> {
                c.setUseOptimalWidth(true);

                IntStream.range(0, c.getCellCount()).forEach(i -> {
                    final Cell cell = c.getCellByIndex(i);
                    cell.setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.CENTER);
                    cell.setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType.MIDDLE);
                    cell.setFont(font1Base);
                });

            });


//            Table table = outputOdt.addTable(2, 2);
//            Cell cell = table.getCellByPosition(0, 0);
//            cell.setStringValue("Hello World!");

            outputOdt.save("HelloWorld-04.odt");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: unable to create output file.");
        }
    }

    @Test
    public void testFonts() {
        //String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        java.awt.Font f[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

        System.out.println(Strings.repeat("-",83));
        final String h = String.format("%-40s | %-40s", "Name", "Family");
        System.out.println(h);
        System.out.println(Strings.repeat("-",83));
        for (int i = 0; i < f.length; i++) {
            final String s = String.format("%-40s | %-40s", f[i].getFontName(), f[i].getFamily());
            System.out.println(s);
        }

/*
        for (int i = 0; i < fonts.length; i++) {
            System.out.println(fonts[i]);
        }
        */
    }

}