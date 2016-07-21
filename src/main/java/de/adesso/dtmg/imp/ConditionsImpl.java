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

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Created by mmoehler on 17.07.16.
 */
public class ConditionsImpl implements Conditions {
    private boolean[] query;

    private final Callable all[] = {
            this::condition000,
            this::condition001,
            this::condition002,
            this::condition003,
            this::condition004
    };

    public ConditionsImpl() {
        super();
    }

    @Override
    public boolean condition000() {
        String s = "x";
        System.out.println(s);
        return query[0];
    }

    @Override
    public boolean condition001() {
        String s = "p";
        System.out.println(s);
        return query[1];
    }

    @Override
    public boolean condition002() {
        String s = "q";
        System.out.println(s);
        return query[2];
    }

    @Override
    public boolean condition003() {
        String s = "r";
        System.out.println(s);
        return query[3];
    }

    @Override
    public boolean condition004() {
        String s = "s";
        System.out.println(s);
        return query[4];
    }

    @Override
    public Conditions setExpected(boolean... flags) {
        this.query = flags;
        return this;
    }

    @Override
    public Supplier<Callable<Boolean>[]> supplier() {
        return this;
    }

    @Override
    public Callable<Boolean>[] get() {
        return all;
    }
}
