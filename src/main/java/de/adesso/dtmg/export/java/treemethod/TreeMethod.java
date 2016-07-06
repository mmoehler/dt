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

package de.adesso.dtmg.export.java.treemethod;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import de.adesso.dtmg.functions.ObservableList2DFunctions;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.model.Declaration;
import de.adesso.dtmg.ui.DeclarationTableViewModel;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.tuple.HObservableLists;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 02.07.16.
 */
public class TreeMethod {
    public static final String X = "X";
    public static final String DASH = "-";
    public static final String Y = "Y";
    public static final String N = "N";

    /** Check the OR-decision table to see if it can be leaf node or condition node*/
    public Tuple2<Boolean,Declaration> step1(DtEntity e) {
        ObservableList<ObservableList<String>> actiondefinitions = e.getActionDefinitions();
        ObservableList<ActionDeclTableViewModel> actionDeclarations = e.getActionDeclarations();

        final Stream<Indexed<ObservableList<String>>> withIndex = StreamUtils.zipWithIndex(actiondefinitions.stream());

        final Optional<Indexed<ObservableList<String>>> found = withIndex
                .filter(l -> l.getValue().stream()
                        .filter(el -> el.equals(X)).count() == l.getValue().size()).findFirst();

        return (found.isPresent())
                ? Tuple.of(true,actionDeclarations.get((int)found.get().getIndex()).getModel())
                : Tuple.of(false,null);
    }


    public Tuple2<DtEntity,DtEntity> step4(DtEntity e, int idx) {

        int index = idx+1;

        final Tuple2<ObservableList<ConditionDeclTableViewModel>, ObservableList<ConditionDeclTableViewModel>> splittedCDEC =
                splitDeclarationsAt(e.getConditionDeclarations(), index);

        final Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>> splittedCDEF =
                splitDefinitionsAt(e.getConditionDefinitions(), index);

        final Tuple2<ObservableList<ActionDeclTableViewModel>, ObservableList<ActionDeclTableViewModel>> splittedADEC =
                splitDeclarationsAt(e.getActionDeclarations(), index);

        final Tuple2<ObservableList<ObservableList<String>>, ObservableList<ObservableList<String>>>
                splittedADEF = splitDefinitionsAt(e.getActionDefinitions(), index);

        return Tuple.of(
                new DtEntity(splittedCDEC._1(),splittedCDEF._1(),splittedADEC._1(),splittedADEF._1()),
                new DtEntity(splittedCDEC._2(),splittedCDEF._2(),splittedADEC._2(),splittedADEF._2())
        );
    }

    // -- private -------------------------------------------------------------

    public static <T extends DeclarationTableViewModel> Tuple2<ObservableList<T>,ObservableList<T>> splitDeclarationsAt(ObservableList<T> l, int idx) {
        final ObservableList<ObservableList<T>> lists = HObservableLists.splitAt(l, idx);
        return Tuple.of(lists.get(0),lists.get(1));
    }

    public static Tuple2<ObservableList<ObservableList<String>>,ObservableList<ObservableList<String>>> splitDefinitionsAt(ObservableList<ObservableList<String>> l, int idx) {
        final ObservableList<ObservableList<String>> y = ObservableList2DFunctions.transpose().apply(l);
        final ObservableList<ObservableList<ObservableList<String>>> lists = HObservableLists.splitAt(y, 1);
        return Tuple.of(ObservableList2DFunctions.transpose().apply(lists.get(0)),ObservableList2DFunctions.transpose().apply(lists.get(1)));
    }


}
