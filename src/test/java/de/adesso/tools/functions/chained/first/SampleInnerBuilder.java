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

package de.adesso.tools.functions.chained.first;

import com.sun.javafx.geom.Point2D;

/**
 * Created by moehler ofList 11.03.2016.
 */
public class SampleInnerBuilder<C> extends AbstractSubBuilder<Point2D, C> {
    private final float x;
    private float y;

    public SampleInnerBuilder(float value, C caller, Callback<Point2D> callback) {
        super(caller, callback);
        this.x = value;
    }

    public C y(float val) {
        this.y = val;
        getCallback().call(this.build());
        return getCaller();
    }

    @Override
    public Point2D build() {
        return new Point2D(x, y);
    }
}
