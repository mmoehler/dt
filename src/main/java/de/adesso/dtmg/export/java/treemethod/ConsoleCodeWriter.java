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

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Created by mmoehler on 20.08.16.
 */
public class ConsoleCodeWriter extends CodeWriter {
    @Override
    public Writer openSource(JPackage pkg, String fileName) throws IOException {
        return new PrintWriter(System.out);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        return null;
    }
}
