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
import com.sun.javafx.geom.Point2D;


/**
 * Created by moehler on 11.03.2016.
 */
public class SampleOuterBuilder implements Builder<Line2D> {

    Point2D start;
    Point2D end;

    public SampleInnerBuilder<SampleOuterBuilder> startPointX(float number) {
        return new SampleInnerBuilder<>(number, this, (p) -> start = p);
    }

    public SampleInnerBuilder<SampleOuterBuilder> endPointX(float number) {
        return new SampleInnerBuilder<>(number, this, (p) -> end = p);
    }

    @Override
    public Line2D build() {
        return new Line2D(start, end);
    }
}
