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

import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmoehler on 06.03.16.
 */
class ActionDeclTableViewModelListBuilder {
    List<ActionDeclTableViewModel> list = new ArrayList<>();

    public ActionDeclTableViewModelListBuilder() {
        super();
    }

    public ActionDeclBuilder<ActionDeclTableViewModelListBuilder> addTableViewModelWithLfdNbr(String number) {
        return new ActionDeclBuilder<>(number, this,
                (lfdNr, expression, possibleIndicators) -> _addTableViewModel(new ActionDecl(lfdNr, expression, possibleIndicators)));
    }

    protected void _addTableViewModel(ActionDecl element) {
        list.add(new ActionDeclTableViewModel(element));
    }

    public List<ActionDeclTableViewModel> build() {
        return list;
    }

    interface Callback {
        void handleCallback(String lfdNr, String expression, String possibleIndicators);
    }

}
