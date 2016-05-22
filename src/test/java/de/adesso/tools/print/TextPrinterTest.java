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

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.text.Text;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by mmoehler on 20.05.16.
 */
@RunWith(JfxRunner.class)
public class TextPrinterTest {

    Printer<Node> printer = node -> {
        boolean success = false;
        javafx.print.Printer p = javafx.print.Printer.getDefaultPrinter();
        /*
        PageLayout pageLayout = p.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, javafx.print.Printer.MarginType.EQUAL);
        double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        node.getTransforms().add(new Scale(scaleX, scaleY));
        */
        System.out.println("... using " + p);
        PrinterJob job = PrinterJob.createPrinterJob(p);
        System.out.println("... job is " + job);
        if (job != null && job.showPrintDialog(null)){
            success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
        return success;
    };

    @Test()
    public void testPrint() throws Exception {
        Text text = new Text("Test of text print");
        text.layoutYProperty().set(150);
        text.prefWidth(300);
        text.prefHeight(150);
        printer.print(text);
    }


}