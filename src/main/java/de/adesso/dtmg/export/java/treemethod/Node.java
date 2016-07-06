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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by moehler on 05.07.2016.
 */
public class Node<T> implements Iterable<Node<T>>{

    public static Node NULL_NODE = new Node();

    protected T data;
    protected Node<T> parent = NULL_NODE;
    protected final LinkedList<Node<T>> children = new LinkedList<>();

    private Node() {
        this(null);
    }

    public Node(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public Node<T> left(T child) {
        Node<T> childNode = new Node<T>(child);
        childNode.parent = this;
        this.children.addFirst(childNode);
        return childNode;
    }

    public Node<T> right(T child) {
        Node<T> childNode = new Node<T>(child);
        childNode.parent = this;
        this.children.addLast(childNode);
        return childNode;
    }

    public Node<T> left(Node<T> child) {
        Node<T> childNode = child;
        childNode.parent = this;
        this.children.addFirst(childNode);
        return childNode;
    }

    public Node<T> right(Node<T> child) {
        Node<T> childNode = child;
        childNode.parent = this;
        this.children.addLast(childNode);
        return childNode;
    }


    public boolean isRoot() {
        return parent == NULL_NODE;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    @Override
    public Iterator<Node<T>> iterator() {
        NodeIterator<T> iter = new NodeIterator<T>(this);
        return iter;

    }

    static class NodeIterator<T> implements Iterator<Node<T>> {

        enum ProcessStages {
            ProcessParent, ProcessChildCurNode, ProcessChildSubNode
        }

        private Node<T> treeNode;
        private ProcessStages doNext;
        private Node<T> next;
        private Iterator<Node<T>> childrenCurNodeIterator;
        private Iterator<Node<T>> childrenSubNodeIterator;

        public NodeIterator(Node<T> node) {
            this.treeNode = node;
            this.doNext = ProcessStages.ProcessParent;
            this.childrenCurNodeIterator = node.children.iterator();
        }

        @Override
        public boolean hasNext() {

            if (this.doNext == ProcessStages.ProcessParent) {
                this.next = this.treeNode;
                this.doNext = ProcessStages.ProcessChildCurNode;
                return true;
            }

            if (this.doNext == ProcessStages.ProcessChildCurNode) {
                if (childrenCurNodeIterator.hasNext()) {
                    Node<T> childDirect = childrenCurNodeIterator.next();
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
        public Node<T> next() {
            return this.next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
