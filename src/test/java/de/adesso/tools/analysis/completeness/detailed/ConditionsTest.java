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

import de.adesso.tools.util.tuple.Tuple2;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by mmoehler on 19.03.16.
 */
public class ConditionsTest {
    @Test
    public void testB1OK() {
        int expected = 1;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("N,N,N,Y,N,N,N,N").rows(4).build();
        int actual = Conditions.B1.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testB1NOK() {
        int expected = 0;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("N,N,N,-,N,N,N,N").rows(4).build();
        int actual = Conditions.B1.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testB2OK() {
        int expected = 1;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("Y,Y,Y,Y,Y,Y,Y,Y").rows(4).build();
        int actual = Conditions.B2.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testB2NOK() {
        int expected = 0;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("Y,Y,Y,-,Y,-,Y,Y").rows(4).build();
        int actual = Conditions.B2.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testB3OK() {
        int expected = 1;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("Y,Y,-,Y,Y,Y,Y,Y").rows(4).build();
        int actual = Conditions.B3.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testB3NOK() {
        int expected = 0;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("Y,Y,Y,-,Y,Y,Y,Y").rows(4).build();
        int actual = Conditions.B3.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testB4OK() {
        int expected = 1;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("Y,Y,-,Y,Y,Y,-,N").rows(4).build();
        int actual = Conditions.B4.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testB4NOK() {
        int expected = 0;
        List<Tuple2<String, String>> tuple2List = Tuple2ListBuilder.on("Y,Y,-,Y,Y,Y,Y,Y").rows(4).build();
        int actual = Conditions.B4.apply(tuple2List);

        assertThat(actual, equalTo(expected));
    }

}