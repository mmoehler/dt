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

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.sun.codemodel.*;
import de.adesso.dtmg.export.java.treemethod.TreeMethodConfiguration;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.ObservableList2DFunctions;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import javafx.collections.ObservableList;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mmoehler on 20
 * .08.16.
 */
public class ApplyMethodEmitter implements BiConsumer<TreeMethodConfiguration, JVar> {

    public static final String VALUE = "value";
    public static final String OTHERWISE = "otherwise";


    private Iterator<List<Tuple2<Boolean, JMethod>>> makeConditionIndex(TreeMethodConfiguration cfg) {
        ObservableList<ConditionDeclTableViewModel> conditionDecls = cfg.getDecisionTable().getConditionDeclarations();
        List<String> cset = conditionDecls.stream()
                .map(c -> c.expressionProperty().get())
                .collect(Collectors.toList());

        ObservableList<ObservableList<String>> conditionDefns = cfg.getDecisionTable().getConditionDefinitions();
        List<List<Tuple2<Boolean, JMethod>>> index = ObservableList2DFunctions.transpose()
                .apply(conditionDefns).stream()
                .map(l -> IntStream.range(0, l.size())
                        .filter(i -> !"-".equals(l.get(i)))
                        .mapToObj(i -> {
                            boolean branch = ("Y".equals(l.get(i)));
                            return Tuple.of(branch, cfg.stub(cset.get(i)));
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        return Iterators.unmodifiableIterator(index.iterator());
    }

    private Iterator<List<JMethod>> makeActionIndex(TreeMethodConfiguration cfg) {
        ObservableList<ActionDeclTableViewModel> actionDecls = cfg.getDecisionTable().getActionDeclarations();
        List<String> aset = actionDecls.stream()
                .map(c -> c.expressionProperty().get())
                .collect(Collectors.toList());

        ObservableList<ObservableList<String>> actionDefns = cfg.getDecisionTable().getActionDefinitions();
        List<List<JMethod>> index = ObservableList2DFunctions.transpose()
                .apply(actionDefns).stream()
                .map(l -> IntStream
                        .range(0, l.size())
                        .filter(i -> "X".equals(l.get(i)))
                        .mapToObj(i -> cfg.stub(aset.get(i)))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        return Iterators.unmodifiableIterator(index.iterator());
    }


    @Override
    public void accept(TreeMethodConfiguration cfg, JVar result) {
        checkNotNull(cfg, "Missing expected TreeMethodConfiguration item!");
        Deque<JStatement> stack = cfg.stack();
        Iterator<List<Tuple2<Boolean, JMethod>>> conditionIndex = makeConditionIndex(cfg);
        Iterator<List<JMethod>> actionIndex = makeActionIndex(cfg);
        JBlock jBlock = (JBlock) stack.peek();
        while (conditionIndex.hasNext() && actionIndex.hasNext()) {
            JBlock __then = jBlock._if(emitLogicalExpression(conditionIndex.next()))._then();
            emitActionExpressions(actionIndex.next()).forEach(i -> __then.add(i));
            __then._return(result);
        }
        jBlock._return(result);
    }

    /**
     * <code>if(C0 && C1 && ... && Cn) { A0();A1(); ... ;An(); }</code>
     *
     * @param visitable
     * @param args
     * @param next
     */

    private JExpression emitLogicalExpression(List<Tuple2<Boolean, JMethod>> conditionOps) {
        List<String> expressions = conditionOps.stream().map(t -> {
            String ret = String.format("%s(%s)", t._2().name(), VALUE);
            return (t._1()) ? ret : '!'+ret;
        }).collect(Collectors.toList());
        if(!expressions.isEmpty()) {
            String joined = Joiner.on("\n&& ").skipNulls().join(expressions);
            return JExpr.direct(joined);
        }
        throw new IllegalStateException("IMPLEMENTATION-ERROR: Missing expected condition(s)!!");
    }

    private List<JStatement> emitActionExpressions(List<JMethod> actionOps) {
        return actionOps.stream().map(t -> JExpr.invoke(t).arg(JExpr.ref(VALUE))).collect(Collectors.toList());
    }


}
