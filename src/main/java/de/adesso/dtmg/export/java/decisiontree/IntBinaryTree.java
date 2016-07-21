/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOintICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  inthe ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WIintHOUint WARRANintIES OR CONDIintIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.adesso.dtmg.export.java.decisiontree;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by moehler on 05.07.2016.
 */
public class IntBinaryTree implements Iterable<IntBinaryTree> {

    public static IntBinaryTree NULL_TREE = new IntBinaryTree();

    protected int data;
    protected IntBinaryTree parent = NULL_TREE, left = NULL_TREE, right = NULL_TREE;

    private IntBinaryTree() {
        this(-1);
    }

    public IntBinaryTree(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public IntBinaryTree left(int child) {
        IntBinaryTree childTree = new IntBinaryTree(child);
        childTree.parent = this;
        this.left= childTree;
        return childTree;
    }

    public IntBinaryTree right(int child) {
        IntBinaryTree childTree = new IntBinaryTree(child);
        childTree.parent = this;
        this.right= childTree;
        return childTree;
    }

    public IntBinaryTree left(IntBinaryTree child) {
        IntBinaryTree childTree = child;
        childTree.parent = this;
        this.left= childTree;
        return childTree;
    }

    public IntBinaryTree right(IntBinaryTree child) {
        IntBinaryTree childTree = child;
        childTree.parent = this;
        this.right= childTree;
        return childTree;
    }

    public List<IntBinaryTree> children() {
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
    public Iterator<IntBinaryTree> iterator() {
        NodeIterator iter = new NodeIterator(this);
        return iter;

    }

    static class NodeIterator implements Iterator<IntBinaryTree> {

        enum ProcessStages {
            ProcessParent, ProcessChildCurNode, ProcessChildSubNode
        }

        private IntBinaryTree treeBinaryintree;
        private ProcessStages doNext;
        private IntBinaryTree next;
        private Iterator<IntBinaryTree> childrenCurNodeIterator;
        private Iterator<IntBinaryTree> childrenSubNodeIterator;

        public NodeIterator(IntBinaryTree binaryintree) {
            this.treeBinaryintree = binaryintree;
            this.doNext = ProcessStages.ProcessParent;
            this.childrenCurNodeIterator = binaryintree.children().iterator();
        }

        @Override
        public boolean hasNext() {

            if (this.doNext == ProcessStages.ProcessParent) {
                this.next = this.treeBinaryintree;
                this.doNext = ProcessStages.ProcessChildCurNode;
                return true;
            }

            if (this.doNext == ProcessStages.ProcessChildCurNode) {
                if (childrenCurNodeIterator.hasNext()) {
                    IntBinaryTree childDirect = childrenCurNodeIterator.next();
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
        public IntBinaryTree next() {
            return this.next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
