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

package de.adesso.dtmg.export.java;

public abstract class Rules implements Runnable {
    public void run() {
        if(_1()) {
            if(_0()) {
                X4();
            } else {
                X2();
            }
        } else {
            if(_2()) {
                if(_0()) {
                    X5();
                } else {
                    X1();
                }
            } else {
                X3();
            }
        }
    }
    protected abstract boolean _0();
    protected abstract boolean _1();
    protected abstract boolean _2();
    protected abstract void X1();
    protected abstract void X2();
    protected abstract void X3();
    protected abstract void X4();
    protected abstract void X5();
}
