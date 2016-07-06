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

package de.adesso.dtmg.export.java.treeMethod;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import com.google.common.io.LineReader;
import de.adesso.dtmg.Dump;
import de.adesso.dtmg.common.builder.ObservableList2DBuilder;
import de.adesso.dtmg.export.java.treemethod.Node;
import de.adesso.dtmg.export.java.treemethod.TreeMethod;
import de.adesso.dtmg.functions.ObservableList2DFunctions;
import de.adesso.dtmg.functions.fixtures.ActionDeclTableViewModelListBuilder;
import de.adesso.dtmg.functions.fixtures.ConditionDeclTableViewModelListBuilder;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.model.Declaration;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.tuple.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hamcrest.CoreMatchers;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mmoehler on 02.07.16.
 */
public class TreeMethodTest {

    public static final String X = "X";
    public static final String DASH = "-";
    public static final String Y = "Y";
    public static final String N = "N";

    TreeMethod tm;

    @BeforeMethod
    public void setUp() throws Exception {
        tm = new TreeMethod();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        tm = null;
    }

    @Test
    public void testStep1_Non_Leaf() {

        final ObservableList<ObservableList<String>> x = ObservableList2DBuilder.observable2DOf(
                        "X,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,"
                        +"-,X,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,"
                        +"-,-,X,-,-,-,X,-,X,-,-,-,X,X,-,-,X,"
                        +"-,-,-,X,-,-,X,-,-,X,X,-,X,X,-,X,X,"
                        +"-,-,-,-,X,-,-,-,-,X,-,-,X,-,-,X,X,"
                        +"-,-,-,-,-,X,-,-,X,-,X,-,-,X,-,X,X,"
                        +"-,-,-,-,-,-,-,X,-,-,-,-,-,-,X,-,-,"
                        +"-,-,-,-,-,-,-,-,-,-,-,X,-,-,X,-,-"
        ).dim(8, 17).build();
        Dump.dumpTableItems("X",x);

        DtEntity e = DtEntityStub.createForActionDefinitions(x);
        final Tuple2<Boolean, Declaration> tuple2 = tm.step1(e);

        assertThat(tuple2._1(), equalTo(Boolean.FALSE));
        assertThat(tuple2._2(), CoreMatchers.nullValue());

        
    }

    @Test
    public void testStep1_Leaf() {

        ActionDeclTableViewModelListBuilder builder = new ActionDeclTableViewModelListBuilder();
        final ObservableList<ActionDeclTableViewModel> w =
                builder.addTableViewModelWithLfdNbr("A001").withExpression("x").withIndicators("Y,N").build();

        final ObservableList<ObservableList<String>> x = ObservableList2DBuilder.observable2DOf(
                "X,-,-,-,-,-,-,-"
        ).dim(8, 1).build();
        Dump.dumpTableItems("X",x);

        DtEntity e = DtEntityStub.createFor(w,x);
        final Tuple2<Boolean, Declaration> tuple2 = tm.step1(e);

        final ObservableList<String> expected = FXCollections.observableArrayList();
        expected.add("X");

        assertThat(tuple2._1(), equalTo(Boolean.TRUE));
        assertThat(tuple2._2(), equalTo(w.get(0).getModel()));


    }

    @Test
    public void testStep4_Split_Definitions() {

        final ObservableList<ObservableList<String>> x = ObservableList2DBuilder.observable2DOf(
                "X,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,"
                        +"-,X,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,"
                        +"-,-,X,-,-,-,X,-,X,-,-,-,X,X,-,-,X,"
                        +"-,-,-,X,-,-,X,-,-,X,X,-,X,X,-,X,X,"
                        +"-,-,-,-,X,-,-,-,-,X,-,-,X,-,-,X,X,"
                        +"-,-,-,-,-,X,-,-,X,-,X,-,-,X,-,X,X,"
                        +"-,-,-,-,-,-,-,X,-,-,-,-,-,-,X,-,-,"
                        +"-,-,-,-,-,-,-,-,-,-,-,X,-,-,X,-,-"
        ).dim(8, 17).build();
        Dump.dumpTableItems("X",x);

        DtEntity e = DtEntityStub.createForActionDefinitions(x);

        final Tuple2<DtEntity, DtEntity> step4 = tm.step4(e, 0);

        Dump.dumpTableItems("LEFT",ObservableList2DFunctions.transpose().apply(step4._1().getActionDefinitions()));
        Dump.dumpTableItems("RIGHT",ObservableList2DFunctions.transpose().apply(step4._2().getActionDefinitions()));
    }

