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

package de.adesso.tools.functions.chained.third;

/**
 * Created by moehler on 12.05.2016.
 */
public class Main {
    public static void main(String[] args) {
        final Root300 root = Root300.newBuilder()
                .withName("100")
                .withChild310()
                    .withName("110")
                .done()
                .withChild320()
                    .withName("120")
                .done()
                .build();

        System.out.println("root = " + root);

        final Child320 child320 = Child320.newBuilder()
                .withName("KNUDEL")
                .build();

        System.out.println("child320 = " + child320);

    }
}
