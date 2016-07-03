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

package de.adesso.dtmg.io.strategy;

import com.google.common.io.LineReader;
import de.adesso.dtmg.exception.IOExceptionSmuggler;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.io.PersistenceStrategy;
import de.adesso.dtmg.io.builder.ActionDeclTableViewModelListBuilder;
import de.adesso.dtmg.io.builder.ConditionDeclTableViewModelListBuilder;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.adesso.dtmg.util.tuple.Tuple3;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.net.www.ParseUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by mmoehler on 02.07.16.
 */
public class HorizontalAsciiPersistenceStrategy implements PersistenceStrategy<DtEntity> {

    public static final String DTH = "dth";

    public HorizontalAsciiPersistenceStrategy() {
    }

    @Override
    public String extension() {
        return DTH;
    }

    private Tuple2<ObservableList<ActionDeclTableViewModel>,ObservableList<ObservableList<String>>> readActions(LineReader lr, String lastLine) {
        try {
            String l = lastLine;
            String[] _s0 = l.split(":");
            int _i = Integer.parseInt(_s0[0]);
            ObservableList<ActionDeclTableViewModel> actionDecls = FXCollections.observableArrayList();
            String[] _s1 = _s0[1].split(",");
            ActionDeclTableViewModelListBuilder abuilder = new ActionDeclTableViewModelListBuilder();
            for (int j = 0; j < _s1.length; j++) {
                abuilder.addTableViewModelWithLfdNbr(String.format("A%02d", j))
                        .withExpression(_s1[j])
                        .withIndicators("X");
            }

            actionDecls.addAll(abuilder.build());

            // Read ActionDefns

            ObservableList<ObservableList<String>> actionDefns = FXCollections.observableArrayList();
            for (int j = 0; j < _i; j++) {
                actionDefns.add(FXCollections.observableArrayList());
            }
            for (; ; ) {
                l = lr.readLine(); // 10000
                if (null == l) break;
                String[] s2 = l.split("");
                Iterator<ObservableList<String>> it = actionDefns.iterator();
                Arrays.stream(s2)
                        .map(x -> ("1".equals(x) ? "X" : (".".equals(x)) ? "-" : x))
                        .forEach(y -> it.next().add(y));
            }

            return Tuple.of(actionDecls,actionDefns);

        } catch(IOException e) {
            throw new IOExceptionSmuggler(e);
        }

    }

    private Tuple3<String,ObservableList<ConditionDeclTableViewModel>,ObservableList<ObservableList<String>>> readConditions(LineReader lr) {
        try {
            String l = lr.readLine();
            // -- Build ConditionDEcls (5:x,p,q,r,s)
            String[] s0 = l.split(":");

            ObservableList<ConditionDeclTableViewModel> conditionDecls = FXCollections.observableArrayList();
            String[] s1 = s0[1].split(",");
            ConditionDeclTableViewModelListBuilder builder = new ConditionDeclTableViewModelListBuilder();
            for (int j = 0; j < s1.length; j++) {
                builder.addTableViewModelWithLfdNbr(String.format("C%02d", j))
                        .withExpression(s1[j])
                        .withIndicators("Y,N");
            }
            conditionDecls.addAll(builder.build());

            // Read ConditionDefns
            int i = Integer.parseInt(s0[0]);
            ObservableList<ObservableList<String>> conditionDefns = FXCollections.observableArrayList();
            for (int j = 0; j < i; j++) {
                conditionDefns.add(FXCollections.observableArrayList());
            }
            for(;;) {
                l = lr.readLine(); // 10000
                if(l.matches("[2-9].*")) break;
                String[] s2 = l.split("");
                Iterator<ObservableList<String>> it = conditionDefns.iterator();
                Arrays.stream(s2)
                        .map(x -> ("1".equals(x) ? "Y" : ("0".equals(x)) ? "N" : x))
                        .forEach(y -> it.next().add(y));
            };

            return Tuple.of(l, conditionDecls, conditionDefns);
        } catch (IOException e) {
            throw new IOExceptionSmuggler(e);
        }
    }


    @Override
    public DtEntity read(URL source) {
        final String path = ParseUtil.decode(source.getPath());
        FileChannel fileChannel = null;
        MappedByteBuffer buffer = null;
        try {
            fileChannel = new RandomAccessFile(path, "r").getChannel();
            buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        } catch (IOException e) {
            throw new IOExceptionSmuggler(e);
        }
        CharBuffer cb =  Charset.forName("utf8").decode(buffer);
        LineReader lr = new LineReader(cb);


        Tuple3<String,ObservableList<ConditionDeclTableViewModel>, ObservableList<ObservableList<String>>> con = readConditions(lr);
        Tuple2<ObservableList<ActionDeclTableViewModel>, ObservableList<ObservableList<String>>> act = readActions(lr, con._1());
        return new DtEntity(con._2(),con._3(),act._1(),act._2());
    }

    @Override
    public void write(DtEntity dtEntity, URL target) {
        throw new UnsupportedOperationException("write *.dth");
    }
}