    @Test
    public void testStep4_Split_Definitions_by_Function() {

        final ObservableList<ObservableList<String>> x = ObservableList2DBuilder.observable2DOf(
                "X,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,"
                        +"-,X,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,"
                        +"-,-,X,-,-,-,X,-,X,-,-,-,X,X,-,-,X,"
                        +"-,-,-,X,-,-,X,-,-,X,X,-,X,X,-,X,X,"
                        +"-,-,-,-,X,-,-,-,-,X,-,-,X,-,-,X,X,"
                        +"-,-,-,-,-,X,-,-,X,-,X,-,-,X,-,X,X,"
                        +"-,-,-,-,-,-,-,X,-,-,-,-,-,-,X,-,-,"
                        +"-,-,-,-,-,-,-,-,-,-,-,X,-,-,X,-,-"
        ).dim(8, 17).build();
        Dump.dumpTableItems("X",x);

        final Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> tuple2 = splitDefinitionsAt(x, 1);

        ObservableList2DBuilder.observable2DOf(
                "X,-,-,-,-,-,-,-"
        ).dim(1,8).build();

        // TODO Das Splitten ist noch voll BANANE!!


        Dump.dumpTableItems("LEFT",tuple2._1());
        Dump.dumpTableItems("RIGHT",tuple2._2());
    }


    public static Tuple2<ObservableList<ObservableList<String>>,ObservableList<ObservableList<String>>> splitDefinitionsAt(ObservableList<ObservableList<String>> l, int idx) {
        final ObservableList<ObservableList<String>> y = ObservableList2DFunctions.transpose().apply(l);
        final ObservableList<ObservableList<ObservableList<String>>> lists = HObservableLists.splitAt(y, 1);
        return Tuple.of(ObservableList2DFunctions.transpose().apply(lists.get(0)),ObservableList2DFunctions.transpose().apply(lists.get(1)));
    }

    @Test
    public void testBuildTree() throws Exception {
        DtEntity e = readDecisionTable();
        Node<Declaration> decisionTree = transform(e);

        for (Node<Declaration> node : decisionTree) {
            String indent = createIndent(node.getLevel());
            System.out.println(indent + node.getData());
        }
    }

