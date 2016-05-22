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

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.collect.Lists;
import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple4;
import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthFixedColumns;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by mmoehler on 22.05.16.
 */
public class EmitterTest {

    public static final String SPLIT_REGEX = "[;]";

    @Test
    public void testApply() throws Exception {
        List<String> rawConditionDefs =
                Lists.newArrayList("Y;Y;Y;N", "Y; ; ; ", " ; ;Y;Y", " ;Y; ; ");
        ObservableList<ObservableList<String>> conditionDefs = rawConditionDefs.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawConditionDecls =
                Lists.newArrayList("C01;90 minutes;Y,N", "C02;Team A Winning;Y,N", "C03;Team B Winning;Y,N", "C04;Draw;Y,N", "C05;Keep Playing;X");
        ObservableList<ConditionDecl> conditionDecls = rawConditionDecls.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ConditionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());


        List<String> rawActionDefs =
                Lists.newArrayList("X; ; ;X", "X; ; ; ", " ; ; ;X", " ;X; ; ", " ;X;X; ");
        ObservableList<ObservableList<String>> actionDefs = rawActionDefs.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawActionDecls =
                Lists.newArrayList("A01;Game Over;X", "A02;Team A Wins;X", "A03;Team B Wins;X", "A04;Extra Time;X", "A05;Keep Playing;X");
        ObservableList<ActionDecl> actionDecls = rawActionDecls.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ActionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());

        Tuple4<ObservableList<ConditionDecl>, ObservableList<ActionDecl>,
                ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> dt =
                Tuple.of(conditionDecls, actionDecls, conditionDefs, actionDefs);

        Optional<V2_AsciiTable> reduce = Stream.of(dt).map(new Emitter()).reduce((a, b) -> a = b);

        reduce.ifPresent(t -> {

            WidthFixedColumns widths = new WidthFixedColumns();
            for (int i = 0; i < t.getColumnCount(); i++) {
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


            V2_AsciiTableRenderer r = new V2_AsciiTableRenderer().setWidth(widths);
            r.setTheme(V2_E_TableThemes.PLAIN_7BIT.get());
            RenderedTable renderedTable = r.render(t);
            String s = renderedTable.toString();
            System.out.println(s);
        });

    }

    @Test
    public void testEmitConditions() throws Exception {

        List<String> rawDefinitions =
                Lists.newArrayList("Y;Y;Y;N", "Y; ; ; ", " ; ;Y;Y", " ;Y; ; ");
        ObservableList<ObservableList<String>> definitions = rawDefinitions.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawDeclarations =
                Lists.newArrayList("0;90 minutes;Y,N", "1;Team A Winning;Y,N", "2;Team B Winning;Y,N", "3;Draw;Y,N", "4;Keep Playing;X");
        ObservableList<ConditionDecl> declarations = rawDeclarations.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ConditionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());

        Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>> input =
                Tuple.of(declarations, definitions);

        List<Object[]> expected = StreamUtils.zip(rawDeclarations.stream(), rawDefinitions.stream(), (l, r) -> (l + ";" + r).split(SPLIT_REGEX)).collect(Collectors.toList());

        List<Object[]> actual = new Emitter().emitConditions().apply(input).collect(Collectors.toList());


        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(expected.size() == actual.size());

        Iterator<Object[]> expIterator = expected.iterator();
        Iterator<Object[]> actIterator = actual.iterator();

        while (expIterator.hasNext() && actIterator.hasNext()) {
            assertArrayEquals(expIterator.next(), actIterator.next());
        }
    }

    @Test
    public void testEmitCondition() throws Exception {
        ConditionDecl a = new ConditionDecl(String.valueOf(0), "Printer does not print", "Y,N");
        Object[] expectedA = a.toArray();
        ObservableList<String> b = FXCollections.observableArrayList("Y,N,Y,N,Y,N,Y,N".split("[,]"));
        Object[] expectedB = b.toArray();

        Object[] expected = Arrays.copyOf(expectedA, expectedA.length + expectedB.length);
        System.arraycopy(expectedB, 0, expected, expectedA.length, expectedB.length);

        Object[] actual = new Emitter().emitCondition(Tuple.of(a, b));

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testEmitActions() throws Exception {

        List<String> rawDefinitions =
                Lists.newArrayList("X; ; ;X", "X; ; ; ", " ; ; ;X", " ;X; ; ", " ;X;X; ");
        ObservableList<ObservableList<String>> definitions = rawDefinitions.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawDeclarations =
                Lists.newArrayList("0;Game Over;X", "1;Team A Wins;X", "2;Team B Wins;X", "3;Extra Time;X", "4;Keep Playing;X");
        ObservableList<ActionDecl> declarations = rawDeclarations.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ActionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());

        Tuple2<ObservableList<ActionDecl>, ObservableList<ObservableList<String>>> input =
                Tuple.of(declarations, definitions);

        List<Object[]> expected = StreamUtils.zip(rawDeclarations.stream(), rawDefinitions.stream(), (l, r) -> (l + ";" + r).split(SPLIT_REGEX)).collect(Collectors.toList());

        List<Object[]> actual = new Emitter().emitActions().apply(input).collect(Collectors.toList());


        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(expected.size() == actual.size());

        Iterator<Object[]> expIterator = expected.iterator();
        Iterator<Object[]> actIterator = actual.iterator();

        while (expIterator.hasNext() && actIterator.hasNext()) {
            assertArrayEquals(expIterator.next(), actIterator.next());
        }
    }

    @Test
    public void testEmitAction() throws Exception {
        ActionDecl a = new ActionDecl(String.valueOf(0), "Check the power cable", "X");
        Object[] expectedA = a.toArray();
        ObservableList<String> b = FXCollections.observableArrayList(" , , , ,X, , , ".split("[,]"));
        Object[] expectedB = b.toArray();

        Object[] expected = Arrays.copyOf(expectedA, expectedA.length + expectedB.length);
        System.arraycopy(expectedB, 0, expected, expectedA.length, expectedB.length);

        Object[] actual = new Emitter().emitAction(Tuple.of(a, b));

        assertArrayEquals(expected, actual);

    }

    @Test
    public void testEmitHeader() throws Exception {

    }
}