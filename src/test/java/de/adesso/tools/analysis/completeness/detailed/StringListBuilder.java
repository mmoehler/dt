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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mmoehler on 19.03.16.
 */
public class StringListBuilder {
    public final static String pattern = "[; ,]";
    private final String data;

    public static StringListBuilder on(String data) {
        return new StringListBuilder(data);
    }

    private StringListBuilder(@Nonnull String data) {
        this.data = data;
    }

    @Nonnull
    public List<String> build() {
        List<String> result = Collections.emptyList();
        if(null != data) {
            final String[] rawData = data.split(pattern);
            result = Arrays.stream(rawData).collect(Collectors.toList());
        }
        return result;
    }

}