    private static String createIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append(' ');
        }
        return sb.toString();
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

        Tuple4<Boolean, ConditionDeclTableViewModel, String, Integer> r2 = step3(e, r1._3());
        System.out.println("r2 = " + r2);
        System.out.println("step3 = Select input: '" + r2._2().expressionProperty().get() + "'");

        //tEntity, DtEntity> r3 =
                step4(e,r2._4());
    }


    public  Node<Declaration> transform(DtEntity dt) {
        final Tuple2<Boolean, ActionDeclTableViewModel> step1 = step1(dt);
        Node<Declaration> ret = null;
        if(step1._1()) {
            ret = new Node(step1.$2().getModel());
        } else {
            final Tuple3<Boolean, ActionDeclTableViewModel, Integer> step2 = step2(dt);
            final Tuple4<Boolean, ConditionDeclTableViewModel, String, Integer> step3 = step3(dt, step2._3());
            Node<Declaration> condition = new Node<>(step3._2().getModel());
            final Tuple2<DtEntity,DtEntity> step4 = step4(dt, step3._4());
            if(N.equals(step3._3())) {
                condition.left(transform(step4._1()));
                condition.right(transform(step4._2()));
            }
            ret = condition;
        }
        return ret;
    }

    /** Check the OR-decision table to see if it can be leaf node or condition node*/
    private Tuple2<Boolean,ActionDeclTableViewModel> step1(DtEntity e) {
        ObservableList<ObservableList<String>> actions = e.getActionDefinitions();

        Dump.dumpTableItems("ACTIONS", actions);

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

        Optional<Indexed<Integer>> max = StreamUtils.zipWithIndex(Arrays.stream(countActions)
                .boxed())
                .collect(Collectors.maxBy((a, b) -> a.getValue().intValue() - b.getValue().intValue()));

        if(max.isPresent()) {
            int idx = (int)max.get().getIndex();
            Tuple3<Boolean, ActionDeclTableViewModel, Integer> tuple3 = Tuple.of(true, adecl.get(idx), idx);
            System.out.println("tuple3 = " + tuple3);
            return tuple3;
        }
        return Tuple.of(false,null,null);
    }

    /** Find the maximum number of occurrence of the unique value of each condition */
    private Tuple4<Boolean,ConditionDeclTableViewModel, String, Integer> step3(DtEntity e, int index) {
        ObservableList<ObservableList<String>> actions = e.getActionDefinitions();

        // determine indices of X of the max action row
        List<Long> collectX = StreamUtils.zipWithIndex(actions.get(index).stream())
                .filter(s -> s.getValue().equals(X))
                .map(t -> t.getIndex())
                .collect(Collectors.toList());

        System.out.println("collectX = " + collectX);

        ObservableList<ObservableList<String>> conditions = ObservableList2DFunctions.transpose().apply(e.getConditionDefinitions());

        Dump.dumpTableItems("TRANSCONDEFS", conditions);

        List<ObservableList<String>> conditionsWithActions = collectX.stream().map(l -> conditions.get(l.intValue())).collect(Collectors.toList());
        System.out.println("conditionsWithActions = " + conditionsWithActions);

        //List<Integer>  =
        Integer[] countedY = conditionsWithActions.stream().map(c -> countingY(c))
                .reduce(newIntegerArray(conditions.get(0).size(),0), addLists);
        System.out.println("countedY = " + Arrays.toString(countedY));

        Integer[]  countedN = conditionsWithActions.stream().map(c -> countingN(c))
                .reduce(newIntegerArray(conditions.get(0).size(),0), addLists);
        System.out.println("countedN = " + Arrays.toString(countedN));

        Optional<Indexed<Integer>> max = Stream.concat(StreamUtils
                .zipWithIndex(Arrays.stream(countedY)), StreamUtils.zipWithIndex(Arrays.stream(countedN)))
                .collect(Collectors.maxBy((a, b) -> a.getValue().compareTo(b.getValue())));

        if(max.isPresent()) {
            int idx = (int)max.get().getIndex();
            return Tuple.of(true,e.getConditionDeclarations().get(idx),e.getConditionDefinitions().get(index).get(idx),idx);
        }

        return Tuple.of(false,null,null,null);
    }

    /** Separate the OR-decision table from the previous
     step into two sub-tables by grouping the rows that contain ‘0’
     into one table and rows that contain ‘1’ into another table. */
    private Tuple2<DtEntity,DtEntity> step4(DtEntity e, int idx) {

        int index = idx+1;

        final ObservableList<ConditionDeclTableViewModel> conditionDeclarations = e.getConditionDeclarations();

        final ObservableList<ObservableList<ConditionDeclTableViewModel>> splittedCDEC = HObservableLists.splitAt(conditionDeclarations, index);

        final ObservableList<ObservableList<String>> conditionDefinitions = e.getConditionDefinitions();
        final ObservableList<ObservableList<ObservableList<String>>> splittedCDEF = HObservableLists.splitAt(conditionDefinitions, index);

        splittedCDEF.forEach(t -> Dump.dumpTableItems("DEFNS",t));

        final ObservableList<ActionDeclTableViewModel> actionDeclarations = e.getActionDeclarations();
        final ObservableList<ObservableList<ActionDeclTableViewModel>> splittedADEC = HObservableLists.splitAt(actionDeclarations, index);

        final ObservableList<ObservableList<String>> actionDefinitions = e.getActionDefinitions();
        final ObservableList<ObservableList<ObservableList<String>>> splittedADEF = HObservableLists.splitAt(actionDefinitions, index);

        splittedADEF.forEach(t -> Dump.dumpTableItems("ADEFNS",t));

        return Tuple.of(
                new DtEntity(splittedCDEC.get(0),splittedCDEF.get(0),splittedADEC.get(0),splittedADEF.get(0)),
                new DtEntity(splittedCDEC.get(1),splittedCDEF.get(1),splittedADEC.get(1),splittedADEF.get(1))
        );

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

    /*

data Tree a = Branch (Tree a) (Tree a)
            | Leaf a
            | Empty
deriving Show


showTree :: Show a => Tree a -> String
showTree (Branch left right) = "(" ++ showTree left ++ ") <-> (" ++ showTree right ++ ")"
showTree (Leaf x) = show x
showTree Empty = "_"


rec :: [a] -> Tree a
rec []    = Empty
rec [x]   = Leaf x
rec xs    = Branch left right
      where left  = rec (take half xs)
            right = rec (drop half xs)
            half  = div (length xs) 2

test = showTree $ rec [1..7]



    */

}