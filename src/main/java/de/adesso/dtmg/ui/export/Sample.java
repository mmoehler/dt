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

package de.adesso.dtmg.ui.export;

import javax.annotation.Generated;
import java.util.function.Function;


/**
 * Class Level Java Docs
 */
@Generated(value = "de.adesso.dtmg.export.java.straightscan.StraightScanCodeGenerator", date = "30.08.2016 12:00:54")
public abstract class Sample<T, R>
        implements Function<T, R> {


    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isSaldoGT0(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isSaldoEQ0(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isSaldoLO0(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isMahnfristErreicht(T value);

    /**
     * Is this custumer a special guest?
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isVIPKunde(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isBagatellbestandspraemie(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isVorgangsartEP(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    protected abstract boolean isVorgangsartFP(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return R as the result of the processing.
     */
    protected abstract R doKeineAktion(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return R as the result of the processing.
     */
    protected abstract R doProtokolleintrag(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return R as the result of the processing.
     */
    protected abstract R doMahnungParagraph38(T value);

    /**
     * What is $39??
     *
     * @param value A <code>T</code> as input of this method.
     * @return R as the result of the processing.
     */
    protected abstract R doMahnungParagraph39(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return R as the result of the processing.
     */
    protected abstract R doVormerkungVertragsstorno(T value);

    /**
     * TODO: Document this method!!
     *
     * @param value A <code>T</code> as input of this method.
     * @return R as the result of the processing.
     */
    protected abstract R doVertragskuendigung(T value);

    /**
     * This operation applies the rules of the implemented decision table on the given parameters.
     *
     * @param value A <code>T</code> as input of this method.
     * @return boolean <code>true</code> if the condition requirements fullfilled, otherwise <code>false</code>
     */
    @Override
    public R apply(T value) {
        R result = null;
        if ((isSaldoGT0(value)
                && !isSaldoEQ0(value)
                && !isSaldoLO0(value)
                && isMahnfristErreicht(value)
                && !isVIPKunde(value)
                && !isBagatellbestandspraemie(value)
                && isVorgangsartEP(value)
                && !isVorgangsartFP(value))) {
            doMahnungParagraph38(value);
            return result;
        }
        if ((isSaldoGT0(value)
                && !isSaldoEQ0(value)
                && !isSaldoLO0(value)
                && isMahnfristErreicht(value)
                && !isVIPKunde(value)
                && isBagatellbestandspraemie(value)
                && isVorgangsartEP(value)
                && !isVorgangsartFP(value))) {
            doMahnungParagraph38(value);
            doVormerkungVertragsstorno(value);
            return result;
        }
        if ((isSaldoGT0(value)
                && !isSaldoEQ0(value)
                && !isSaldoLO0(value)
                && isMahnfristErreicht(value)
                && !isVIPKunde(value)
                && !isBagatellbestandspraemie(value)
                && !isVorgangsartEP(value)
                && isVorgangsartFP(value))) {
            doMahnungParagraph39(value);
            return result;
        }
        if ((isSaldoGT0(value)
                && !isSaldoEQ0(value)
                && !isSaldoLO0(value)
                && isMahnfristErreicht(value)
                && !isVIPKunde(value)
                && isBagatellbestandspraemie(value)
                && !isVorgangsartEP(value)
                && isVorgangsartFP(value))) {
            doMahnungParagraph39(value);
            doVertragskuendigung(value);
            return result;
        }
        if ((!isSaldoGT0(value)
                && !isSaldoEQ0(value)
                && isSaldoLO0(value))) {
            doProtokolleintrag(value);
            return result;
        }
        if ((isSaldoGT0(value)
                && !isSaldoEQ0(value)
                && !isSaldoLO0(value)
                && isMahnfristErreicht(value)
                && isVIPKunde(value))) {
            doProtokolleintrag(value);
            return result;
        }
        if ((!isSaldoGT0(value)
                && isSaldoEQ0(value)
                && !isSaldoLO0(value))) {
            doKeineAktion(value);
            return result;
        }
        if ((isSaldoGT0(value)
                && !isSaldoEQ0(value)
                && !isSaldoLO0(value)
                && !isMahnfristErreicht(value))) {
            doKeineAktion(value);
            return result;
        }
        return result;
    }

}
