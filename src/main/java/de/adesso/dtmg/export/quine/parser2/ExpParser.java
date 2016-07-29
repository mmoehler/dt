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

package de.adesso.dtmg.export.quine.parser2;

import com.google.common.base.Splitter;
import de.adesso.dtmg.Dump;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by moehler on 28.07.2016.
 */
public class ExpParser {
    public List<String> parse(String exp) {
        return Splitter.on('+').trimResults()
                .splitToList(exp)
                .stream()
                .map(this::toBinaryForm)
                .collect(Collectors.toList());
    }

    private String toBinaryForm(String s) {
        StringBuilder sb = new StringBuilder();
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; i++) {
            switch (c[i]) {
                case '*':
                    break;
                case '!':
                case '~':
                    i++;
                    sb.append('0');
                    break;
                default:
                    sb.append('1');
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        final List<String> list = new ExpParser().parse("!a*!b*!c*!d + !a*b*!c*d + !a*b*c*!d + !a*b*c*d +a*!b*!c*d + a*!b*c*!d + a*b*!c*d + a*b*c*!d + a*b*c*d");
        Dump.dumpList1DItems("TERMS",list);
    }


}
