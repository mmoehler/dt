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

package de.adesso.tools.io;

import de.adesso.tools.Dump;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.exception.IOExceptionSmuggler;
import de.adesso.tools.functions.Adapters;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TableView;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.exception.LambdaExceptionUtil.rethrowIntFunction;
import static de.adesso.tools.functions.DtFunctionsTestData.conditionDeclTableViewBuilder;

/**
 * Created by mmoehler on 01.04.16.
 */
public class StorerTest {

    @BeforeClass
    public void setUp() throws Exception {
        JFXPanel fxPanel = new JFXPanel();
    }

    @Test
    public void testStore() throws Exception {
        final List<List<String>> inConditions = MatrixBuilder.on("Y,Y,Y,-,-,N,N,N,-,-,-,N,Y,Y,N,N").dim(4, 4).build();

        TableView<ConditionDeclTableViewModel> conditionDeclTab = conditionDeclTableViewBuilder()
                .addModelWithLfdNbr("01").withExpression("EXP-01").withIndicators("Y,N")
                .addModelWithLfdNbr("02").withExpression("EXP-02").withIndicators("Y,N")
                .addModelWithLfdNbr("03").withExpression("EXP-03").withIndicators("Y,N")
                .addModelWithLfdNbr("04").withExpression("EXP-04").withIndicators("Y,N")
                .build();

        List<ConditionDeclTableViewModel> inDecls = Adapters.Lists.adapt(conditionDeclTab.getItems());

        DtDataPacket dp = new DtDataPacket(inDecls, inConditions);

        Dump.dumpTableItems("BEFORE CONDS", dp.conditionDefinitions);

        Dump.dumpList1DItems("BEFORE DECLS", dp.getConditionDeclarations());

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(bout);
        outStream.writeObject(dp);

        byte[] theFile = bout.toByteArray();

        outStream.close();
        bout.close();

        System.out.println("serialized conditionDefinitions = " + new String(theFile));

        ByteArrayInputStream fileIn = new ByteArrayInputStream(theFile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        DtDataPacket dp0 = (DtDataPacket) in.readObject();
        in.close();
        fileIn.close();


        Dump.dumpTableItems("AFTER CONDS", dp0.conditionDefinitions);
        Dump.dumpList1DItems("AFTER DECLS", dp0.getConditionDeclarations());
    }

    static class DtDataPacket implements Externalizable {

        private final List<List<String>> conditionDefinitions;
        private final List<ConditionDeclTableViewModel> conditionDeclarations;

        public DtDataPacket() {
            this.conditionDefinitions = new ArrayList<>();
            this.conditionDeclarations = new ArrayList<>();
        }

        public DtDataPacket(List<ConditionDeclTableViewModel> conditionDeclarations, List<List<String>> conditionDefinitions) {
            this.conditionDefinitions = conditionDefinitions.stream()
                    .map(r -> r.stream()
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());

            this.conditionDeclarations = conditionDeclarations.stream()
                    .collect(Collectors.toList());
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            final int rows = in.readInt();
            final int cols = in.readInt();

            try {
                readExternalConditions(in, rows, cols);

            } catch (IOExceptionSmuggler ex) {
                throw ex.getTargetException();
            }
        }

        private void readExternalConditions(ObjectInput in, int rows, int cols) throws IOException {
            IntStream.range(0, rows)
                    .mapToObj(rethrowIntFunction(k -> {
                        final String lfdnr = in.readUTF();
                        final String expr = in.readUTF();
                        final String posind = in.readUTF();
                        return new ConditionDeclTableViewModel(new ConditionDecl(lfdnr, expr, posind));
                    }))
                    .forEach(k -> conditionDeclarations.add(k));


            IntStream.range(0, rows).forEach(k -> conditionDefinitions.add(new ArrayList<>(cols)));
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    final String s = in.readUTF();
                    conditionDefinitions.get(i).add(s);
                }
            }
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            final int rows = conditionDefinitions.size();
            final int cols = conditionDefinitions.get(0).size();

            try {

                out.writeInt(rows);
                out.writeInt(cols);

                IntStream.range(0, rows)
                        .mapToObj(i -> conditionDeclarations.get(i))
                        .map(e -> e.getModel()).forEach(m -> {
                    try {
                        out.writeUTF(m.getLfdNr());
                        out.writeUTF(m.getExpression());
                        out.writeUTF(m.getPossibleIndicators());
                    } catch (IOException e) {
                        throw new IOExceptionSmuggler(e);
                    }
                });

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        out.writeUTF(conditionDefinitions.get(i).get(j));
                    }
                }
            } catch (IOExceptionSmuggler ex) {
                throw ex.getTargetException();
            }
        }

        public List<List<String>> getConditionDefinitions() {
            return conditionDefinitions;
        }

        public List<ConditionDeclTableViewModel> getConditionDeclarations() {
            return conditionDeclarations;
        }
    }


}