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

/**
 * Created by mmoehler on 15.07.16.
 */
public class Sample {
    public static final boolean F = false;
    public static final boolean T = true;
    static boolean x, p, q, r, s;

    public static void doAction(String s) {
        System.out.println(s);
    }

    public static void applyTable(boolean _x, boolean _p, boolean _q, boolean _r, boolean _s) {
        x = _x;
        p = _p;
        q = _q;
        r = _r;
        s = _s;
        String s1 = String.format("[%s,%s,%s,%s,%s]", x, p, q, r, s);
        System.out.println(s1);
        applyTable();
    }


    public static void applyTable() {
        if (x) {
            if (q) {
                doAction("x=q");
            } else {
                if (p) {
                    if (r) {
                        doAction("x=p+r");
                    } else {
                        doAction("x=p");
                    }
                } else {
                    if (r) {
                        if (s) {
                            doAction("x=r+s");
                        } else {
                            doAction("x=r");
                        }
                    } else {
                        if (s) {
                            doAction("x=s");
                        } else {
                            doAction("new label");
                        }
                    }
                }
            }
        } else {
            doAction("no action");
        }
    }

    public static void main(String args[]) {
        applyTable(F, T, F, T, F);
        System.out.println("-------------------------");
        applyTable(T, F, F, F, F);
        System.out.println("-------------------------");
        applyTable(T, T, F, F, F);
        System.out.println("-------------------------");
        applyTable(T, F, T, F, F);
        System.out.println("-------------------------");
        applyTable(T, F, F, T, F);
        System.out.println("-------------------------");
        applyTable(T, T, T, T, F);

    }

}
