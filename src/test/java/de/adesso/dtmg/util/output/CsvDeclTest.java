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

package de.adesso.dtmg.util.output;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.adesso.dtmg.model.ActionDecl;
import org.assertj.core.util.Files;
import org.testng.annotations.Test;

import java.io.*;

/**
 * Created by moehler on 03.08.2016.
 */
public class CsvDeclTest {

    @Test
    public void saveActionDeclToCsvTest() throws Exception {
        CsvMapper mapper = new CsvMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CsvSchema schema = mapper.schemaFor(ActionDecl.class);
        schema = schema.withColumnSeparator(';');


        ActionDecl actionDecl = ActionDecl.newBuilder().withLfdNr("A01").withExpression("doCalculate").withPossibleIndicators("X").build();


        File f = Files.newTemporaryFile();
        System.out.println(f.getAbsolutePath());

        // output writer
        ObjectWriter myObjectWriter = mapper.writer(schema);
        try(
            FileOutputStream tempFileOutputStream = new FileOutputStream(f);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(tempFileOutputStream, 1024)) {
            OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
            myObjectWriter.writeValue(writerOutputStream, actionDecl);
        }

        ObjectReader myObjectReader = mapper.readerFor(ActionDecl.class).with(schema);
        try(
                FileInputStream tempFileInputStream = new FileInputStream(f);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(tempFileInputStream, 1024)) {
            InputStreamReader readerInputStream = new InputStreamReader(bufferedInputStream, "UTF-8");
            ActionDecl reincarnated = myObjectReader.readValue(readerInputStream);

            System.out.println("reincarnated = " + reincarnated);
        }






    }
}
