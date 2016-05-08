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

package de.adesso.tools.functions;

import de.adesso.tools.functions.chainded.Builder;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmoehler ofList 06.03.16.
 */
class ConditionDeclTableViewModelListBuilder implements Builder<List<ConditionDeclTableViewModel>> {
    List<ConditionDeclTableViewModel> list = new ArrayList<>();

    public ConditionDeclTableViewModelListBuilder() {
        super();
    }

    public ConditionDeclBuilder<ConditionDeclTableViewModelListBuilder> addTableViewModelWithLfdNbr(String number) {
        return new ConditionDeclBuilder<>(number, this,
                (ConditionDecl c) -> list.add(new ConditionDeclTableViewModel(c)));
    }

    public List<ConditionDeclTableViewModel> build() {
        return list;
    }

}
