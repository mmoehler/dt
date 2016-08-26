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

import java.util.function.UnaryOperator;

import static java.util.stream.IntStream.range;

/**
 * Created by moehler on 25.08.2016.
 */
public class MatrixFunctions {

    public static UnaryOperator<DtCell[][]> transpose() {
        return m -> {
            return range(0, m[0].length).mapToObj(r ->
                    range(0, m.length).mapToObj(c -> m[c][r]).toArray(DtCell[]::new)
            ).toArray(DtCell[][]::new);
        };
    }



}
