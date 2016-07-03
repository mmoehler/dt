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

package de.adesso.dtmg.export.java;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import com.google.common.io.LineReader;
import de.adesso.dtmg.Dump;
import de.adesso.dtmg.functions.ObservableList2DFunctions;
import de.adesso.dtmg.functions.fixtures.ActionDeclTableViewModelListBuilder;
import de.adesso.dtmg.functions.fixtures.ConditionDeclTableViewModelListBuilder;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.adesso.dtmg.util.tuple.Tuple3;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 02.07.16.
 */
public class TreeMethodTest {

    public static final String X = "X";
    public static final String DASH = "-";
    public static final String Y = "Y";
    public static final String N = "N";

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testLoad() throws Exception {
        DtEntity e = readDecisionTable();

        // step 1
        Tuple2<Boolean,ActionDeclTableViewModel> r0 = step1(e);

        System.out.println("r0 = " + r0);
        System.out.println("step1 = " + (r0._1() ? "This is leaf node" : "This is condition node"));

        Tuple3<Boolean,ActionDeclTableViewModel,Integer> r1 = step2(e);
        System.out.println("step2 = Select action: '" + r1._2().expressionProperty().get() + "'");

        Tuple3<Boolean, ConditionDeclTableViewModel, Integer> r2 = step3(e, r1._3());
        System.out.println("step3 = Select input: '" + r2._2().expressionProperty().get() + "'");


    }

    /** Check the OR-decision table to see if it can be leaf node or condition node*/
    private Tuple2<Boolean,ActionDeclTableViewModel> step1(DtEntity e) {
        ObservableList<ObservableList<String>> actions = e.getActionDefinitions();
        ObservableList<ActionDeclTableViewModel> adecl = e.getActionDeclarations();
        Optional<ObservableList<String>> optional = actions.stream()
                .filter(l -> l.stream()
                        .filter(el -> el.equals(X)).count() == l.size()).findFirst();
        return (optional.isPresent())
                ? Tuple.of(true,adecl.get(adecl.indexOf(optional.get())))
                : Tuple.of(false,null);

    }

    /** Find the action that has maximum number of occurrence in OR-decision table */
    private Tuple3<Boolean,ActionDeclTableViewModel,Integer> step2(DtEntity e) {
        ObservableList<ObservableList<String>> actions = e.getActionDefinitions();
        ObservableList<ObservableList<String>> conditions = ObservableList2DFunctions.transpose().apply(e.getConditionDefinitions());
        ObservableList<ActionDeclTableViewModel> adecl = e.getActionDeclarations();

        int countActions[] = new int[actions.size()];
        int row = 0;
        for (; row < actions.size(); row++) {
            countActions[row] = 0;
            for (int col = 0; col < actions.get(0).size(); col++) {
                if (actions.get(row).get(col).equals(X)) {
                    long dashCount = conditions.get(col).stream().filter(x -> DASH.equals(x)).count();
                    countActions[row]+=Math.pow(2,dashCount);
                }
            }
        }

        Optional<Indexed<Integer>> max = StreamUtils.zipWithIndex(Arrays.stream(countActions).boxed()).collect(Collectors.maxBy((a, b) -> a.getValue().intValue() - b.getValue().intValue()));

        if(max.isPresent()) {
            int idx = (int)max.get().getIndex();
            Tuple3<Boolean, ActionDeclTableViewModel, Integer> tuple3 = Tuple.of(true, adecl.get(idx), idx);
            System.out.println("tuple3 = " + tuple3);
            return tuple3;
        }
        return Tuple.of(false,null,null);
    }

    /** Find the maximum number of occurrence of the unique value of each condition */
    private Tuple3<Boolean,ConditionDeclTableViewModel, Integer> step3(DtEntity e, int index) {
        ObservableList<ObservableList<String>> actions = e.getActionDefinitions();

        // determine indices of X of the max action row
        List<Long> collectX = StreamUtils.zipWithIndex(actions.get(index).stream())
                .filter(s -> s.getValue().equals(X))
                .map(t -> t.getIndex())
                .collect(Collectors.toList());

        System.out.println("collectX = " + collectX);

        ObservableList<ObservableList<String>> conditions = ObservableList2DFunctions.transpose().apply(e.getConditionDefinitions());

        List<ObservableList<String>> conditionsWithActions = collectX.stream().map(l -> conditions.get(l.intValue())).collect(Collectors.toList());
        System.out.println("conditionsWithActions = " + conditionsWithActions);

        //List<Integer>  =
        Integer[] countedY = conditionsWithActions.stream().map(c -> countingY(c)).reduce(newIntegerArray(conditions.get(0).size(),0), addLists);
        System.out.println("countedY = " + Arrays.toString(countedY));
        Integer[]  countedN = conditionsWithActions.stream().map(c -> countingN(c)).reduce(newIntegerArray(conditions.get(0).size(),0), addLists);
        System.out.println("countedN = " + Arrays.toString(countedN));


        Optional<Indexed<Integer>> max = Stream.concat(StreamUtils.zipWithIndex(Arrays.stream(countedY)), StreamUtils.zipWithIndex(Arrays.stream(countedN)))
                .collect(Collectors.maxBy((a, b) -> a.getValue().compareTo(b.getValue())));

        if(max.isPresent()) {
            int idx = (int)max.get().getIndex();
            return Tuple.of(true,e.getConditionDeclarations().get(idx),idx);
        }


        return Tuple.of(false,null,null);
    }

