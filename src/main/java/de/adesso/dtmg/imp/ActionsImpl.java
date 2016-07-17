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

package de.adesso.dtmg.imp;

import java.util.Arrays;

/**
 * Created by mmoehler on 17.07.16.
 */
public class ActionsImpl implements Actions {

    private final Runnable all[] = {
            this::action000,
            this::action001,
            this::action002,
            this::action003,
            this::action004,
            this::action005,
            this::action006,
            this::action007
    };

    public ActionsImpl() {
    }

    @Override
    public void action000() {
        String s = "no action";
        System.out.println(s);
    }

    @Override
    public void action001() {
        String s = "new label";
        System.out.println(s);
    }

    @Override
    public void action002() {
        String s = "x=p";
        System.out.println(s);
    }

    @Override
    public void action003() {
        String s = "x=q";
        System.out.println(s);
    }

    @Override
    public void action004() {
        String s = "x=r";
        System.out.println(s);
    }

    @Override
    public void action005() {
        String s = "x=s";
        System.out.println(s);
    }

    @Override
    public void action006() {
        String s = "x=p+r";
        System.out.println(s);
    }

    @Override
    public void action007() {
        String s = "x=r+s";
        System.out.println(s);
    }

    @Override
    public Runnable[] get() {
        return all;
    }

    public static void main(String args[]) {
        ActionsImpl actions = new ActionsImpl();
        Arrays.stream(actions.all).forEach(s -> s.run());
    }

}
