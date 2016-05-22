package de.adesso.tools.functions;

import de.adesso.tools.Dump;
import javafx.collections.FXCollections;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * All List2D relevant functions
 * Created by mmoehler ofList 13.02.16.
 */
public final class List2DFunctions {

    // --Commented out by Inspection (15.05.16, 08:50):public static final String QMARK = "?";

    private List2DFunctions() {
        super();
    }

    public static <T> Function<List<List<T>>, List<List<T>>> transpose() {
        return m ->
              range(0, m.get(0).size()).mapToObj(r ->
                    range(0, m.size()).mapToObj(c -> m.get(c).get(r)).collect(toList())).collect(toList());
    }

    public static Function<List<String>, List<String>> replaceColumn(List<String> newData, int pos) {
        return new ReplaceColumn(newData.iterator(), pos);
    }

    public static Function<List<String>, List<String>> insertColumn(String defaultData, int pos) {
        return new InsertColumn(defaultData, pos);
    }

    public static Function<List<String>, List<String>> removeColumn(int pos) {
        return new RemoveColumn(pos);
    }

    public  static Function<List<String>, List<String>> swapColumns(int pos1, int pos2) {
        return new SwapColumns(pos1,pos2);
    }

    public static Function<List<String>, List<String>> moveColumn(int oldPos, int newPos) {
        return new MoveColumn(oldPos, newPos);
    }


    public static <T> List<List<T>> transpose(List<List<T>> original) {
        Dump.dumpTableItems("TRANSPOSE", original);
        if(original.isEmpty()) return original;
        return Stream.of(original).map(transpose()).collect(MoreCollectors.toSingleObject());
    }

    public static List<List<String>> addColumn(List<List<String>> original, Supplier<String> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        List<List<String>> copiedMatrix = copy(original);
        copiedMatrix.stream().forEach(l -> l.add(valueSupplier.get()));
        return copiedMatrix;
    }

    public static List<List<String>> removeColumnsAt(List<List<String>> original, int index) {
        return original.stream().map(removeColumn(index)).collect(toList());
    }

    public static List<List<String>> insertColumnsAt(List<List<String>> original, int index, Supplier<String> defaultValue) {
        return original.stream().map(insertColumn("?", index)).collect(Collectors.toList());
    }

    public static List<List<String>> replaceColumnsAt(List<List<String>> original, int index, List<String> newData) {
        return original.stream().map(replaceColumn(newData, index)).collect(Collectors.toList());
    }

    public static List<List<String>> removeLastColumn(List<List<String>> original) {
        return removeColumnsAt(original, original.get(0).size() - 1);
    }

    public static List<List<String>> swapColumnsAt(List<List<String>> original, int col1Idx, int col2Idx) {
        return original.stream().map(swapColumns(col1Idx,col2Idx)).collect(Collectors.toList());

    }

    public static List<List<String>> moveColumn(List<List<String>> original, int from, int to) {
        return original.stream().map(moveColumn(from,to)).collect(Collectors.toList());

    }


    public static List<String> newRow(int len, Supplier<String> defaultValue) {
        if (len < 0) {
            throw new IllegalStateException("len < 0!");
        }
        if (len == 0) {
            return Collections.emptyList();
        }
        return IntStream.range(0, len)
                .mapToObj(i -> defaultValue.get())
                .collect(toList());
    }

    public static List<List<String>> removeRowsAt(List<List<String>> original, int index) {
        if (original.isEmpty()) {
            return original;
        }
        return IntStream.range(0, original.size())
                .filter(i -> index != i).mapToObj(original::get)
                .collect(toCollection(FXCollections::observableArrayList));
    }

    public static List<List<String>> removeLastRow(List<List<String>> original) {
        return removeRowsAt(original, (original.size() - 1));
    }

    public static List<List<String>> insertRowsAt(List<List<String>> original, int index, Supplier<String> defaultValue) {
        final int len = original.get(0).size();
        Iterator<? extends List<String>> it = original.iterator();
        return IntStream.range(0, original.size() + 1)
                .mapToObj(i -> (index == i) ? (newRow(len, defaultValue)) : (it.next()))
                .collect(toCollection(FXCollections::observableArrayList));
    }

    public static <T> List<T> swapRowsAt(List<T> original, int row1Idx, int row2Idx) {
        List<T> copy = FXCollections.observableList(original);
        T row1 = copy.get(row1Idx);
        copy.set(row1Idx, copy.get(row2Idx));
        copy.set(row2Idx, row1);
        return copy;
    }

    public static List<List<String>> copy(List<List<String>> original) {
        return original.stream().map(l -> l.stream().collect(toList())).collect(toList());
    }

    public static List<String> copyRow(List<String> original) {
        return original.stream().collect(toList());
    }

    public static List<List<String>> addRow(List<List<String>> original, Supplier<String> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        List<List<String>> copiedMatrix = copy(original);
        List<String> copiedRow = IntStream.range(0, original.get(0).size())
                .mapToObj(i -> valueSupplier.get())
                .collect(toList());
        copiedMatrix.add(copiedRow);
        return copiedMatrix;
    }

    // -----------------

    static class AddColumn extends AbstractList2DOperator {

        public AddColumn() {
            super(0);
        }

        @Override
        public List<String> apply(List<String> strings) {
            List<String> intern = new ArrayList<>(strings);
            intern.add("?");
            return intern;
        }
    }


    static class MoveColumn extends AbstractList2DOperator {

        private final int newPos;

        public MoveColumn(int oldPos, int newPos) {
            super(oldPos);
            this.newPos = newPos;
        }

        @Override
        public List<String> apply(List<String> strings) {
            List<String> intern = new ArrayList<>(strings);
            intern.add(newPos, intern.remove(positions[0]));
            return intern;
        }
    }

    static class RemoveColumn extends AbstractList2DOperator {

        public RemoveColumn(int... morePos) {
            super(morePos);
        }

        @Override
        public List<String> apply(List<String> strings) {
            Set<Integer> set = Arrays.stream(positions).boxed().collect(Collectors.toSet());
            return IntStream.range(0, strings.size())
                    .filter(x -> !set.contains(x))
                    .mapToObj(strings::get)
                    .collect(toList());
        }


    }

    static class InsertColumn extends AbstractList2DOperator {

        private final String defaultData;

        public InsertColumn(String defaultData, int pos) {
            super(pos);
            this.defaultData = defaultData;
        }

        @Override
        public List<String> apply(List<String> strings) {
            List<String> intern = FXCollections.observableArrayList(strings);
            intern.add(positions[0], defaultData);
            return intern;
        }
    }

    static class ReplaceColumn extends AbstractList2DOperator {

        private final Iterator<String> newData;

        public ReplaceColumn(Iterator<String> newData, int pos) {
            super(pos);
            this.newData = newData;
        }

        @Override
        public List<String> apply(List<String> strings) {
            List<String> intern = FXCollections.observableArrayList(strings);
            intern.remove(positions[0]);
            intern.add(positions[0], newData.next());
            return intern;
        }
    }

    static class SwapColumns extends AbstractList2DOperator {

        public SwapColumns(int col1Idx, int col2Idx) {
            super(col1Idx, col2Idx);
        }

        @Override
        public List<String> apply(List<String> original) {
            List<String> l = new ArrayList<>(original);
            String l1 = l.get(positions[0]);
            l.set(positions[0], l.get(positions[1]));
            l.set(positions[1], l1);
            return l;
        }
    }

}