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

package de.adesso.tools;

import de.adesso.tools.functions.Formatter;

import java.io.PrintWriter;

/**
 * Simple Dump Utility with user defined formatter
 * Created by moehler on 02.03.2016.
 */
public class Dumper<T> {

    private Formatter<T> formatter;
    private T what;

    public static <X> Dumper<X> newDumper() {
        return new Dumper<>();
    }

    public Dumper<T> on(T what) {
        this.what = what;
        return this;
    }

    public Dumper<T> using(Formatter<T> formatter) {
        this.formatter = formatter;
        return this;
    }

    public void printOn(PrintWriter out) {
        out.println(formatter.apply(this.what));
    }
}
