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

package de.adesso.tools.analysis.completeness.detailed;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

import static de.adesso.tools.functions.Adapters.Matrix.adapt;
import static de.adesso.tools.functions.List2DFunctions.transpose;

/**
 * Created by mmoehler ofList 28.03.16.
 */
public class ConditionDetailedCompletenessCheck {
    public ConditionDetailedCompletenessCheck() {
    }

    public static List<Indexed<List<String>>> isFormalComplete(ObservableList<ConditionDeclTableViewModel> decls, ObservableList<ObservableList<String>> defns) {

        final List<List<String>> rights = adapt(defns);
        final List<List<String>> missingRules = transpose(Functions.difference().apply(rights));
        // calculate index of the missing rule
        final List<Indexed<List<String>>> missingRulesIndexed = StreamUtils.zipWithIndex(missingRules.stream()).collect(Collectors.toList());
        final int countRules = defns.get(0).size();
        return missingRulesIndexed.stream()
                .map(c -> Indexed.index(c.getIndex() + countRules + 1, c.getValue()))
                .collect(Collectors.toList());
    }

}
