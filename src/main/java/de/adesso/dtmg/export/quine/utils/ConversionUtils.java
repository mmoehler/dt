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

package de.adesso.dtmg.export.quine.utils;

/**
 * Created by moehler on 27.07.2016.
 */
public final class ConversionUtils {
    public static char c(int i) {
        if (i >= 0 && i <= 9) {
            return (char) (i + 48);
        }
        throw new IndexOutOfBoundsException("Illegal char range");
    }

    public static int i(char c) {
        if (c >= 48 && c <= 57) {
            return (c - 48);
        }
        throw new IndexOutOfBoundsException("Illegal char range");
    }

    public static char[] c(int i[]) {
        char ret[] = new char[i.length];
        for (int j = 0; j < i.length; j++) {
            ret[j] = c(i[j]);
        }
        return ret;
    }

    public static int[] i(char c[]) {
        return String.valueOf(c).chars().map(a -> i((char) a)).toArray();
    }

}
