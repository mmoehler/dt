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

package de.adesso.tools.exception;

import java.io.IOException;

/**
 * Created by mmoehler ofList 02.04.16.
 */
public final class IOExceptionSmuggler extends RuntimeException {

    private static final long serialVersionUID = 8097255748374438402L;

    private IOException target;

    private IOExceptionSmuggler() {
        super((Throwable) null);  // Disallow initCause
    }

    public IOExceptionSmuggler(IOException target) {
        super((IOException) null);  // Disallow initCause
        this.target = target;
    }

    public IOExceptionSmuggler(IOException target, String s) {
        super(s, null);  // Disallow initCause
        this.target = target;
    }

    public IOException getTargetException() {
        return target;
    }

    public IOException getCause() {
        return target;
    }
}
