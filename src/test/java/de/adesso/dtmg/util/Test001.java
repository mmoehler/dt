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

package de.adesso.dtmg.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.adesso.dtmg.export.java.treeMethod.*;
import de.adesso.dtmg.io.DtEntity;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Created by moehler on 22.08.2016.
 */
public class Test001 {

    final Comparator<List<String>> ruleConditionsComparatpr0 = (l, r) -> {
        for (int i = 0; i < l.size(); i++) {
            String cr = l.get(i);
            String cl = r.get(i);
            if ("-".equals(cl) || "-".equals(cr))
                continue;
            final int p = cl.compareTo(cr);
            if (0 != p)
                return (p);
        }
        return 0;
    };

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



    @Test
    public void testPermutations() {
        System.out.println("-----------------------------------------");
        Integer[] i = new Integer[]{0, 1, 2};
        Permutation<Integer> p = new Permutation<>(i);
        p.forEach(e -> System.out.println(Arrays.toString(e)));
    }

    @Test
    public void testPermutationsWithStreamSequential() {
        System.out.println("-----------------------------------------");
        Integer[] i = new Integer[]{0, 1, 2};
        Permutation<Integer> p = new Permutation<>(i);
        p.stream().forEach(e -> System.out.println(Arrays.toString(e)));
    }

    @Test
    public void testPermutationsWithStreamParallel() {
        System.out.println("-----------------------------------------");
        Integer[] i = new Integer[]{0, 1, 2};
        Permutation<Integer> p = new Permutation<>(i);
        p.parallelStream().forEach(e -> System.out.println(Arrays.toString(e)));
        System.out.println("-----------------------------------------");
    }

    @Test
    public void testCanonicla01() {

        Function<ObservableList<ObservableList<String>>, List<List<DtCell>>> translate = (l -> {
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

        UnaryOperator<List<List<DtCell>>> transpose =
            m -> range(0, m.get(0).size())
                    .mapToObj(r -> range(0, m.size())
                        .mapToObj(c -> m.get(c).get(r))
                        .collect(toList()))
                    .collect(toList());

        final ObservableList<ObservableList<String>> condefs = ObservableList2DBuilder.observable2DOf(
                "N,N,-,Y," +
                "N,Y,N,Y," +
                "Y,-,N,-"
        ).dim(3, 4).build();

        Dump.dumpTableItems("ORIGINAL", condefs);
        List<List<DtCell>> cells = translate.apply(condefs);
        Dump.dumpTableItems("Cells",cells);


        // optimizing sort
        cells = cells.stream().sorted(countDontCaresComparator).collect(Collectors.toList());
        Dump.dumpTableItems("countDontCaresComparator",cells);
        cells = transpose.apply(cells);
        cells = cells.stream().sorted(ruleConditionsComparator).collect(Collectors.toList());
        cells = transpose.apply(cells);
        Dump.dumpTableItems("ruleConditionsComparator",cells);

        // split yes/no

        cells = transpose.apply(cells);


        Map<DtCellType, List<List<DtCell>>> map = cells.stream().collect(
                Collectors.groupingBy(l -> l.get(0).type(),
                        Collectors.mapping(t -> IntStream.range(1, ((List) t).size())
                                .mapToObj(i -> t.get(i))
                                .collect(Collectors.toList()), Collectors.toList())
                )
        );


        Dump.dumpTableItems("Y Branch",transpose.apply(map.get(DtCellType.Y)));
        Dump.dumpTableItems("N Branch",transpose.apply(map.get(DtCellType.N)));


    }

    @Test
    public void testDecompose() {
        final ObservableList<ObservableList<String>> condefs = ObservableList2DBuilder.observable2DOf(
                        "N,N,-,Y," +
                        "N,Y,N,Y," +
                        "Y,-,N,-"
        ).dim(3, 4).build();
        Dump.dumpTableItems("ORIGINAL", condefs);

        DtEntity dtEntity = DtEntityStub.createForConditionDefinitions(condefs);

        DtNode top = new DecomposeOptimized().apply(dtEntity);

        System.out.println("top = " + top);

        emitCode(top);

    }

    static void printTo(DtNode n) {
        if(null == n) return;
        final String s = Strings.padStart(String.valueOf(n.getConditionIndex()), 3 * n.getConditionIndex(), '.');
        System.out.println(n);
        printTo(n.yes);
        printTo(n.no);
    }

    @Test
    public void testLexicographicalSort() {
        Lists.newArrayList("NNY", "YN-", "N-N", "YY-").stream().sorted((l, r) -> {
            for (int i = 0; i < l.length(); i++) {
                char cl = l.charAt(i);
                char cr = r.charAt(i);
                if (cl == '-' || cr == '-')
                    continue;
                if (0 != (cr - cl))
                    return (cr - cl);
            }
            return 0;
        }).collect(Collectors.toList()).forEach(System.out::println);
    }


    static void emitCode(DtNode top) {
        System.out.println("public abstract class Rules implements Runnable {");

        System.out.println("public void run() {");

        Visitor<DtNode> v = new SimpleVisitor();
        TreeSet<String> actionNames = Sets.newTreeSet();
        TreeSet<String> conditionNames = Sets.newTreeSet();
        top.accept(v, actionNames, conditionNames);

        System.out.println("}");

        String conTpl = "protected abstract boolean %s();";
        conditionNames.forEach(n -> System.out.println(String.format(conTpl, n)));
        String actTpl = "protected abstract void %s();";
        actionNames.forEach(n -> System.out.println(String.format(actTpl, n)));

        System.out.println("}");
    }






}
