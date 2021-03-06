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

import de.adesso.dtmg.export.AbstractExportStrategy;
import de.adesso.dtmg.io.DtEntity;
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
import sun.net.www.ParseUtil;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.stream.IntStream;

/**
 * Created by mmoehler on 05.06.16.
 */
public class OdtExportStrategy extends AbstractExportStrategy<DtEntity> {

    public static final String EXTENSION = "odt";

    @Nonnull
    @Override
    public String extension() {
        return EXTENSION;
    }

    @Override
    public void export(DtEntity dtEntity, URI target) {
        final String path = ParseUtil.decode(target.getPath());
        try {

            TextDocument outputOdt = createTextDocument(dtEntity);
            outputOdt.save(path);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private TextDocument createTextDocument(DtEntity dtEntity) {
        TextDocument outputOdt;
        try {
            outputOdt = TextDocument.newTextDocument();

            PageLayoutProperties pageLayoutProperties = obtainPageLayoutProperties(outputOdt);
            configurePageForA4Landscape(pageLayoutProperties);

            Font font1Base = obtainFontCourierNew8();

            OdtDecisionTableData.Builder builder = OdtDecisionTableData.newBuilder()
                    .conditionDecls(dtEntity.getConditionDeclarations())
                    .conditionDefs(dtEntity.getConditionDefinitions())
                    .actionDecls(dtEntity.getActionDeclarations())
                    .actionDefs(dtEntity.getActionDefinitions());

            Table table = OdtTableEmitter.emit(outputOdt, builder.build());

            table.setCellStyleInheritance(true);
            formatTable(font1Base, table);

            return outputOdt;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void formatTable(Font font1Base, Table table) {
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
            IntStream.range(0, cellCount).forEach(i1 -> {
                Cell cell1 = r.getCellByIndex(i1);
                cell1.setFont(font1Base);
                cell1.setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.CENTER);
                cell1.setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType.MIDDLE);
                cell1.setBorders(StyleTypeDefinitions.CellBordersType.ALL_FOUR, new Border(Color.GRAY, 1.0, StyleTypeDefinitions.SupportedLinearMeasure.PT));
            });
        });
    }

    private Font obtainFontCourierNew8() {
        return new Font("Courier new", StyleTypeDefinitions.FontStyle.REGULAR, 8, Color.BLACK, StyleTypeDefinitions.TextLinePosition.REGULAR);
    }

    private void configurePageForA4Landscape(PageLayoutProperties pageLayoutProperties) {
        double pageHeightInMM = 210.00;
        pageLayoutProperties.setPageHeight(pageHeightInMM);
        double pageWidthInMM = 297.00;
        pageLayoutProperties.setPageWidth(pageWidthInMM);
        pageLayoutProperties.setPrintOrientation(StyleTypeDefinitions.PrintOrientation.LANDSCAPE);

        //pageLayoutProperties.setMarginLeft(leftMarginInMM);
        //pageLayoutProperties.setMarginRight(rightMarginInMM);
        //pageLayoutProperties.setMarginTop(topMarginInMM);
        //pageLayoutProperties.setMarginBottom(bottomMarginInMM);
    }

    private PageLayoutProperties obtainPageLayoutProperties(TextDocument outputOdt) {
        StyleMasterPageElement defaultPage = outputOdt.getOfficeMasterStyles().getMasterPage("Standard");
        String pageLayoutName = defaultPage.getStylePageLayoutNameAttribute();
        OdfStylePageLayout pageLayoutStyle = defaultPage.getAutomaticStyles().getPageLayout(pageLayoutName);
        return PageLayoutProperties.getOrCreatePageLayoutProperties(pageLayoutStyle);
    }


}
