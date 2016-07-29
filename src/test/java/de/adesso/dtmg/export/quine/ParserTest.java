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

package de.adesso.dtmg.export.quine;

import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import org.testng.annotations.Test;

import java.util.List;
import java.util.function.IntFunction;

/**
 * Created by moehler on 19.07.2016.
 */
public class ParserTest {

    final static char H = '-';
    final static Tuple2<List<Integer>, String> EMPY_COMB_RESULT = Tuple.of(null,null);

    @Test
    public void testQuineMcCluskey() throws Exception {
        String[] terms = {
                "0000",
                "0001",
                "0010",
                "1000",
                "0101",
                "0110",
                "1001",
                "1010",
                "0111",
                "1110"
        };

        String result = new QuineMcCluskey().apply("a*b+a*c+!b*c");

        System.out.println("result = " + result);

    }


    @Test
    public void testOther() throws Exception {
        for (int c = 0; c < 3; c++) {
            final int k = c;
            IntFunction<Integer> a0 = (r) -> r % 2;
            IntFunction<Integer> a1 = (r) -> ((r / (int) Math.pow(2, k) % (int) Math.pow(2, k)));
            IntFunction<Integer> f = (0 < c) ? a1 : a0;
            for (int r = 0; r < 8; r++) {
                System.out.print(f.apply(r));
            }
            System.out.println();
        }

    }
}