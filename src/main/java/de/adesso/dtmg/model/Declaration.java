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

package de.adesso.dtmg.model;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * General state and behaviour of all declarations
 * Created by mohler ofList 16.01.16.
 */
public class Declaration extends Identity implements Iterable<String> {

    public static final String EMPTY_STRING = "";
    /**
     * sequential numbr of this dicl
     */
    protected String lfdNr = EMPTY_STRING;

    /**
     * Expression of this decl. E.g. 'Partner.age > 32'
     */
    protected String expression = EMPTY_STRING;

    /**
     * Possible indicators of this decl. Currently only {YES,NO} are supported
     */
    protected String possibleIndicators = EMPTY_STRING;

    /**
     * Additional documentation
     */
    protected String documentation = EMPTY_STRING;

    public Declaration() {
    }

    protected Declaration(@Nonnull List<String> data) {
        this(data.get(0), data.get(1), data.get(2), data.get(3));
    }


    public Declaration(@Nonnull String lfdNr, @Nonnull String expression, @Nonnull String possibleIndicators, @Nonnull String documentation) {
        this.lfdNr = lfdNr;
        this.expression = expression;
        this.documentation = documentation;
        this.possibleIndicators = possibleIndicators;
    }

    public String getLfdNr() {
        return lfdNr;
    }

    public void setLfdNr(String lfdNr) {
        this.lfdNr = lfdNr;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getPossibleIndicators() {
        return possibleIndicators;
    }

    public void setPossibleIndicators(String possibleIndicators) {
        this.possibleIndicators = possibleIndicators;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public int getPossibleIndicatorSize() {
        return (null == this.possibleIndicators || this.possibleIndicators.trim().length() == 0)
                ? 0
                : this.possibleIndicators.split("[,;]]").length;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id='" + getId() + '\'' +
                ", lfdNr='" + lfdNr + '\'' +
                ", expression='" + expression + '\'' +
                ", possibleIndicators='" + possibleIndicators + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Declaration that = (Declaration) o;

        if (lfdNr != null ? !lfdNr.equals(that.lfdNr) : that.lfdNr != null) return false;
        if (expression != null ? !expression.equals(that.expression) : that.expression != null) return false;
        return possibleIndicators != null ? possibleIndicators.equals(that.possibleIndicators) : that.possibleIndicators == null;

    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (lfdNr != null ? lfdNr.hashCode() : 0);
        result = 31 * result + (expression != null ? expression.hashCode() : 0);
        result = 31 * result + (possibleIndicators != null ? possibleIndicators.hashCode() : 0);
        return result;
    }

    public List<String> asList() {
        return Lists.newArrayList(getLfdNr(), getExpression(), getPossibleIndicators(), getDocumentation());
    }

    @Override
    public Iterator<String> iterator() {
        return asList().iterator();
    }
}
