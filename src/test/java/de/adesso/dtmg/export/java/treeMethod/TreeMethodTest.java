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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.LineReader;
import de.adesso.dtmg.Dump;
import de.adesso.dtmg.export.java.decisiontree.BinaryTree;
import de.adesso.dtmg.export.java.decisiontree.DecisionTree;
import de.adesso.dtmg.export.java.decisiontree.IntBinaryTree;
import de.adesso.dtmg.export.java.decisiontree.IntBinaryTreeFactory;
import de.adesso.dtmg.functions.ObservableList2DFunctions;
import de.adesso.dtmg.functions.fixtures.ActionDeclTableViewModelListBuilder;
import de.adesso.dtmg.functions.fixtures.ConditionDeclTableViewModelListBuilder;
import de.adesso.dtmg.imp.ActionsImpl;
import de.adesso.dtmg.imp.ConditionsImpl;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.model.Declaration;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.adesso.dtmg.util.tuple.Tuple3;
import de.adesso.dtmg.util.tuple.Tuple4;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.Map;
import java.util.stream.Collectors;

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

    IntBinaryTreeFactory tm;

    @BeforeMethod
    public void setUp() throws Exception {
        tm = new IntBinaryTreeFactory();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        tm = null;
    }

    @Test
    public void testStep1_Non_Leaf() throws Exception {

        DtEntity e = readDecisionTable();

        final ObservableList<ObservableList<String>> x = e.getActionDefinitions();

        Dump.dumpTableItems("X",x);

        final Tuple2<Boolean, Declaration> tuple2 = tm.step1(e);

        System.out.println(">>> tuple2 = " + tuple2);

        assertThat(tuple2._1(), equalTo(Boolean.FALSE));
        assertThat(tuple2._2(), CoreMatchers.nullValue());

    }

    @Test
    public void testStep1_Leaf_1() throws Exception {

        DtEntity e = readDecisionTable();

        final ObservableList<ObservableList<String>> x = e.getActionDefinitions();
        final ObservableList<ActionDeclTableViewModel> y = e.getActionDeclarations();

        final ObservableList<ObservableList<String>> _x = FXCollections.observableArrayList();
        _x.add(x.get(0));

        final ObservableList<ActionDeclTableViewModel> _y = FXCollections.observableArrayList();
        _y.add(y.get(0));

        DtEntity _e = DtEntityStub.createForActions(_y,_x);

        Dump.dumpTableItems("X",_x);

        final Tuple2<Boolean, Declaration> tuple2 = tm.step1(_e);

        System.out.println(">>> tuple2 = " + tuple2);

        assertThat(tuple2._1(), equalTo(Boolean.TRUE));
        assertThat(tuple2._2(), equalTo(_y.get(0).getModel()));

    }

    @Test
    public void testStep2() throws Exception {

        DtEntity e = readDecisionTable();

        final ObservableList<ObservableList<String>> x = e.getActionDefinitions();
        Dump.dumpTableItems("X",x);

        final ObservableList<ObservableList<String>> y = e.getConditionDefinitions();
        Dump.dumpTableItems("Y",y);

        Tuple3<Boolean, ActionDeclTableViewModel, Integer> tuple3 = tm.step2(e);
        System.out.println("tuple3 = " + tuple3);

        assertThat(tuple3._1(), equalTo(Boolean.TRUE));
        assertThat(tuple3._2().expressionProperty().get(), equalTo("no action"));
        assertThat(tuple3._3(), equalTo(0));
    }

    @Test
    public void testStep3() throws Exception {

        DtEntity e = readDecisionTable();

        final ObservableList<ObservableList<String>> x = e.getActionDefinitions();
        Dump.dumpTableItems("X",x);

        final ObservableList<ObservableList<String>> y = e.getConditionDefinitions();
        Dump.dumpTableItems("Y",y);

        final Tuple4<Boolean, ConditionDeclTableViewModel, Integer, Integer> tuple4 = tm.step3(e, 0);
        System.out.println("tuple4 = " + tuple4);


        assertThat(tuple4._1(), equalTo(Boolean.TRUE));
        assertThat(tuple4._2().expressionProperty().get(), equalTo("x"));
        assertThat(tuple4._3(), equalTo(0));
        assertThat(tuple4._4(), equalTo(0));

    }


    @Test
    public void testStep4() throws Exception {

        DtEntity e = readDecisionTable();

        final ObservableList<ObservableList<String>> x = e.getActionDefinitions();
        Dump.dumpTableItems("X",x);

        final ObservableList<ObservableList<String>> y = e.getConditionDefinitions();
        Dump.dumpTableItems("Y",y);

        final Tuple2<DtEntity, DtEntity> tuple2 = tm.step4(e, 0);
        System.out.println("tuple2 = " + tuple2);

        System.out.println("====================================================================");

        Dump.dumpTableItems("L_CDF", tuple2._1().getConditionDefinitions());
        Dump.dumpTableItems("L_ADF", tuple2._1().getActionDefinitions());

        System.out.println("====================================================================");

        Dump.dumpTableItems("R_CDF", tuple2._2().getConditionDefinitions());
        Dump.dumpTableItems("R_ADF", tuple2._2().getActionDefinitions());

        /*
        assertThat(tuple4._1(), equalTo(Boolean.TRUE));
        assertThat(tuple4._2().expressionProperty().getVar(), equalTo("x"));
        assertThat(tuple4._3(), equalTo("N"));
        assertThat(tuple4._4(), equalTo(0));
        */

    }

    @Test
    public void testStep1_L_Step4() throws Exception {

        DtEntity e = readDecisionTable();

        final ObservableList<ObservableList<String>> x = e.getActionDefinitions();
        Dump.dumpTableItems("X",x);

        final ObservableList<ObservableList<String>> y = e.getConditionDefinitions();
        Dump.dumpTableItems("Y",y);

        final Tuple2<DtEntity, DtEntity> tuple2 = tm.step4(e, 0);
        System.out.println("tuple2 = " + tuple2);


        System.out.println("====================================================================");

        Dump.dumpTableItems("L_CDF", tuple2._1().getConditionDefinitions());
        Dump.dumpTableItems("L_ADF", tuple2._1().getActionDefinitions());

        System.out.println("====================================================================");

        Dump.dumpTableItems("R_CDF", tuple2._2().getConditionDefinitions());
        Dump.dumpTableItems("R_ADF", tuple2._2().getActionDefinitions());


        final Tuple2<Boolean, Declaration> tuple21 = tm.step1(tuple2._1());

        System.out.println("tuple21 = " + tuple21);

        final Tuple2<Boolean, Declaration> tuple22 = tm.step1(tuple2._2());

        System.out.println("tuple22 = " + tuple22);

    }

    @Test
    public void testDecisionTree() throws Exception {
        DtEntity e = readDecisionTable();
        boolean b[] = {true,true,false,false,true};
        DecisionTree tree = DecisionTree.newBuilder()
                .conditionSupplier(new ConditionsImpl().setExpected(b).supplier())
                .actionSupplier(new ActionsImpl())
                .decisionTable(e)
                .build();

        for (IntBinaryTree t : tree.getTree()) {
            String indent = createIndent(t.getLevel());
            String s = String.format("%s%02d.%d",(t.isLeaf()) ? "A" : "C",t.getData(), t.hashCode());
            System.out.println(indent + s);
        }


        System.out.println("######################################");
        tree.accept(b);
        System.out.println("######################################");

    }



    @Test
    public void testBuildTree() throws Exception {
        DtEntity e = readDecisionTable();
        IntBinaryTreeFactory tm = new IntBinaryTreeFactory();
        BinaryTree<Declaration> decisionTree = transform(e, tm);

        for (BinaryTree<Declaration> binaryTree : decisionTree) {
            String indent = createIndent(binaryTree.getLevel());
            String s = String.format("%d[%s]@%s", getIndex(binaryTree), binaryTree.getData().getExpression(), Integer.toHexString(binaryTree.hashCode()));
            System.out.println(indent + s);
        }
    }

    static int getIndex(BinaryTree<Declaration> t) {
        String strNr = t.getData().getLfdNr();
        strNr = strNr.substring(1,strNr.length());
        return Integer.parseInt(strNr);
    }

    String nodeName(BinaryTree<Declaration> binaryTree) {
        String s = String.format("%s[%s]@%s", binaryTree.getData().getLfdNr(), binaryTree.getData().getExpression(), Integer.toHexString(binaryTree.hashCode()));
        return s;
    }

    @Test
    public void testBuildGraphvizTree() throws Exception {
        DtEntity e = readDecisionTable();
        IntBinaryTreeFactory tm = new IntBinaryTreeFactory();
        BinaryTree<Declaration> decisionTree = transform(e, tm);
        int i=0;
        BiMap<Object,Object> map = HashBiMap.create();

        for (BinaryTree<Declaration> binaryTree : decisionTree) {
            map.put(nodeName(binaryTree), i++);
        }


        System.out.println("digraph d {");

        for (Map.Entry<Object,Object> n : map.entrySet()) {
            String s = String.format("%s [label=\"%s\"]", n.getValue(), n.getKey());
            System.out.println(s);
        }

        for (BinaryTree<Declaration> binaryTree : decisionTree) {
            if (!binaryTree.isLeaf()) {
                List<Object> list = binaryTree.children().stream().map(l -> map.get(nodeName(l))).collect(Collectors.toList());
                for (Object o : list) {
                    String s = String.format("%s -> %s", map.get(nodeName(binaryTree)), String.valueOf(o));
                    System.out.println(s);
                }
            }
        }
        System.out.println("}");
    }

    private static String createIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("     ");
        }
        return sb.toString();
    }


    public TreeMethodTest() {
        super();
    }

    public BinaryTree<Declaration> transform(DtEntity dt, IntBinaryTreeFactory tm) {

        final Tuple2<Boolean, Declaration> step1 = tm.step1(dt);
        BinaryTree<Declaration> ret = null;
        if(step1._1()) {
            ret = new BinaryTree(step1._2());
        } else {
            final Tuple3<Boolean, ActionDeclTableViewModel, Integer> step2 = tm.step2(dt);
            final Tuple4<Boolean, ConditionDeclTableViewModel, Integer, Integer> step3 = tm.step3(dt, step2._3());
            BinaryTree<Declaration> condition = new BinaryTree<>(step3._2().getModel());
            final Tuple2<DtEntity,DtEntity> step4 = tm.step4(dt, step3._3());
            condition.left(transform(step4._1(),tm));
            condition.right(transform(step4._2(),tm));
            ret = condition;
        }
        return ret;
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


        final ObservableList<ObservableList<String>> _conditionDefns = ObservableList2DFunctions.transpose().apply(conditionDefns);
        final ObservableList<ObservableList<String>> _actionDefns = ObservableList2DFunctions.transpose().apply(actionDefns);

        DtEntity ret = new DtEntity(conditionDecls, _conditionDefns, actionDecls, _actionDefns, new SimpleBooleanProperty(false));
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