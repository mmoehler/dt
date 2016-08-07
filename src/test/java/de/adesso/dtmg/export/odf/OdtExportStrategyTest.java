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
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.PageLayoutProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mmoehler on 05.06.16.
 */
public class OdtExportStrategyTest {

    @Test
    public void testDoit() throws Exception {

        String ind[] = {"Y", "N", "-"};


        final java.util.List<String> stringList = IntStream.range(0, 16 * 16).mapToObj(i -> ind[ThreadLocalRandom.current().nextInt(0, 3)]).collect(Collectors.toList());

        //java.util.List<java.util.List<String>> data = (List2DBuilder.matrixOf(stringList).dim(16, 16).build());


        TextDocument outputOdt;
        try {
            outputOdt = TextDocument.newTextDocument();


            outputOdt.getOfficeMasterStyles().getMasterPages().forEachRemaining(System.out::println);

            StyleMasterPageElement defaultPage = outputOdt.getOfficeMasterStyles().getMasterPage("Standard");
            String pageLayoutName = defaultPage.getStylePageLayoutNameAttribute();
            System.out.println("pageLayoutName = " + pageLayoutName);
            OdfStylePageLayout pageLayoutStyle = defaultPage.getAutomaticStyles().getPageLayout(pageLayoutName);
            PageLayoutProperties pageLayoutProperties = PageLayoutProperties.getOrCreatePageLayoutProperties(pageLayoutStyle);

            double pageHeightInMM = 210.00;
            pageLayoutProperties.setPageHeight(pageHeightInMM);
            double pageWidthInMM = 297.00;
            pageLayoutProperties.setPageWidth(pageWidthInMM);
            pageLayoutProperties.setPrintOrientation(StyleTypeDefinitions.PrintOrientation.LANDSCAPE);

            //pageLayoutProperties.setMarginLeft(leftMarginInMM);
            //pageLayoutProperties.setMarginRight(rightMarginInMM);
            //pageLayoutProperties.setMarginTop(topMarginInMM);
            //pageLayoutProperties.setMarginBottom(bottomMarginInMM);


            Font font1Base = new Font("Courier new", StyleTypeDefinitions.FontStyle.REGULAR, 8, Color.RED, StyleTypeDefinitions.TextLinePosition.REGULAR);
            //outputOdt.getParagraphByIndex(0, true).setFont(font1Base);

            // add table
            OdtDecisionTableData data = OdtDecisionTableData.newBuilder().build();
            Table table = OdtTableEmitter.emit(outputOdt, data);
            table.setCellStyleInheritance(true);


            table.getColumnList().forEach(c -> {
                c.setUseOptimalWidth(true);

                IntStream.range(0, c.getCellCount()).forEach(i -> {
                    final Cell cell = c.getCellByIndex(i);
                    cell.setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.CENTER);
                    cell.setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType.MIDDLE);
                    cell.setFont(font1Base);
                });

            });

            table.getRowList().forEach(r -> {
                r.setUseOptimalHeight(true);
                int cellCount = r.getCellCount();
                IntStream.range(0,cellCount).forEach(i1 -> {
                    Cell cell1 = r.getCellByIndex(i1);
                    cell1.setFont(font1Base);
                    cell1.setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.CENTER);
                    cell1.setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType.MIDDLE);
                    cell1.setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, new Border(Color.GRAY, 1.0, StyleTypeDefinitions.SupportedLinearMeasure.PT));
                });
            });



//            Table table = outputOdt.addTable(2, 2);
//            Cell cell = table.getCellByPosition(0, 0);
//            cell.setStringValue("Hello World!");

            outputOdt.save("HelloWorld-10.odt");
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