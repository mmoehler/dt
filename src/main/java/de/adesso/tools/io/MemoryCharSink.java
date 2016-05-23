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

package de.adesso.tools.io;

import com.google.common.io.CharSink;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by moehler on 23.05.2016.
 */
public class MemoryCharSink extends CharSink {
    private final StringBuilder buffer = new StringBuilder();

    public MemoryCharSink() {
    }

    @Override
    public Writer openStream() throws IOException {
        return CharStreams.asWriter(buffer);
    }

    public String getString() {
        return String.valueOf(buffer);
    }

}
