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
import de.adesso.dtmg.Dump;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.model.Declaration;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.adesso.dtmg.util.tuple.Tuple3;
import de.adesso.dtmg.util.tuple.Tuple5;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.adesso.dtmg.functions.MoreCollectors.toObservableList;
import static de.adesso.dtmg.functions.ObservableList2DFunctions.removeColumn;
import static de.adesso.dtmg.functions.ObservableList2DFunctions.transpose;

/**
 * Created by mmoehler on 02.07.16.
 */
public class TreeMethod {
    public static final String X = "X";
    public static final String DASH = "-";
    public static final String Y = "Y";
    public static final String N = "N";
    public static final String S = " ";

    /** Check the OR-decision table to see if it can be leaf node or condition node*/
    public Tuple2<Boolean,Declaration> step1(DtEntity e) {
        ObservableList<ObservableList<String>> actiondefinitions = transpose().apply(e.getActionDefinitions());
        ObservableList<ActionDeclTableViewModel> actionDeclarations = e.getActionDeclarations();

        final Stream<Indexed<ObservableList<String>>> withIndex = StreamUtils.zipWithIndex(actiondefinitions.stream());

        final Optional<Indexed<ObservableList<String>>> found = withIndex
                .filter(l -> l.getValue().stream()
                        .filter(el -> el.equals(X)).count() == l.getValue().size()).findFirst();

        String s = (found.isPresent())
                ? String.format("This is leaf node - Assign leaf node '%s'", actionDeclarations.get((int)found.get().getIndex()).getModel().getExpression())
                : "This is condition node";

        System.out.println("=================================================================================================================");

        Dump.dumpTableItems("CDEF", e.getConditionDefinitions());
        Dump.dumpTableItems("ADEF", e.getActionDefinitions());

        System.out.println(">>> " + s);

        return (found.isPresent())
                ? Tuple.of(true,actionDeclarations.get((int)found.get().getIndex()).getModel())
                : Tuple.of(false,null);
    }

    /** Find the action that has maximum number of occurrence in OR-decision table */
    public Tuple3<Boolean,ActionDeclTableViewModel,Integer> step2(DtEntity e) {
        ObservableList<ObservableList<String>> aDefinitions = transpose().apply(e.getActionDefinitions());
        ObservableList<ObservableList<String>> cdefinitions = e.getConditionDefinitions();
        ObservableList<ActionDeclTableViewModel> adeclarations = e.getActionDeclarations();

        Stream<Integer> integerStream = StreamUtils.zipWithIndex(aDefinitions.stream())
                .map(r -> StreamUtils.zipWithIndex(r.getValue().stream())
                        .filter(s -> s.getValue().equals(X))
                        .map(s -> calcCountFromDashes(cdefinitions.get((int) s.getIndex())))
                        .reduce(0, (a, b) -> a + b));
        Optional<Indexed<Integer>> max = StreamUtils.zipWithIndex(integerStream)
                .collect(Collectors.maxBy((l, r) -> l.getValue().compareTo(r.getValue())));


        if(max.isPresent()) {
            int actionIndex = (int)max.get().getIndex();

            String s =  String.format("Select action '%s'",adeclarations.get(actionIndex).getModel().getExpression());
            System.out.println(">>> "+s);

            Tuple3<Boolean, ActionDeclTableViewModel, Integer> tuple3 = Tuple.of(true, adeclarations.get(actionIndex), actionIndex);
            return tuple3;
        }
        return Tuple.of(false,null,null);
    }

    private static int calcCountFromDashes(ObservableList<String> c) {
        int result = 1;
        long count = c.stream().filter(s -> DASH.equals(s)).count();
        if(count>0) {
            result = (int) Math.pow(2,count);
        }
        return result;
    }

