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

package de.adesso.tools.analysis.structure;

import com.google.common.collect.Lists;
import de.adesso.tools.Dump;
import de.adesso.tools.model.DecisionTableModel;
import de.adesso.tools.ui.DeclarationTableViewModel;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.util.tuple.Tuple4;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by mmoehler ofList 01.05.16.
 */
public class DecisionTable
        extends Tuple4<TableView<ConditionDeclTableViewModel>,TableView<ObservableList<String>>,
                TableView<ActionDeclTableViewModel>,TableView<ObservableList<String>>>
        implements Consumer<DecisionTableModel> {

    private final static int MORE_COLS = 0x02;
    private final static int LESS_COLS = 0x04;
    private final static int SAME_COLS = 0x08;
    private final static int MORE_ROWS = 0x10;
    private final static int LESS_ROWS = 0x20;
    private final static int SAME_ROWS = 0x40;



    public DecisionTable(TableView<ConditionDeclTableViewModel> conditionDecls,
                         TableView<ObservableList<String>> conditionDefns,
                         TableView<ActionDeclTableViewModel> actionDecls,
                         TableView<ObservableList<String>> actionDefns) {
        super(conditionDecls, conditionDefns, actionDecls, actionDefns);
    }

    @Override
    public void accept(DecisionTableModel decisionTableModel) {

    }

    private static void transferDeclarationData(TableView<DeclarationTableViewModel> view, ObservableList<DeclarationTableViewModel> newData) {
        ObservableList<DeclarationTableViewModel> oldData = view.getItems();

    }

    private static void transferDefinitionData(TableView<ObservableList<String>> view, ObservableList<ObservableList<String>> newData) {
        ObservableList<ObservableList<String>> oldData = view.getItems();




    }


    private static int kindOfDefinitionChange(ObservableList<ObservableList<String>> oldData, ObservableList<ObservableList<String>> newData) {
        int or = oldData.size();
        int nr = newData.size();
        int oc = (oldData.isEmpty()) ? (0) : (oldData.get(0).size());
        int nc = (newData.isEmpty()) ? (0) : (newData.get(0).size());
        int ret = 0;
        ret += nr > or ? MORE_ROWS : ((nr < or) ? LESS_ROWS : SAME_ROWS);
        ret += nc > oc ? MORE_COLS : ((nc < oc) ? LESS_COLS : SAME_COLS);
        return ret;
    }

    public static void main(String[] args) {
        List<List<Integer>> li = Lists.newArrayList(
                Lists.newArrayList(1,2,3),
                Lists.newArrayList(1,2,3),
                Lists.newArrayList(1,2,3)
        );

        final int pos = 1;
        final Iterator<Integer> col = Lists.newArrayList(4,5,6).iterator();

        List<List<Integer>> list = li.stream().map(InsertColumn.insertColumnAt(pos, col.next())).collect(Collectors.toList());

        Dump.dumpTableItems("ADD COLUMN", list);

        List<List<Integer>> removed = list.stream().map(new RemoveColumn(2)).collect(Collectors.toList());

        Dump.dumpTableItems("REM COLUMN", removed);
    }

    static class InsertColumn implements Function<List<Integer>,List<Integer>> {
        final int pos, val;

        public static InsertColumn insertColumnAt(int pos, int val) {
            return new InsertColumn(pos,val);
        }

        private InsertColumn(int pos, int val) {
            this.pos = pos;
            this.val = val;
        }

        @Override
        public List<Integer> apply(List<Integer> integers) {
            List<Integer> ret = new ArrayList<>(integers.size()+1);
            ret.addAll(integers);
            ret.add(pos,val);
            return ret;
        }
    }

    static class RemoveColumn implements Function<List<Integer>,List<Integer>> {
        final int pos;

        public RemoveColumn(int pos) {
            this.pos = pos;
        }

        @Override
        public List<Integer> apply(List<Integer> integers) {
            List<Integer> ret = new ArrayList<>(integers.size()+1);
            ret.addAll(integers);
            ret.remove(pos);
            return ret;
        }
    }





}
