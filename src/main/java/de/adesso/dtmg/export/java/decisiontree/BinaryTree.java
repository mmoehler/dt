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

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by moehler on 05.07.2016.
 */
public class BinaryTree<T> implements Iterable<BinaryTree<T>>, Consumer<Boolean[]>{

    public static BinaryTree NULL_TREE = new BinaryTree();

    protected T data;
    protected BinaryTree<T> parent = NULL_TREE, left = NULL_TREE, right = NULL_TREE;

    private BinaryTree() {
        this(null);
    }

    public BinaryTree(T data) {
        this.data = data;
    }

    @Override
    public void accept(Boolean[] booleen) {

    }

    public T getData() {
        return data;
    }

    public BinaryTree<T> left(T child) {
        BinaryTree<T> childBinaryTree = new BinaryTree<T>(child);
        childBinaryTree.parent = this;
        this.left= childBinaryTree;
        return childBinaryTree;
    }

    public BinaryTree<T> right(T child) {
        BinaryTree<T> childBinaryTree = new BinaryTree<T>(child);
        childBinaryTree.parent = this;
        this.right= childBinaryTree;
        return childBinaryTree;
    }

    public BinaryTree<T> left(BinaryTree<T> child) {
        BinaryTree<T> childBinaryTree = child;
        childBinaryTree.parent = this;
        this.left= childBinaryTree;
        return childBinaryTree;
    }

    public BinaryTree<T> right(BinaryTree<T> child) {
        BinaryTree<T> childBinaryTree = child;
        childBinaryTree.parent = this;
        this.right= childBinaryTree;
        return childBinaryTree;
    }

    public List<BinaryTree<T>> children() {
        return (isLeaf())
                ? Collections.emptyList()
                : (this.left == NULL_TREE)
                ? Lists.newArrayList(this.right)
                : (this.right == NULL_TREE)
                ? Lists.newArrayList(this.left)
                :  Lists.newArrayList(this.left, this.right);
    }

    public boolean isRoot() {
        return parent == NULL_TREE;
    }

    public boolean isLeaf() {
        return this.left == NULL_TREE && this.right == NULL_TREE;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    @Override
    public Iterator<BinaryTree<T>> iterator() {
        NodeIterator<T> iter = new NodeIterator<T>(this);
        return iter;

    }

    static class NodeIterator<T> implements Iterator<BinaryTree<T>> {

        enum ProcessStages {
            ProcessParent, ProcessChildCurNode, ProcessChildSubNode
        }

        private BinaryTree<T> treeBinaryTree;
        private ProcessStages doNext;
        private BinaryTree<T> next;
        private Iterator<BinaryTree<T>> childrenCurNodeIterator;
        private Iterator<BinaryTree<T>> childrenSubNodeIterator;

        public NodeIterator(BinaryTree<T> binaryTree) {
            this.treeBinaryTree = binaryTree;
            this.doNext = ProcessStages.ProcessParent;
            this.childrenCurNodeIterator = binaryTree.children().iterator();
        }

        @Override
        public boolean hasNext() {

            if (this.doNext == ProcessStages.ProcessParent) {
                this.next = this.treeBinaryTree;
                this.doNext = ProcessStages.ProcessChildCurNode;
                return true;
            }

            if (this.doNext == ProcessStages.ProcessChildCurNode) {
                if (childrenCurNodeIterator.hasNext()) {
                    BinaryTree<T> childDirect = childrenCurNodeIterator.next();
                    childrenSubNodeIterator = childDirect.iterator();
                    this.doNext = ProcessStages.ProcessChildSubNode;
                    return hasNext();
                }

                else {
                    this.doNext = null;
                    return false;
                }
            }

            if (this.doNext == ProcessStages.ProcessChildSubNode) {
                if (childrenSubNodeIterator.hasNext()) {
                    this.next = childrenSubNodeIterator.next();
                    return true;
                }
                else {
                    this.next = null;
                    this.doNext = ProcessStages.ProcessChildCurNode;
                    return hasNext();
                }
            }
            return false;
        }

        @Override
        public BinaryTree<T> next() {
            return this.next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
