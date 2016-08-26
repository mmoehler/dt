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

package de.adesso.dtmg.export.java.straightscan;

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.base.Strings;
import com.sun.codemodel.*;
import de.adesso.dtmg.exception.LambdaExceptionUtil;
import de.adesso.dtmg.export.java.ClassDescription;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.model.DecisionTable;
import de.adesso.dtmg.util.ObservableList2DFunctions;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import javafx.collections.ObservableList;

import javax.annotation.Generated;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by moehler on 08.06.2016.
 */
public class StraightScan implements LambdaExceptionUtil.BiFunction_WithExceptions<DecisionTable, ClassDescription, JCodeModel, Exception> {

    @Override
    public JCodeModel apply(DecisionTable dt, ClassDescription cd) throws Exception {

        ObservableList<ObservableList<String>> condefs = ObservableList2DFunctions.transpose().apply(dt.getConditionDefs());
        ObservableList<ObservableList<String>> actdefs = ObservableList2DFunctions.transpose().apply(dt.getActionDefs());

        JCodeModel jCodeModel = new JCodeModel();
        JPackage jp = jCodeModel._package(cd.getPackagename());
        JDefinedClass jc = jp._class(JMod.PUBLIC | JMod.ABSTRACT, cd.getClassname());
        final JTypeVar jTypeVar01 = jc.generify("I");
        final JTypeVar jTypeVar02 = jc.generify("O");
        jc.annotate(Generated.class)
                .param("value", getClass().getName())
                .param("date", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()));
        JDocComment jDocComment = jc.javadoc();
        jDocComment.add("TODO Class Level Java Docs");

        final Map<String, Tuple2<JMethod, JVar>> conditionStubs = emitConditionStubs(dt.getConditionDecls(), condefs, jCodeModel, jc, jTypeVar01);
        final Map<String, Tuple2<JMethod, JVar>> actionStubs = emitActionStubs(dt.getActionDecls(), actdefs, jc, jTypeVar01, jTypeVar02);
        final Map<String, Tuple2<JMethod, JVar>> reducerStubs = emitReducerStubs(jCodeModel, jc, jTypeVar01, jTypeVar02);
        emitApplyMethod(condefs, actdefs, jCodeModel, jc, jTypeVar01, jTypeVar02, conditionStubs, actionStubs, reducerStubs);
        return jCodeModel;
    }

    private void emitApplyMethod(
            ObservableList<ObservableList<String>> condefs,
            ObservableList<ObservableList<String>> actdefs,
            JCodeModel jm,
            JDefinedClass jc,
            JTypeVar jTypeVar01,
            JTypeVar jTypeVar02,
            Map<String, Tuple2<JMethod, JVar>> conditionStubs,
            Map<String, Tuple2<JMethod, JVar>> actionStubs,
            Map<String, Tuple2<JMethod, JVar>> reducerStubs
    ) {
        String methodName = "apply";
        JMethod jmRun = jc.method(JMod.PUBLIC, jTypeVar02, methodName);
        jmRun.param(jTypeVar01, "input");
        jmRun.javadoc().add("Method Level Java Docs");
        JBlock jmBody = jmRun.body();

        final JClass listOfTypeVar02 = jm.ref(List.class).narrow(jTypeVar02);
        final JClass linkedListOfTypeVar02 = jm.ref(LinkedList.class).narrow(jTypeVar02);
        final JVar resultBuffer = jmBody.decl(listOfTypeVar02, "results", JExpr._new(linkedListOfTypeVar02));

        JConditional flag[] = {null};
        IntStream.range(0, condefs.size()).forEach(i -> {
            JExpression[] invocation = {null};

            final String condition = StreamUtils.zipWithIndex(condefs.get(i).stream())
                    .filter(f -> !"-".equals(f.getValue()))
                    .map(s -> String.format("%scondition%02d(input)","N".equals(s.getValue()) ? "!" : "", s.getIndex()))
                    .reduce("", (l, r) -> Strings.isNullOrEmpty(l) ? r : l + " && " + r);

            flag[0] = jmBody._if(JExpr.direct(condition));
            JBlock thenBody = flag[0]._then();

            StreamUtils.zipWithIndex(actdefs.get(i).stream())
                    .filter(f -> !"-".equals(f.getValue()))
                    .map(s -> {
                        Tuple2<JMethod, JVar> info = actionStubs.get(String.format("actions%02d", s.getIndex()));
                        //final JVar result$02d = thenBody.decl(jTypeVar02, String.format("result%02d", s.getIndex()), JExpr.invoke(info._1()).arg(info._2()));
                        final JInvocation listAdd = resultBuffer.invoke("add").arg(JExpr.invoke(info._1()).arg(info._2()));
                        return listAdd;
                    })
                    .forEach(thenBody::add);
        });
        final JInvocation reduceResults = JExpr.invoke(reducerStubs.get("reduceResults")._1()).arg(resultBuffer);
        jmBody._return(reduceResults);
    }

    private Map<String, Tuple2<JMethod, JVar>> emitReducerStubs(JCodeModel jm, JDefinedClass jc, JTypeVar jTypeVar01, JTypeVar jTypeVar02) {
        return IntStream.range(0, 1)
                .mapToObj(i -> {
                    JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jTypeVar02, "reduceResults");

                    final JClass listOfTypeVar02 = jm.ref(List.class).narrow(jTypeVar02);
                    final JVar param = jMethod.param(listOfTypeVar02, "results");
                    final JDocComment javadoc = jMethod.javadoc();
                    javadoc.add("Reduces the different action outputs and returns the result of the reduction");
                    javadoc.addParam(param).add("an array of O as action operation results to reduce.");
                    javadoc.addReturn().add("an Oas result of the reduction process.");
                    return Tuple.of(jMethod, param);
                })
                .collect(Collectors.toMap(t -> t._1().name(), Function.identity()));
    }

    private Map<String, Tuple2<JMethod, JVar>> emitActionStubs(ObservableList<ActionDecl> actdecls, ObservableList<ObservableList<String>> actdefs, JDefinedClass jc, JTypeVar jTypeVar01, JTypeVar jTypeVar02) {
        final Iterator<ActionDecl> actDeclsIterator = actdecls.iterator();
        return IntStream.range(0, actdefs.get(0).size())
                .mapToObj(i -> {
                    JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jTypeVar02, String.format("actions%02d", i));
                    final JVar param = jMethod.param(jTypeVar01, "input");
                    jMethod.javadoc().add(actDeclsIterator.next().getExpression());
                    return Tuple.of(jMethod, param);
                })
                .collect(Collectors.toMap(t -> t._1().name(), Function.identity()));
    }

    private Map<String, Tuple2<JMethod, JVar>> emitConditionStubs(ObservableList<ConditionDecl> condecls, ObservableList<ObservableList<String>> condefs, JCodeModel jCodeModel, JDefinedClass jc, JTypeVar jTypeVar01) {
        final Iterator<ConditionDecl> conDeclsIterator = condecls.iterator();
        return IntStream.range(0, condefs.get(0).size())
                .mapToObj(i -> {
                    JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jCodeModel.BOOLEAN, String.format("condition%02d", i));
                    final JVar param = jMethod.param(jTypeVar01, "input");
                    jMethod.javadoc().add(conDeclsIterator.next().getExpression());
                    jMethod.javadoc().addReturn().add("boolean <code>true</code> if the condition requirements full filled, otherwise <code>false</code>");
                    return Tuple.of(jMethod, param);
                })
                .collect(Collectors.toMap(t -> t._1().name(), Function.identity()));
    }
}