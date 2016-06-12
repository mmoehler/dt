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

import com.codepoetics.protonpack.StreamUtils;
import com.sun.codemodel.*;
import de.adesso.dtmg.exception.LambdaExceptionUtil;
import de.adesso.dtmg.functions.ObservableList2DFunctions;
import de.adesso.dtmg.model.DecisionTable;
import javafx.collections.ObservableList;

import javax.annotation.Generated;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by moehler on 08.06.2016.
 */
public class StraigtScan implements LambdaExceptionUtil.BiFunction_WithExceptions<DecisionTable, ClassDescription, JCodeModel, Exception> {

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

        emitConditionStubs(condefs, jCodeModel, jc, jTypeVar01);
        emitActionStubs(actdefs, jc, jTypeVar01, jTypeVar02);
        emitApplyMethod(condefs, jc, jTypeVar01, jTypeVar02);
        return jCodeModel;
    }

    private void emitApplyMethod(ObservableList<ObservableList<String>> condefs, JDefinedClass jc, JTypeVar jTypeVar01, JTypeVar jTypeVar02) {
        String methodName = "apply";
        JMethod jmRun = jc.method(JMod.PUBLIC, jTypeVar02, methodName);
        jmRun.param(jTypeVar01, "input");
        jmRun.javadoc().add("Method Level Java Docs");
        JBlock jmBody = jmRun.body();
        JDoLoop doLoop = jmBody._do(JExpr.lit(true));
        JBlock loopBody = doLoop.body();

        JConditional flag[] = {null};
        IntStream.range(0, condefs.size()).forEach(i -> {
            JExpression[] invocation = {null};

            Optional<JExpression> expression = StreamUtils.zipWithIndex(condefs.get(i).stream())
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
        });
    }

    private void emitActionStubs(ObservableList<ObservableList<String>> actdefs, JDefinedClass jc, JTypeVar jTypeVar01, JTypeVar jTypeVar02) {
        Map<String, JMethod> actions = IntStream.range(0, actdefs.size())
                .mapToObj(i -> {
                    JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jTypeVar02, String.format("actions%02d", i));
                    jMethod.param(jTypeVar01, "input");
                    jMethod.javadoc().add(String.format("Documentation of the %d. action block", i + 1));
                    return jMethod;
                })
                .collect(Collectors.toMap(JMethod::name, Function.identity()));
    }

    private void emitConditionStubs(ObservableList<ObservableList<String>> condefs, JCodeModel jCodeModel, JDefinedClass jc, JTypeVar jTypeVar01) {
        Map<String, JMethod> conditions = IntStream.range(0, condefs.get(0).size())
                .mapToObj(i -> {
                    JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jCodeModel.BOOLEAN, String.format("condition%02d", i));
                    jMethod.param(jTypeVar01, "input");
                    jMethod.javadoc().add(String.format("Documentation of the %d. condition", i + 1));
                    jMethod.javadoc().addReturn().add("boolean <code>true</code> if the condition requirements full filled, otherwise <code>false</code>");
                    return jMethod;
                })
                .collect(Collectors.toMap(JMethod::name, Function.identity()));
    }


}