    /** Separate the OR-decision table from the previous
     step into two sub-tables by grouping the rows that contain ‘0’
     into one table and rows that contain ‘1’ into another table. */
    private Tuple3<Boolean,ConditionDeclTableViewModel, Integer> step4(DtEntity e, int index) {

        return Tuple.of(false,null,null);


    }


    public Integer[] newIntegerArray(int size, Integer filler) {
        Integer ret[] = new Integer[size];
        Arrays.fill(ret,0);
        return ret;
    }

    private BinaryOperator<Integer[]> addLists = (a, b) -> StreamUtils.zip(Arrays.stream(a), Arrays.stream(b),
            (l, r) -> new Integer(l + r)).toArray(Integer[]::new);

    private Integer[] countingY(ObservableList < String > c) {
        Integer[] list = new Integer[c.size()];
        Arrays.fill(list,0);
        for (int i = 0; i < c.size(); i++) {
            if(Y.equals(c.get(i))) {
                long dashes = c.stream().filter(y -> DASH.equals(y)).count();
                list[i]+=(int)Math.pow(2,dashes);
            }
        }
        return list;
    }

    private Integer[] countingN(ObservableList<String> c) {
        Integer[] list = new Integer[c.size()];
        Arrays.fill(list,0);
        for (int i = 0; i < c.size(); i++) {
            if(N.equals(c.get(i))) {
                long dashes = c.stream().filter(y -> DASH.equals(y)).count();
                list[i]+=(int)Math.pow(2,dashes);
            }
        }
        return list;
    }


    protected DtEntity readDecisionTable() throws IOException {
        File file = new File("test.dtx");
        FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        CharBuffer cb = Charset.forName("utf8").decode(buffer);
        LineReader lr = new LineReader(cb);


        String l = lr.readLine();
        // -- Build ConditionDEcls (5:x,p,q,r,s)
        String[] s0 = l.split(":");
        int i = Integer.parseInt(s0[0]);
        ObservableList<ConditionDeclTableViewModel> conditionDecls = FXCollections.observableArrayList();
        String[] s1 = s0[1].split(",");
        ConditionDeclTableViewModelListBuilder builder = new ConditionDeclTableViewModelListBuilder();
        for (int j = 0; j < s1.length; j++) {
            builder.addTableViewModelWithLfdNbr(String.format("C%02d", j))
                    .withExpression(s1[j])
                    .withIndicators("Y,N");
        }
        conditionDecls.addAll(builder.build());

        // -- DUMP
        conditionDecls.stream().forEach(System.out::println);

        // Read ConditionDefns

        ObservableList<ObservableList<String>> conditionDefns = FXCollections.observableArrayList();
        for (int j = 0; j < i; j++) {
            conditionDefns.add(FXCollections.observableArrayList());
        }
        for (; ; ) {
            l = lr.readLine(); // 10000
            if (l.matches("[2-9].*")) break;
            String[] s2 = l.split("");
            Iterator<ObservableList<String>> it = conditionDefns.iterator();
            Arrays.stream(s2)
                    .map(x -> ("1".equals(x) ? "Y" : ("0".equals(x)) ? "N" : x))
                    .forEach(y -> it.next().add(y));
        }
        ;

        Dump.dumpTableItems("CONDDEFS", conditionDefns);

        s0 = l.split(":");
        i = Integer.parseInt(s0[0]);
        ObservableList<ActionDeclTableViewModel> actionDecls = FXCollections.observableArrayList();
        s1 = s0[1].split(",");
        ActionDeclTableViewModelListBuilder abuilder = new ActionDeclTableViewModelListBuilder();
        for (int j = 0; j < s1.length; j++) {
            abuilder.addTableViewModelWithLfdNbr(String.format("A%02d", j))
                    .withExpression(s1[j])
                    .withIndicators("X");
        }

        actionDecls.addAll(abuilder.build());

        // -- DUMP
        actionDecls.stream().forEach(System.out::println);

        // Read ActionDefns

        ObservableList<ObservableList<String>> actionDefns = FXCollections.observableArrayList();
        for (int j = 0; j < i; j++) {
            actionDefns.add(FXCollections.observableArrayList());
        }
        for (; ; ) {
            l = lr.readLine(); // 10000
            if (null == l) break;
            String[] s2 = l.split("");
            Iterator<ObservableList<String>> it = actionDefns.iterator();
            Arrays.stream(s2)
                    .map(x -> ("1".equals(x) ? "X" : (".".equals(x)) ? " " : x))
                    .forEach(y -> it.next().add(y));
        }
        ;

        Dump.dumpTableItems("ACTDEFS", actionDefns);

        DtEntity ret = new DtEntity(conditionDecls, conditionDefns, actionDecls, actionDefns);
        return ret;
    }

}