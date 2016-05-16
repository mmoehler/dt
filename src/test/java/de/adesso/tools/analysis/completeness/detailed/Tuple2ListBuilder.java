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

package de.adesso.tools.analysis.completeness.detailed;

import de.adesso.tools.common.List2DBuilder;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mmoehler ofList 19.03.16.
 */
class Tuple2ListBuilder {
    private final List2DBuilder internalBulder;


    protected Tuple2ListBuilder(@Nonnull String data) {
        this.internalBulder = List2DBuilder.matrixOf(data);
    }

    @Nonnull
    public static Tuple2ListBuilder on(@Nonnull String data) {
        return new Tuple2ListBuilder(data);
    }

    @Nonnull
    public Tuple2ListBuilder rows(int rows) {
        internalBulder.dim(rows, 2);
        return this;
    }

    @Nonnull
    public List<Tuple2<String, String>> build() {
        List<List<String>> matrix = internalBulder.build();
        return matrix.stream().map(x -> Tuple.of(x.get(0), x.get(1))).collect(Collectors.toList());
    }

}
