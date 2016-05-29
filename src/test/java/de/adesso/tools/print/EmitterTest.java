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
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.model.DecisionTable;
import de.adesso.tools.util.output.Align;
import de.adesso.tools.util.output.TableFormat;
import de.adesso.tools.util.output.TableFunctions;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple4;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
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
                Lists.newArrayList("Y;Y;Y;N", "Y;-;-;-", "-;-;Y;Y", "-;Y;-;-");
        ObservableList<ObservableList<String>> conditionDefs = rawConditionDefs.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawConditionDecls =
                Lists.newArrayList("*;90 minutes;1;*", "*;Team A Winning;2;*", "*;Team B Winning;3;*", "*;Draw;4;*", "*;Keep Playing;5;*");
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
                Lists.newArrayList("*;Game Over;101;*", "*;Team A Wins;102;*", "*;Team B Wins;103;*", "*;Extra Time;104;*", "*;Keep Playing;105;*");
        ObservableList<ActionDecl> actionDecls = rawActionDecls.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ActionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());

        // ---

        final DecisionTable dt = DecisionTable.newBuilder()
                .conditionDecls(conditionDecls)
                .conditionDefs(conditionDefs)
                .actionDecls(actionDecls)
                .actionDefs(actionDefs)
                .build();

        // ------------------------------------

        AsciiTable asciiTable = Stream.of(dt).map(new Emitter())
                .collect(MoreCollectors.toSingleObject());

        assertNotNull(asciiTable);
        
        final int maxLen = 40;
        final int columns = asciiTable.getColumnCount();

        TableFormat.Builder builder = TableFormat.newBuilder();

        //builder.columnSeparator().character('|').length(1).done();

        for (int i = 0; i < columns; i++) {
            switch (i) {
                case 0:
                    builder.addColumnFormat().width(3).align(Align.LEFT).done();
                    break;
                case 1:
                    builder.addColumnFormat().width(maxLen+2).align(Align.LEFT).done();
                    break;
                case 2:
                    builder.addColumnFormat().width(4).align(Align.RIGHT).done();
                case 3:
                    builder.addColumnFormat().width(3).align(Align.CENTER).done();
                    break;
                default:
                    builder.addColumnFormat().width(1).align(Align.CENTER).done();
                    break;
            }
        }


        String result = new Formatter(builder.build()).apply(asciiTable);

        System.out.println(result);

        /*

        AsciiTable.AsciiRows conditionAsciiRows = asciiTable.getConditionRows();
        List<AsciiRow> formattedConditions = Stream.of(conditionAsciiRows.intern())
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());
        formattedConditions.forEach(System.out::println);




        AsciiTable.AsciiRows headerRows = asciiTable.getHeaderRows();
        List<AsciiRow> formattedHeader = Stream.of(headerRows.intern())
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());
        formattedHeader.forEach(System.out::println);


        AsciiTable.AsciiRows conditionAsciiRows = asciiTable.getConditionRows();
        List<List<String>> formattedConditions = Stream.of(conditionAsciiRows.intern())
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toAsciiRow());
        formattedConditions.forEach(System.out::println);

        AsciiTable.AsciiRows actionAsciiRows = asciiTable.getActionRows();
        List<List<String>> formattedActions = Stream.of(actionAsciiRows.intern())
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());
        formattedActions.forEach(System.out::println);

        */

        //reduce.ifPresent(EmitterConsumer.outputTo(() -> new ConsoleCharSink(Charset.forName("utf8"))));
        //reduce.ifPresent(EmitterConsumer.outputTo(() -> new FileCharSink(new File("./sample-dt.txt"), Charset.forName("utf8"))));

        // ------------------------------------
    }

    private Tuple4<
            ObservableList<ConditionDecl>,
            ObservableList<ActionDecl>,
            ObservableList<ObservableList<String>>,
            ObservableList<ObservableList<String>>> asDecisionTable(
            ObservableList<ObservableList<String>> conditionDefs,
            ObservableList<ConditionDecl> conditionDecls,
            ObservableList<ObservableList<String>> actionDefs,
            ObservableList<ActionDecl> actionDecls) {

        return Tuple.of(conditionDecls, actionDecls, conditionDefs, actionDefs);
    }

    @Test
    public void testEmitConditions() throws Exception {

        List<String> rawDefinitions =
                Lists.newArrayList("Y;Y;Y;N", "Y;-;-;-", "-;-;Y;Y", "-;Y;-;-");
        ObservableList<ObservableList<String>> definitions = rawDefinitions.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawDeclarations =
                Lists.newArrayList("*;90 minutes;1;*", "*;Team A Winning;2;*", "*;Team B Winning;3;*", "*;Draw;4;*", "*;Keep Playing;5;*");
        ObservableList<ConditionDecl> declarations = rawDeclarations.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ConditionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());

        Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>> input =
                Tuple.of(declarations, definitions);

        // ... calculate expected values
        List<List<String>> expected = StreamUtils.zip(rawDeclarations.stream(), rawDefinitions.stream(), (l, r) -> (l + ";" + r))
                .map(Splitter.on(Pattern.compile(SPLIT_REGEX)).trimResults()::splitToList)
                .collect(Collectors.toList());

        // ... get the actual
        List<AsciiRow> actual = new Emitter().emitConditions().apply(input).collect(Collectors.toList());


        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(expected.size() == actual.size());

        Iterator<AsciiRow> expIterator = expected.stream().map(AsciiRow::new).iterator();
        Iterator<AsciiRow> actIterator = actual.iterator();

        while (expIterator.hasNext() && actIterator.hasNext()) {
            AsciiRow se = expIterator.next();
            AsciiRow sa = actIterator.next();
            assertEquals(se, sa);
        }

        final int maxLen = actual.stream().mapToInt(l -> l.get(1).length()).max().getAsInt();

        TableFormat.Builder builder = TableFormat.newBuilder();

        for (int i = 0; i < actual.get(0).size(); i++) {
            switch (i) {
                case 0:
                    builder.addColumnFormat().width(3).align(Align.LEFT).done();
                    break;
                case 1:
                    builder.addColumnFormat().width(maxLen+2).align(Align.LEFT).done();
                    break;
                case 2:
                    builder.addColumnFormat().width(5).align(Align.RIGHT).done();
                case 3:
                    builder.addColumnFormat().width(3).align(Align.CENTER).done();
                    break;
                default:
                    builder.addColumnFormat().width(1).align(Align.CENTER).done();
                    break;
            }
        }

        List<AsciiRow> collect = Stream.of(actual)
                .map(TableFunctions.formatTable(builder.build()))
                .collect(MoreCollectors.toSingleObject());

        for (AsciiRow l : collect) {
            for (String s : l) {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testEmitCondition() throws Exception {
        ConditionDecl a = new ConditionDecl(String.valueOf(0), "Printer does not print", "Y,N");
        List<String> expectedA = Lists.newArrayList("*", a.getExpression(), "1", "*");
        ObservableList<String> b = FXCollections.observableArrayList("Y,N,Y,N,Y,N,Y,N".split("[,]"));
        List<String> expectedB = b;

        List<String> tmp = Stream.concat(expectedA.stream(), expectedB.stream()).collect(Collectors.toList());
        AsciiRow expected = new AsciiRow(tmp);

        AsciiRow actual = new Emitter().emitRow(1, Tuple.of(a, b));
        System.out.println("actual = " + actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testEmitActions() throws Exception {

        List<String> rawDefinitions =
                Lists.newArrayList("X; ; ;X", "X; ; ; ", " ; ; ;X", " ;X; ; ", " ;X;X; ");
        ObservableList<ObservableList<String>> definitions = rawDefinitions.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawDeclarations =
                Lists.newArrayList("*;Game Over;101;*", "*;Team A Wins;102;*", "*;Team B Wins;103;*", "*;Extra Time;104;*", "*;Keep Playing;105;*");
        ObservableList<ActionDecl> declarations = rawDeclarations.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ActionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());

        Tuple2<ObservableList<ActionDecl>, ObservableList<ObservableList<String>>> input =
                Tuple.of(declarations, definitions);

        List<List<String>> expected = StreamUtils.zip(rawDeclarations.stream(), rawDefinitions.stream(), (l, r) -> (l + ";" + r))
                .map(Splitter.on(Pattern.compile(SPLIT_REGEX))::splitToList)
                .collect(Collectors.toList());

        List<AsciiRow> actual = new Emitter().emitActions().apply(input).collect(Collectors.toList());


        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(expected.size() == actual.size());

        Iterator<AsciiRow> expIterator = expected.stream().map(AsciiRow::new).iterator();
        Iterator<AsciiRow> actIterator = actual.iterator();

        while (expIterator.hasNext() && actIterator.hasNext()) {
            AsciiRow se = expIterator.next();
            AsciiRow sa = actIterator.next();
            System.out.println("sa = " + sa);
            assertEquals(se, sa);
        }
    }

    @Test
    public void testEmitAction() throws Exception {
        ActionDecl a = new ActionDecl(String.valueOf(0), "Check the power cable", "X");
        List<String> expectedA = Lists.newArrayList("*", a.getExpression(), String.valueOf(102), "*");
        ObservableList<String> b = FXCollections.observableArrayList(" , , , ,X, , , ".split("[,]"));
        List<String> expectedB = b;

        List<String> tmp = Stream.concat(expectedA.stream(), expectedB.stream()).collect(Collectors.toList());
        AsciiRow expected = new AsciiRow(tmp);

        AsciiRow actual = new Emitter().emitRow(102, Tuple.of(a, b));

        //Dump.dumpList1DItems("EMIT-ACTION", actual);
        System.out.println("actual = " + actual);


        assertEquals(expected, actual);

    }

    @Test
    public void testEmitHeader() throws Exception {
        List<String> rawDefinitions =
                Lists.newArrayList("Y;Y;Y;N", "Y;-;-;-", "-;-;Y;Y", "-;Y;-;-");
        ObservableList<ObservableList<String>> definitions = rawDefinitions.stream()
                .map(r -> FXCollections.observableArrayList(r.split(SPLIT_REGEX)))
                .collect(MoreCollectors.toObservableList());

        List<String> rawDeclarations =
                Lists.newArrayList("*;90 minutes;1;*", "*;Team A Winning;2;*", "*;Team B Winning;3;*", "*;Draw;4;*", "*;Keep Playing;5;*");
        ObservableList<ConditionDecl> declarations = rawDeclarations.stream().map(r -> {
            String s[] = r.split(SPLIT_REGEX);
            return new ConditionDecl(s[0], s[1], s[2]);
        }).collect(MoreCollectors.toObservableList());

        Tuple2<ObservableList<ConditionDecl>, ObservableList<ObservableList<String>>> input =
                Tuple.of(declarations, definitions);

        Stream<AsciiRow> listStream = new Emitter().emitHeader().apply(input);

        listStream.forEach(System.out::println);

    }
}