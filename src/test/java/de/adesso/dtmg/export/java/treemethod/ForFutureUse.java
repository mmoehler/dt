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

package de.adesso.dtmg.export.java.treemethod;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import com.sun.codemodel.*;
import de.adesso.dtmg.util.Dump;
import de.adesso.dtmg.util.List2DBuilder;
import de.adesso.dtmg.util.List2DFunctions;
import de.adesso.dtmg.util.RandomDefinitions;
import org.testng.annotations.Test;

import javax.annotation.Generated;
import java.io.File;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by moehler on 08.06.2016.
 */
public class ForFutureUse {


    private static <T> List<List<T>> splitBySeparator(List<T> list, Predicate<? super T> predicate) {
        final List<List<T>> finalList = new ArrayList<>();
        int fromIndex = 0;
        int toIndex = 0;
        for (T elem : list) {
            if (predicate.test(elem)) {
                finalList.add(list.subList(fromIndex, toIndex));
                fromIndex = toIndex + 1;
            }
            toIndex++;
        }
        if (fromIndex != toIndex) {
            finalList.add(list.subList(fromIndex, toIndex));
        }
        return finalList;
    }

    static void createDecisionTree(List<List<String>> dt, DtNode top) {
/*
        if (top.getConditionIndex() >= dt.size() - 1) return;

        final List<Indexed<String>> theTrues = top.data.stream()
                .filter(i -> Pattern.matches("Y|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(top.conditionIndex + 1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        DtNode ifTrue = DtNode.newBuilder().data(theTrues).index(top.conditionIndex + 1).build();
        top.yes = ifTrue;
        createDecisionTree(dt, ifTrue);

        final List<Indexed<String>> theOthers = top.data.stream()
                .filter(i -> Pattern.matches("N|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(top.conditionIndex + 1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        DtNode ifFalse = DtNode.newBuilder().data(theOthers).index(top.conditionIndex + 1).build();
        top.no = ifFalse;
        createDecisionTree(dt, ifFalse);
*/
    }

    @Test
    public void testEmit() throws Exception {
        RandomDefinitions.Builder builder = RandomDefinitions
                .newBuilder().cols(5).rows(3).indicators(() -> new String[]{"Y", "N", "-"}).transpose();
        List<List<String>> definitions = builder.build().get();

        Dump.dumpTableItems("ORIGINAL DT", List2DFunctions.transpose(definitions));

        List<List<C>> dt = definitions.stream().map(l -> l.stream().map(C::from).collect(Collectors.toList())).collect(Collectors.toList());
        Dump.dumpTableItems("RANDOM DT", dt);

        final List<Indexed<List<C>>> collect = StreamUtils.zipWithIndex(dt.stream()).collect(Collectors.toList());

        final List<Indexed<List<C>>> collect1 = collect.stream().sorted((l, r) -> l.getValue().get(0).code - r.getValue().get(0).code).collect(Collectors.toList());

        collect1.forEach(e -> System.out.println(String.format("(%02d) -> %s", e.getIndex(), e.getValue())));
    }


    @Test
    public void testSplit() throws Exception {
        final List<List<String>> dt = List2DBuilder.matrixOf("-,Y,Y,N,N,Y,Y,Y,Y,Y,N,N").dim(3, 4).build();


        final List<String> list = dt.get(0);
        final List<Indexed<String>> indexeds = StreamUtils.zipWithIndex(list.stream()).collect(Collectors.toList());

        final List<Indexed<String>> theTrues = indexeds.stream()
                .filter(i -> Pattern.matches("Y|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        final List<Indexed<String>> theOthers = indexeds.stream()
                .filter(i -> Pattern.matches("N|-", i.getValue()))
                .map(j -> Indexed.index(j.getIndex(), dt.get(1).get((int) j.getIndex())))
                .collect(Collectors.toList());

        Dump.dumListWithIndexedValues("LEFT", theTrues);
        Dump.dumListWithIndexedValues("RIGHT", theOthers);


    }

    @Test
    public void testCodemodel() throws Exception {
        String packagename = "de.adesso.dtmg.export.java";
        JCodeModel jCodeModel = new JCodeModel();
        JPackage jp = jCodeModel._package(packagename);
        JDefinedClass jc = jp._class("Rules")._implements(Runnable.class);
        jc.annotate(Generated.class)
                .param("value", getClass().getName())
                .param("date", DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
        JDocComment jDocComment = jc.javadoc();
        jDocComment.add("Class Level Java Docs");


        JMethod jCondMeth00 = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jCodeModel.BOOLEAN, String.format("condition%02d", 0));
        JMethod jCondMeth01 = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jCodeModel.BOOLEAN, String.format("condition%02d", 1));
        JMethod jCondMeth02 = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jCodeModel.BOOLEAN, String.format("condition%02d", 2));

        String methodName = "run";
        JMethod jmRun = jc.method(JMod.PUBLIC, jCodeModel.VOID, methodName);

            /* Addign java doc for method */
        jmRun.javadoc().add("Method Level Java Docs");

        JBlock jmBody = jmRun.body();
        JConditional jConditional = jmBody._if(JExpr.invoke(
                jCondMeth00).
                not()
                .cand(
                        JExpr.invoke(jCondMeth01)
                )
                .cand(
                        JExpr.invoke(jCondMeth02)
                )
        );


        final File file = new File("./");
        jCodeModel.build(file);


    }

