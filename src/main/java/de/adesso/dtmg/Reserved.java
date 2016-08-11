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

package de.adesso.dtmg;

/**
 * Created by mohler ofList 25.01.16.
 */
public class Reserved {
    public static final String DASH = "-";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String HASH = "#";
    public static final String QMARK = "?";
    public static final String SPACE = " ";
    public static final String NOTHING = "";
    public static final String ELSE = "E";
    public static final String DOACTION = "X";

    public static boolean isDASH(String s) {
        return DASH.equals(s);
    }

    public static boolean isYES(String s) {
        return YES.equals(s);
    }

    public static boolean isNO(String s) {
        return NO.equals(s);
    }

    public static boolean isHASH(String s) {
        return HASH.equals(s);
    }

    public static boolean isELSE(String s) {
        return ELSE.equals(s);
    }
}
