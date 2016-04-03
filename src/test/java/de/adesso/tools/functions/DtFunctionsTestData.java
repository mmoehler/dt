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

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Test fixtures of the DtFunctionsTest's
 * Created by moehler on 02.03.2016.
 */
public class DtFunctionsTestData {

    public final static String ALPHAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public final static String ALPHA_NUMERICS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static Supplier<String> RANDOM_NUMBER_STRING_000_999 =
            () -> ThreadLocalRandom.current().ints(3, 0, 9)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining());
    public static Supplier<String> RANDOM_ALPHA_5 =
            () -> ThreadLocalRandom.current().ints(5, 0, ALPHAS.length())
                    .mapToObj(i -> Character.toString(ALPHAS.charAt(i)))
                    .reduce("", (a, b) -> a + b);
    public static Supplier<String> RANDOM_ALPHA_NUMERIC_5 =
            () -> ThreadLocalRandom.current().ints(5, 0, ALPHA_NUMERICS.length())
                    .mapToObj(i -> Character.toString(ALPHA_NUMERICS.charAt(i)))
                    .reduce("", (a, b) -> a + b);


    public static ListOfIndicatorSuppliersBuilder listOfIndicatorSupliersBuilder() {
        return ListOfIndicatorSuppliersBuilder.newBuilder();
    }

    public static ConditionDeclTableViewBuilder conditionDeclTableViewBuilder() {
        return new ConditionDeclTableViewBuilder();
    }

    public static ActionDeclTableViewBuilder actionDeclTableViewBuilder() {
        return new ActionDeclTableViewBuilder();
    }


    public static ConditionDeclTableViewModelListBuilder conditionDeclTableViewModelListBuilder() {
        return new ConditionDeclTableViewModelListBuilder();
    }

    public static ActionDeclTableViewModelListBuilder actionDeclTableViewModelListBuilder() {
        return new ActionDeclTableViewModelListBuilder();
    }

    public static DefinitionsTableViewBuilder definitionsTableViewBuilder() {
        return new DefinitionsTableViewBuilder();
    }
}