    /** Find the maximum number of occurrence of the unique value of each condition */
    public Tuple5<Boolean,ConditionDeclTableViewModel, String, Integer, Integer> step3(DtEntity e, int actionIndex) {
        ObservableList<ObservableList<String>> actions = transpose().apply(e.getActionDefinitions());

        // determine indices of X's of the max action row
        List<Long> collectX = StreamUtils.zipWithIndex(actions.get(actionIndex).stream())
                .filter(s -> s.getValue().equals(X))
                .map(t -> t.getIndex())
                .collect(Collectors.toList());

        ObservableList<ObservableList<String>> conditions = e.getConditionDefinitions();
        List<ObservableList<String>> conditionsWithActions = collectX.stream().map(l -> conditions.get(l.intValue())).collect(Collectors.toList());

        Integer[] countedY = conditionsWithActions.stream().map(c -> occurencesOf(c,Y))
                .reduce(newIntegerArray(conditions.get(0).size(),0), addLists);

        Integer[]  countedN = conditionsWithActions.stream().map(c -> occurencesOf(c,N))
                .reduce(newIntegerArray(conditions.get(0).size(),0), addLists);

        Optional<Indexed<Integer>> max = Stream.concat(StreamUtils
                .zipWithIndex(Arrays.stream(countedY)), StreamUtils.zipWithIndex(Arrays.stream(countedN)))
                .collect(Collectors.maxBy((a, b) -> a.getValue().compareTo(b.getValue())));

        if(max.isPresent()) {
            final String sN = String.format("Count input='0' %s", Arrays.toString(countedN));
            System.out.println(">>> "+sN);
            final String sY = String.format("Count input='1' %s", Arrays.toString(countedY));
            System.out.println(">>> "+sY);

            int conditionIndex = (int)max.get().getIndex();

            final String s0 = String.format(">>> Select input: '%s'", e.getConditionDeclarations().get(conditionIndex).getModel().getExpression());
            System.out.println(s0);
            final String s1 = String.format(">>> Split table by condition '%s'", e.getConditionDeclarations().get(conditionIndex).getModel().getExpression());
            System.out.println(s1);

            return Tuple.of(true,e.getConditionDeclarations().get(conditionIndex),e.getConditionDefinitions().get(actionIndex).get(conditionIndex),conditionIndex, actionIndex);
        }

        return Tuple.of(false,null,null,null,null);
    }

    public Integer[] newIntegerArray(int size, Integer filler) {
        Integer ret[] = new Integer[size];
        Arrays.fill(ret,0);
        return ret;
    }

    private BinaryOperator<Integer[]> addLists = (a, b) -> StreamUtils.zip(Arrays.stream(a), Arrays.stream(b),
            (l, r) -> new Integer(l + r)).toArray(Integer[]::new);

    private Integer[] occurencesOf(ObservableList <String> c, String s) {
        Integer[] list = new Integer[c.size()];
        Arrays.fill(list,0);
        for (int i = 0; i < c.size(); i++) {
            if(s.equals(c.get(i))) {
                long dashes = c.stream().filter(y -> DASH.equals(y)).count();
                list[i]+=(int)Math.pow(2,dashes);
            }
        }
        return list;
    }

    public Tuple2<DtEntity,DtEntity> step4(DtEntity entity, int conditionIndex) {

        final Stream<Indexed<ObservableList<String>>> indexedCdefs = StreamUtils.zipWithIndex(entity.getConditionDefinitions().stream());
        final Map<String, List<Indexed<ObservableList<String>>>> indexedDefnsGrouped = indexedCdefs.collect(Collectors.groupingBy((i) -> i.getValue().get(conditionIndex)));

        final Stream<ObservableList<String>> leftCDefnsStream = indexedDefnsGrouped.get(Y).stream().map(e -> e.getValue());
        final ObservableList<ObservableList<String>> leftCDefns = leftCDefnsStream
                .map(removeColumn(conditionIndex))
                .collect(toObservableList());

        final Stream<ObservableList<String>> rightCDefnsStream = indexedDefnsGrouped.get(N).stream().map(e -> e.getValue());
        final ObservableList<ObservableList<String>> rightCDefns = rightCDefnsStream
                .map(removeColumn(conditionIndex))
                .collect(toObservableList());

        final ObservableList<ObservableList<String>> leftADefns = indexedDefnsGrouped.get(Y).stream()
                .map(e -> entity.getActionDefinitions().get((int) e.getIndex())).collect(toObservableList());
        final ObservableList<ObservableList<String>> rightADefns = indexedDefnsGrouped.get(N).stream()
                .map(e -> entity.getActionDefinitions().get((int) e.getIndex())).collect(toObservableList());

        ObservableList<ConditionDeclTableViewModel> cdl = FXCollections.observableArrayList(entity.getConditionDeclarations());
        cdl.remove(conditionIndex);

        return Tuple.of(
                new DtEntity(cdl,leftCDefns,entity.getActionDeclarations(),leftADefns),
                new DtEntity(cdl,rightCDefns,entity.getActionDeclarations(),rightADefns)
        );
    }

}
