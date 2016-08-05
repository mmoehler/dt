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

package de.adesso.dtmg.ui.condition;

import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.ui.DeclarationTableViewModel;
import de.adesso.dtmg.ui.PossibleIndicatorsSupplier;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.StringProperty;

import java.util.Iterator;

import static java.util.Arrays.asList;

/**
 * Created by mohler ofList 16.01.16.
 */
public class ConditionDeclTableViewModel implements PossibleIndicatorsSupplier, DeclarationTableViewModel {

    private final static String EMPTY_STRING = "";

    private final String id;

    private final ModelWrapper<ConditionDecl> wrapper = new ModelWrapper<>();

    public ConditionDeclTableViewModel(ConditionDecl decl) {
        id = decl.getId();
        wrapper.set(decl);
        wrapper.reload();
    }

    public void reset() {
        wrapper.reset();
    }

    public void reloadFromModel() {
        wrapper.reload();
    }

    @Override
    public ConditionDeclTableViewModel save() {
        boolean someValidation = true;
        if (someValidation) {
            wrapper.commit();
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConditionDeclTableViewModel)) {
            return false;
        }
        ConditionDeclTableViewModel other = (ConditionDeclTableViewModel) obj;
        return other.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public StringProperty lfdNrProperty() {
        return wrapper.field("lfdNr", ConditionDecl::getLfdNr, ConditionDecl::setLfdNr);
    }

    @Override
    public StringProperty expressionProperty() {
        return wrapper.field("expression", ConditionDecl::getExpression, ConditionDecl::setExpression);
    }

    @Override
    public StringProperty possibleIndicatorsProperty() {
        return wrapper.field("possibleIndicators", ConditionDecl::getPossibleIndicators, ConditionDecl::setPossibleIndicators);
    }

    @Override
    public String toString() {
        return wrapper.get().toString();
    }

    public boolean isValid() {
        return asList(lfdNrProperty(), expressionProperty(), possibleIndicatorsProperty()).stream()
                .map(i -> i.get() != null && !EMPTY_STRING.equals(i.get()))
                .reduce(true, (x, y) -> x && y);

    }

    public ConditionDecl getModel() {
        return wrapper.get();
    }

    @Override
    public Iterator<String> iterator() {
        return getModel().iterator();
    }
}
