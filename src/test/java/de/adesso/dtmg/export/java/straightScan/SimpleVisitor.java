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

package de.adesso.dtmg.export.java.straightScan;

import com.codepoetics.protonpack.Indexed;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by mmoehler on 10.06.16.
 */
public class SimpleVisitor implements Visitor<DtNode> {
    @Override
    public void visit(DtNode visitable, Object...args) {

        Set<String> aset = (Set<String>)args[0];
        Set<String> cset = (Set<String>)args[1];

        printIF(visitable, cset);

        if(null != visitable.yes)
        visitable.yes.accept(this,args);
        else printAction(visitable, true, aset);

        printELSE();

        if(null != visitable.no)
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
        final String cname = String.format("condition%02d",visitable.index);
        set.add(cname);
        System.out.println(String.format("if(%s()) {", cname));

    }

    private void printAction(DtNode visitable, boolean flag, Set<String> set) {
        Predicate<Indexed<String>> p = (flag)
                ? (i) -> i.getValue().equals("Y")
                : (i) -> i.getValue().equals("N");

        Optional<Indexed<String>> first = visitable.data.stream().filter(p).findFirst();

        String s = first.isPresent()
                ? String.format("action%02d", first.get().getIndex())
                : ("otherwise");

        set.add(s);

        System.out.println(s + "();");


    }

    private void printOtherwise() {
        System.out.println("otherwise();");
    }

}
