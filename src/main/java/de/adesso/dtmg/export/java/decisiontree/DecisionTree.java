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

package de.adesso.dtmg.export.java.decisiontree;

import com.google.common.collect.Sets;
import de.adesso.dtmg.io.DtEntity;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by mmoehler on 16.07.16.
 */
public class DecisionTree implements Consumer<boolean[]> {

    private final Callable<Boolean>[] conditions;
    private final Runnable[] actions;
    private final IntBinaryTree tree;

    private DecisionTree(Builder builder) {
        actions = builder.actionSupplier.get();
        conditions = builder.conditionSupplier.get();
        tree = IntBinaryTreeFactory.createFrom(builder.decisionTable);
    }

    public IntBinaryTree getTree() {
        return tree;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void accept(boolean...qry) {
        IntBinaryTree t = tree;
        Set<Integer> seen = Sets.newHashSet();
        traverse(t,qry,seen);
    }

    private void traverse(IntBinaryTree t, boolean[] qry, Set<Integer> seen) {
        int pointer = t.getData();
        IntBinaryTree next;
        if (t.isLeaf()) {
            actions[pointer].run();
            next = t.parent.left == t ? t.parent.right : t.parent.left;
            next = (next.isLeaf() || seen.contains(next.data)) ? IntBinaryTree.NULL_TREE : next;
        } else {
            boolean flag;
            try {
                flag = conditions[pointer].call();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            seen.add(pointer);
            next = (flag) ? t.left : t.right;
        }
        if(next == IntBinaryTree.NULL_TREE) return;
        traverse(next,qry,seen);
    }

    public static final class Builder {
        private Supplier<Runnable[]> actionSupplier;
        private Supplier<Callable<Boolean>[]> conditionSupplier;
        private DtEntity decisionTable;

        private Builder() {
        }

        public Builder actionSupplier(Supplier<Runnable[]> val) {
            actionSupplier = val;
            return this;
        }

        public Builder conditionSupplier(Supplier<Callable<Boolean>[]> val) {
            conditionSupplier = val;
            return this;
        }

        public Builder decisionTable(DtEntity val) {
            decisionTable = val;
            return this;
        }

        public DecisionTree build() {
            return new DecisionTree(this);
        }
    }
}
