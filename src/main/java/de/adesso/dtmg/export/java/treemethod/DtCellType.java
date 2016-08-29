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

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by moehler on 25.08.2016.
 */
public enum DtCellType {
    Y("Y", 1), N("N", 2), I("-", 4);

    final String code;
    final int weight;

    DtCellType(String code, int weight) {
        this.code = code;
        this.weight = weight;
    }

    public static Optional<DtCellType> lookup(String code) {
        return Arrays.stream(values()).filter(s -> code.equals(s.code)).findFirst();
    }
}
