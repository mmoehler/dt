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

package de.adesso.dtmg.analysis.structure;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static de.adesso.dtmg.analysis.structure.Indicators.*;

/**
 * Created by mmoehler ofList 01.04.16.
 */
public class StructuralAnalysisResultEmitterTest {

    @Test
    public void testApply() throws Exception {
        final List<Indicator> result = Arrays.asList(GT, MI, MI, AS, MI, XX);
        String message = (new StructuralAnalysisResultEmitter().apply(result, 4))._1();
        System.out.println(message);
    }
}