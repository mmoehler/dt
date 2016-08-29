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

import com.google.common.base.Strings;
import com.sun.codemodel.*;
import de.adesso.dtmg.export.java.ClassDescription;
import de.adesso.dtmg.export.java.GeneratorStrategy;
import de.adesso.dtmg.export.java.treemethod.TreeMethodConfiguration;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.ObservableList;

import javax.annotation.Generated;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mmoehler on 26.08.16.
 */
public class StraightScanCodeGenerator implements GeneratorStrategy<TreeMethodConfiguration> {

    public static final String VALUE = "value";
    public static final String TODO_METHOD_DOC = "TODO: Document this method!!";
    public static final String OTHERWISE = "otherwise";

    @Override
    public JCodeModel apply(TreeMethodConfiguration cfg) throws Exception {

        ClassDescription cd = checkNotNull(cfg.getClassDescription(), "Missing Configured Class Description");

        JCodeModel jCodeModel = new JCodeModel();
        JPackage jp = jCodeModel._package(cd.getPackagename());
        JDefinedClass jc = jp._class(JMod.PUBLIC | JMod.ABSTRACT, cd.getClassname());
        final JTypeVar jTypeVar01 = jc.generify("T");
        final JTypeVar jTypeVar02 = jc.generify("R");
        jc._implements(jCodeModel.ref(Function.class).narrow(jTypeVar01, jTypeVar02));
        jc.annotate(Generated.class)
                .param("value", getClass().getName())
                .param("date", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()));
        JDocComment jDocComment = jc.javadoc();
        jDocComment.add("Class Level Java Docs");

        DtEntity dtEntity = checkNotNull(cfg.getDecisionTable(), "Missing Configured Decisiontable");
        Map<String, JMethod> conditionStubs = emitConditionStubs(jCodeModel, jc, jTypeVar01, dtEntity.getConditionDeclarations());
        Map<String, JMethod> actionStubs = emitActionStubs(jCodeModel, jc, jTypeVar01, jTypeVar02, dtEntity.getActionDeclarations());
        cfg.stubs().putAll(conditionStubs);
        cfg.stubs().putAll(actionStubs);

        JMethod jmApply = emitApplyMethodDecl(jc, jTypeVar01, jTypeVar02);
        cfg.stack().push(jmApply.body());

        /**

         Function<DtEntity,DtNode> decomposer = cfg.useOptimizedDecomposition() ? new OptimizedDecomposer() : new StandardDecomposer();
         DtNode dtNode = decomposer.apply(dtEntity);
         dtNode.accept(new ApplyMethodEmitter(cfg));


         **/

        return jCodeModel;
    }

    private Map<String, JMethod> emitConditionStubs(JCodeModel jCodeModel, JDefinedClass jc, JTypeVar jTypeVar01, ObservableList<ConditionDeclTableViewModel> cdecls) {
        Map<String, JMethod> actions = cdecls.stream().map(decl -> {
            JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jCodeModel.BOOLEAN, decl.expressionProperty().get());
            JVar param = jMethod.param(jTypeVar01, VALUE);
            String jdoc = decl.documentationProperty().get();
            jdoc = (Strings.isNullOrEmpty(jdoc)) ? TODO_METHOD_DOC : jdoc;
            jMethod.javadoc().add(jdoc);
            jMethod.javadoc().addParam(param).add("A <code>T</code> as input of this method.");
            jMethod.javadoc().addReturn().add("boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>");
            return jMethod;
        }).collect(Collectors.toMap(JMethod::name, Function.identity()));
        return actions;
    }

    private Map<String, JMethod> emitActionStubs(JCodeModel jCodeModel, JDefinedClass jc, JTypeVar jTypeVar01, JTypeVar jTypeVar02, ObservableList<ActionDeclTableViewModel> adecls) {
        Map<String, JMethod> operations = adecls.stream().map(decl -> {
            JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jTypeVar02, decl.expressionProperty().get());
            JVar param = jMethod.param(jTypeVar01, VALUE);
            String jdoc = decl.documentationProperty().get();
            jdoc = (Strings.isNullOrEmpty(jdoc)) ? TODO_METHOD_DOC : jdoc;
            jMethod.javadoc().add(jdoc);
            jMethod.javadoc().addParam(param).add("A <code>T</code> as input of this method.");
            jMethod.javadoc().addReturn().add("R as the result of the processing.");
            return jMethod;
        }).collect(Collectors.toMap(JMethod::name, Function.identity()));
        final JMethod otherwise = emitOtherwise(jc, jTypeVar01, jTypeVar02);
        operations.put(otherwise.name(), otherwise);
        return operations;
    }

    private JMethod emitOtherwise(JDefinedClass jc, JTypeVar jTypeVar01, JTypeVar jTypeVar02) {
        JMethod jMethod = jc.method(JMod.PROTECTED | JMod.ABSTRACT, jTypeVar02, OTHERWISE);
        JVar param = jMethod.param(jTypeVar01, VALUE);
        String jdoc = "This operation can be used to implement the behaviour, which should be executed if no rule matches the given input.";
        jdoc = (Strings.isNullOrEmpty(jdoc)) ? TODO_METHOD_DOC : jdoc;
        jMethod.javadoc().add(jdoc);
        jMethod.javadoc().addParam(param).add("A <code>T</code> as input of this method.");
        jMethod.javadoc().addReturn().add("R as the result of the processing.");
        return jMethod;
    }

    private JMethod emitApplyMethodDecl(JDefinedClass jc, JTypeVar jTypeVar01, JTypeVar jTypeVar02) {
        String methodName = "apply";
        JMethod jmApply = jc.method(JMod.PUBLIC, jTypeVar02, methodName);
        JVar param = jmApply.param(jTypeVar01, VALUE);
        jmApply.annotate(Override.class);
        String jdoc = "This operation applies the rules of the implemented decision table on the given parameters.";
        jmApply.javadoc().add(jdoc);
        jmApply.javadoc().addParam(param).add("A <code>T</code> as input of this method.");
        jmApply.javadoc().addReturn().add("boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>");

        return jmApply;
    }


}
