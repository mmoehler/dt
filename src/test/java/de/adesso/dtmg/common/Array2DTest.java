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

package de.adesso.dtmg.common;

import de.adesso.dtmg.Dump;
import de.adesso.dtmg.common.builder.Array2D;
import org.testng.annotations.Test;

/**
 * Created by moehler on 18.05.2016.
 */
public class Array2DTest {

    @Test
    public void testSet() throws Exception {
        final Array2D array2D = Array2D.newBuilder()
                .fillWith("-")
                .dimension()
                .rows(4)
                .columns(2)
                .done()
                .build();

        array2D.set(2,1,"Y");

        Dump.arry2DItems("ARRAY_2D", array2D.intern());

    }

    @Test
    public void testGet() throws Exception {

    }
}