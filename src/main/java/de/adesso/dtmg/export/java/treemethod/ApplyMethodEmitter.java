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

import com.google.common.collect.Lists;
import com.sun.codemodel.*;
import de.adesso.dtmg.export.java.Visitor;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.ObservableList2DFunctions;
import javafx.collections.ObservableList;

import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mmoehler on 20
 * .08.16.
 */
public class ApplyMethodEmitter implements Visitor<DtNode> {

    public static final String VALUE = "value";
    public static final String OTHERWISE = "otherwise";

    private final TreeMethodConfiguration cfg;
    private List<List<String>> actionIndex;
    private Deque<JStatement> stack;
    private List<String> cset;

    public ApplyMethodEmitter(TreeMethodConfiguration cfg) {
        this.cfg = cfg;

        checkNotNull(this.cfg, "Missing expected TreeMethodConfiguration item!");
        DtEntity dtEntity = checkNotNull(cfg.getDecisionTable(),"Missing Decisiontable for processing!!");
        stack = cfg.stack();

        ObservableList<ConditionDeclTableViewModel> conditionDecls = checkNotNull(dtEntity.getConditionDeclarations(),"Missing Condition Declarations");
        cset = conditionDecls.stream()
                .map(c -> c.expressionProperty().get())
                .collect(Collectors.toList());

        ObservableList<ActionDeclTableViewModel> actionDecls = checkNotNull(dtEntity.getActionDeclarations(),"Missing Action Declarations");
        List<String> aset = actionDecls.stream()
                .map(c -> c.expressionProperty().get())
                .collect(Collectors.toList());

        ObservableList<ObservableList<String>> actionDefns = checkNotNull(dtEntity.getActionDefinitions(),"Missing Action Definitions");

        actionIndex = ObservableList2DFunctions.transpose()
                .apply(actionDefns).stream()
                .map(l -> IntStream
                        .range(0, l.size())
                        .filter(i -> "X".equals(l.get(i)))
                        .mapToObj(i -> aset.get(i))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @Override
    public void visit(DtNode visitable, Object... args) {

        // ... first handle don't care
        if(visitable.isDontCare()) {
            final int ruleIndex = visitable.data.get(0).get(0).col();
            actionIndex.get(ruleIndex).stream()
                    .map(s -> JExpr.invoke(cfg.stub(s)).arg(JExpr.ref("value")))
                    .collect(Collectors.toList())
                    .forEach(a -> ((JBlock)stack.peek()).add(a));
            return;
        }

        stack.push(((JBlock)stack.peek())._if(emitIFCondition(visitable, cset, cfg)));
        stack.push(((JConditional)stack.peek())._then());

        if(null != visitable.yes && !visitable.isDontCare()) {
            visitable.yes.accept(this, args);
        } else {
            emitActions(visitable, true, actionIndex, cfg).forEach(a -> ((JBlock)stack.peek()).add(a));
        }

        stack.pop();
        stack.push(((JConditional)stack.peek())._else());

        if(null != visitable.no && !visitable.isDontCare()) {
            visitable.no.accept(this, args);
        } else {
            emitActions(visitable, false, actionIndex, cfg).forEach(a -> ((JBlock)stack.peek()).add(a));
        }

        stack.pop();
        stack.pop();

    }

    private List<JInvocation> emitActions(DtNode visitable, boolean flag, List<List<String>> actionIndex, TreeMethodConfiguration cfg) {
        Predicate<DtCell> p = (flag)
                ? (i) -> i.typeOf(DtCellType.Y)
                : (i) -> i.typeOf(DtCellType.N);

        Optional<DtCell> first = visitable.data.stream().flatMap(a -> a.stream()).filter(p).findFirst();
        int ruleIndex = first.isPresent() ? first.get().col() : -1;

        List<JInvocation> otherwise = Lists.newArrayList(JExpr.invoke(cfg.stub(OTHERWISE)).arg(JExpr.ref(VALUE)));

        return (ruleIndex<0) ? otherwise : actionIndex.get(ruleIndex).stream()
                .map(s -> JExpr.invoke(cfg.stub(s)).arg(JExpr.ref(VALUE)))
                .collect(Collectors.toList());
    }

    private JInvocation emitIFCondition(DtNode visitable, List<String> cset, TreeMethodConfiguration cfg) {
        final String cname = cset.get(visitable.getConditionIndex());
        return JExpr.invoke(cfg.stub(cname)).arg(JExpr.ref(VALUE));

    }

}
