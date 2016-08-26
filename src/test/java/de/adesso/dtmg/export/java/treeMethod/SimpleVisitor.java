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

package de.adesso.dtmg.export.java.treeMethod;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by mmoehler on 10.06.16.
 */
public class SimpleVisitor implements Visitor<DtNode> {

    private static String[] N = {"X1","X2", "X3", "X4"};

    @Override
    public void visit(DtNode visitable, Object...args) {

        Set<String> aset = (Set<String>)args[0];
        Set<String> cset = (Set<String>)args[1];

        printIF(visitable, cset);

        if(null != visitable.yes && ! visitable.yes.isDontCare())
        visitable.yes.accept(this,args);
        else printAction(visitable, true, aset);

        printELSE();

        if(null != visitable.no && ! visitable.no.isDontCare())
        visitable.no.accept(this, args);
        else printAction(visitable, false, aset);
        printENDIF();
    }

    private void printENDIF() {
        System.out.println("}");
    }

    private void printELSE() {
        System.out.println("} else {");
    }

    private void printIF(DtNode visitable, Set<String> set) {
        final String cname = String.format("_%d",visitable.getConditionIndex());
        set.add(cname);
        System.out.println(String.format("if(%s()) {", cname));

    }

    private void printAction(DtNode visitable, boolean flag, Set<String> set) {
        Predicate<DtCell> p = (flag)
                ? (i) -> i.typeOf(DtCellType.Y)
                : (i) -> i.typeOf(DtCellType.N);

        Optional<DtCell> first = visitable.data.get(0).stream().filter(p).findFirst();

        String s = first.isPresent()
                ? String.format("%s", N[first.get().col()])
                : ("X5");

        set.add(s);

        System.out.println(s + "();");


    }

    private void printOtherwise() {
        System.out.println("otherwise();");
    }

}
