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

package de.adesso.dtmg.export.java.treeMethod;

import com.google.common.collect.Lists;
import de.adesso.dtmg.io.DtEntity;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Created by moehler on 26.08.2016.
 */
public class DecomposeOptimized implements Function<DtEntity,DtNode> {

    final Comparator<List<DtCell>> ruleConditionsComparator = (l, r) -> {
        if(l.size()!=r.size()) throw new IllegalStateException();
        Iterator<DtCell> li = l.iterator();
        Iterator<DtCell> ri = r.iterator();
        while(li.hasNext()) {
            DtCell cl = li.next();
            DtCell cr = ri.next();
            if (cl.typeOf(DtCellType.I) || cr.typeOf(DtCellType.I))
                continue;
            final int p = cl.compareTo(cr);
            if (0 != p)
                return (p);
        }
        return 0;
    };

    final Comparator<List<DtCell>> countDontCaresComparator = (l, r) -> {
        Long cl = l.stream().filter(e -> e.typeOf(DtCellType.I)).count();
        Long cr = r.stream().filter(e -> e.typeOf(DtCellType.I)).count();
        final int p = cl.compareTo(cr);
        return (p);
    };

    final UnaryOperator<List<List<DtCell>>> transpose =
            m -> range(0, m.get(0).size())
                    .mapToObj(r -> range(0, m.size())
                            .mapToObj(c -> m.get(c).get(r))
                            .collect(toList()))
                    .collect(toList());

    final Function<ObservableList<ObservableList<String>>, List<List<DtCell>>> translate = (l -> {
        final int rows = l.size();
        final int cols = l.get(0).size();

        List<List<DtCell>> columns = Lists.newArrayListWithExpectedSize(rows);
        for (int i = 0; i < rows; i++) {
            List<DtCell> curRow = Lists.newArrayListWithExpectedSize(cols);
            for (int j = 0; j < cols; j++) {
                curRow.add(DtCell.newBuilder()
                        .row(i)
                        .col(j)
                        .type(DtCellType.lookup(l.get(i).get(j)).get())
                        .build());
            }
            columns.add(curRow);
        }
        return columns;
    });

    final UnaryOperator<List<List<DtCell>>> optimize = (c) -> {
        List<List<DtCell>> cells = c;
        cells = cells.stream().sorted(countDontCaresComparator).collect(Collectors.toList());
        cells = transpose.apply(cells);
        cells = cells.stream().sorted(ruleConditionsComparator).collect(Collectors.toList());
        cells = transpose.apply(cells);
        return cells;
    };

    final Predicate<List<List<DtCell>>> hasValues = (c) -> c.stream().filter(l -> !l.isEmpty()).findFirst().isPresent();

    final Function<DtNode,DtNode> decompose = x -> {
        BiFunction<BiFunction, DtNode, DtNode> internal =
                (func, node) -> {
                    List<List<DtCell>> cells = node.data;
                    cells = optimize.apply(cells);
                    node.data = cells;
                    node.conditionIndex = cells.get(0).get(0).row();
                    cells = transpose.apply(cells);
                    Map<DtCellType, List<List<DtCell>>> grouped = cells.stream().collect(
                            Collectors.groupingBy(l -> l.get(0).type(),
                                    Collectors.mapping(t -> IntStream.range(1, ((List) t).size())
                                            .mapToObj(i -> t.get(i))
                                            .collect(Collectors.toList()), Collectors.toList())
                            )
                    );
                    if(grouped.containsKey(DtCellType.Y) && hasValues.test(grouped.get(DtCellType.Y))) {
                        List<List<DtCell>> yesCells = transpose.apply(grouped.get(DtCellType.Y));
                        node.yes = (DtNode)func.apply(func, DtNode.newBuilder().data(yesCells).build());
                    }
                    if(grouped.containsKey(DtCellType.N) && hasValues.test(grouped.get(DtCellType.N))) {
                        List<List<DtCell>> noCells = transpose.apply(grouped.get(DtCellType.N));
                        node.no = (DtNode)func.apply(func, DtNode.newBuilder().data(noCells).build());
                    }
                    return node;
                };
        return internal.apply(internal, x);
    };

    @Override
    public DtNode apply(DtEntity entity) {
        List<List<DtCell>> cells = translate.apply(entity.getConditionDefinitions());
        return decompose.apply(DtNode.newBuilder().data(cells).build());
    }
}
