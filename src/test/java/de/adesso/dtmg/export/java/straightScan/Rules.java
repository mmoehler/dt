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

package de.adesso.dtmg.export.java.straightScan;

/**
 * Created by mmoehler on 10.06.16.
 */
public abstract class Rules implements Runnable {
    public void run() {
        if (condition00()) {
            if (condition01()) {
                if (condition02()) {
                    action00();
                } else {
                    action01();
                }
            } else {
                if (condition02()) {
                    action00();
                } else {
                    action01();
                }
            }
        } else {
            if (condition01()) {
                if (condition02()) {
                    action02();
                } else {
                    action03();
                }
            } else {
                if (condition02()) {
                    otherwise();
                } else {
                    otherwise();
                }
            }
        }
    }

    protected abstract boolean condition00();

    protected abstract boolean condition01();

    protected abstract boolean condition02();

    protected abstract void action00();

    protected abstract void action01();

    protected abstract void action02();

    protected abstract void action03();

    protected abstract void otherwise();
}
