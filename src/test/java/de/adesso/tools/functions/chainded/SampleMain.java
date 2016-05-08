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

package de.adesso.tools.functions.chainded;

import com.sun.javafx.geom.Line2D;

/**
 * Created by moehler ofList 11.03.2016.
 */
public class SampleMain {
    public static void main(String[] args) {
        Line2D line = new SampleOuterBuilder()
                .startPointX(12.00f).y(12.00f)
                .endPointX(24.00f).y(24.00f)
                .build();
        line2DAsString(line);
    }

    public static void line2DAsString(Line2D l) {
        final String s = String.format("[%f;%f] -> [%f;%f]", l.x1, l.y1, l.x2, l.y2);
        System.out.println(s);
    }


}
