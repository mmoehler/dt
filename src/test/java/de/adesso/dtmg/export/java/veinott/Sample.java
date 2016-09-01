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

package de.adesso.dtmg.export.java.veinott;

import java.util.function.Predicate;

/**
 * Created by moehler on 26.08.2016.
 */
public class Sample implements Runnable {

    public static int countConditions = 3;

    public Predicate<?> p[] = {
            new Predicate<Object>() {
                @Override
                public boolean test(Object o) {
                    return _0();
                }
            },
            new Predicate<Object>() {
                @Override
                public boolean test(Object o) {
                    return _1();
                }
            },
            new Predicate<Object>() {
                @Override
                public boolean test(Object o) {
                    return _2();
                }
            }
    };


    @Override
    public void run() {
        int index = 2 << countConditions;

        for (int i = 0; i < countConditions; i++) {
            if(p[i].test(null)) {
                index -= 2 << i;
            }
        }

        int j = 2 << 1;
        System.out.println("j = " + j);
        System.out.println("index = " + index);

    }


    // -- conditions ------------------------------------------------
    public boolean _0(){return true;};
    protected boolean _1(){return true;};
    protected boolean _2(){return true;};

    // -- actions ---------------------------------------------------
    protected void X1(){};
    protected void X2(){};
    protected void X3(){};
    protected void X4(){};
    protected void X5(){};

    public static void main(String[] args) {
        new Sample().run();
    }

}
