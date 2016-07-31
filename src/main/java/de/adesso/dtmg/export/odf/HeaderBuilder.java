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

package de.adesso.dtmg.export.odf;

import de.adesso.dtmg.common.builder.AbstractNestable;
import de.adesso.dtmg.common.builder.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmoehler on 31.07.16.
 */
public class HeaderBuilder extends AbstractNestable<ODFDecisionTableData.Builder, String[]> {

    private final List<String> fields = new ArrayList<>();

    public HeaderBuilder(ODFDecisionTableData.Builder parentBuilder, Callback ownerCallback) {
        super(parentBuilder, ownerCallback);
    }

    public HeaderBuilder field(String name) {
        this.fields.add(name);
        return this;
    }

    @Override
    public String[] build() {
        return fields.toArray(new String[fields.size()]);
    }
}