    @Test
    public void testCreateRuleCode() throws Exception {
        final List<List<String>> dt = List2DBuilder.matrixOf("Y,Y,N,N,N,-,-,Y,Y,N,Y,N,Y,N,-").dim(3, 5).transposed().build();

        Dump.dumpTableItems("DT TRANSPOSED", dt);

        // ... let the user define the package name
        String packagename = "de.adesso.dtmg.export.java";
        JCodeModel jCodeModel = new JCodeModel();
        JPackage jp = jCodeModel._package(packagename);
        JDefinedClass jc = jp._class(JMod.PUBLIC | JMod.ABSTRACT, "AbstractRules");
        final JTypeVar jTypeVar01 = jc.generify("I");
        final JTypeVar jTypeVar02 = jc.generify("O");
        jc.annotate(Generated.class)
                .param("value", getClass().getName())
                .param("date", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()));
        JDocComment jDocComment = jc.javadoc();
        jDocComment.add("Class Level Java Docs");


        Map<String, JMethod> conditions = IntStream.range(0, dt.get(0).size())
                .mapToObj(i -> {
                    JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jCodeModel.BOOLEAN, String.format("condition%02d", i));
                    jMethod.param(jTypeVar01, "input");
                    jMethod.javadoc().add(String.format("Documentation of the %d. condition", i + 1));
                    jMethod.javadoc().addReturn().add("boolean <code>true</code> if the condition requirements full filled, otherwise <code>false</code>");
                    return jMethod;
                })
                .collect(Collectors.toMap(JMethod::name, Function.identity()));

        conditions.entrySet().forEach(e -> System.out.println(e.getKey() + " => " + e.getValue()));

        Map<String, JMethod> actions = IntStream.range(0, dt.size())
                .mapToObj(i -> {
                    JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jTypeVar02, String.format("actions%02d", i));
                    jMethod.param(jTypeVar01, "input");
                    jMethod.javadoc().add(String.format("Documentation of the %d. action block", i + 1));
                    return jMethod;
                })
                .collect(Collectors.toMap(JMethod::name, Function.identity()));


        String methodName = "apply";
        JMethod jmRun = jc.method(JMod.PUBLIC, jTypeVar02, methodName);
        jmRun.param(jTypeVar01, "input");

            /* Addign java doc for method */
        jmRun.javadoc().add("Method Level Java Docs");

        JBlock jmBody = jmRun.body();

        JDoLoop doLoop = jmBody._do(JExpr.lit(true));

        JBlock loopBody = doLoop.body();

        JConditional flag[] = {null};
        IntStream.range(0, dt.size()).forEach(i -> {
                    JExpression[] invocation = {null};

                    Optional<JExpression> expression = StreamUtils.zipWithIndex(dt.get(i).stream())
                            .filter(f -> !"-".equals(f.getValue()))
                            .map(s -> {
                                invocation[0] = JExpr.invoke(String.format("condition%02d", s.getIndex())).arg(JExpr.direct("input"));

                                if ("N".equals(s.getValue())) {
                                    invocation[0] = invocation[0].not();
                                }
                                return invocation[0];
                            }).reduce((l, r) -> l.cand(r));

                    JExpression expr = expression.orElseThrow(() -> new IllegalStateException("Missing Conditional Expression!"));

                    flag[0] = loopBody._if(expr);
                    JBlock thenBody = flag[0]._then();


                    thenBody._return(JExpr.invoke(String.format("actions%02d", i)).arg(JExpr.direct("input")));

                }
        );
        //Users/mmoehler/checkouts/dt/src/test/java/de/adesso/dtmg/export/java/ifthen
        final File file = new File("./src/test/java");
        //file.mkdirs();
        jCodeModel.build(file);


    }


    @Test
    public void testCreateDecisionTree() throws Exception {
        /*
        //final List<List<String>> dt = List2DBuilder.matrixOf("-,Y,Y,N,N,Y,Y,Y,Y,Y,N,N").dim(3, 4).build();

        final List<List<String>> dt = List2DBuilder.matrixOf("Y,Y,N,N,N,-,-,Y,Y,N,Y,N,Y,N,-").dim(3, 5).build();

        Dump.dumpTableItems("DT TRANSPOSED", dt);

        final List<String> list = dt.get(0);
        final List<Indexed<String>> indexeds = StreamUtils.zipWithIndex(list.stream()).collect(Collectors.toList());
        final DtNode top = DtNode.newBuilder().index(0).data(indexeds).build();
        createDecisionTree(dt, top);

        System.out.println("public abstract class Rules implements Runnable {");

        System.out.println("public void run() {");

        Visitor<DtNode> v = new SimpleVisitor();
        TreeSet<String> actionNames = Sets.newTreeSet();
        TreeSet<String> conditionNames = Sets.newTreeSet();
        top.accept(v, actionNames, conditionNames);

        System.out.println("}");

        String conTpl = "protected abstract boolean %s();";
        conditionNames.forEach(n -> System.out.println(String.format(conTpl, n)));
        String actTpl = "protected abstract void %s();";
        actionNames.forEach(n -> System.out.println(String.format(actTpl, n)));

        System.out.println("}");
        */
    }


    enum C implements Identifiable {
        Y(0, "Y"), N(1, "N"), H(2, "-");
        final int code;
        final String id;

        C(int code, String id) {
            this.code = code;
            this.id = id;
        }

        static C from(String s) {
            final Optional<C> i1 = Arrays.stream(values()).filter((i) -> s.equals(i.id)).findFirst();
            return i1.orElseThrow(() -> new IllegalStateException(s + "??"));
        }

        public int code() {
            return code;
        }

        public String id() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }

    }

    enum A implements Identifiable {
        X("X"), H("-");
        final String id;

        A(String id) {
            this.id = id;
        }

        static A from(String s) {
            final Optional<A> i1 = Arrays.stream(values()).filter((i) -> s.equals(i.id)).findFirst();
            return i1.orElseThrow(() -> new IllegalStateException(s + "??"));
        }

        public String id() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }

    }

    interface Identifiable {
        String id();
    }

}