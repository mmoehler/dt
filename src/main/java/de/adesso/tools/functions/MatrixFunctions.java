package de.adesso.tools.functions;

import javafx.collections.FXCollections;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * All List2D relevant functions
 * Created by mmoehler on 13.02.16.
 */
public final class MatrixFunctions {

    public static final String QMARK = "?";

    private MatrixFunctions() {
        super();
    }

    public static <T> List<List<T>> transpose(List<List<T>> table) {

        if (null == table) throw new IllegalArgumentException("Table to transpose is null");
        if (table.isEmpty()) return table;
        List<List<T>> transposedList = new ArrayList<>();
        final int firstListSize = table.get(0).size();
        for (int i = 0; i < firstListSize; i++) {
            List<T> tempList = new ArrayList<>();
            for (List<T> row : table) {
                tempList.add(row.get(i));
            }
            transposedList.add(tempList);
        }
        return transposedList;
    }

    /**
     * Copies a nested {@code List}
     *
     * @param original the List which should be copied
     * @return an List as copy of {@code src}
     */
    public static List<List<String>> copy(List<List<String>> original) {
        return original.stream().map(l -> l.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }

    public static List<String> copyRow(List<String> original) {
        return original.stream().collect(Collectors.toList());
    }

    public static List<List<String>> addRow(List<List<String>> original, Supplier<String> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        List<List<String>> copiedMatrix = copy(original);
        List<String> copiedRow = IntStream.range(0, original.get(0).size())
                .mapToObj(i -> valueSupplier.get())
                .collect(Collectors.toList());
        copiedMatrix.add(copiedRow);
        return copiedMatrix;
    }

    public static List<List<String>> addColumn(List<List<String>> original, Supplier<String> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        List<List<String>> copiedMatrix = copy(original);
        copiedMatrix.stream().forEach(l -> l.add(valueSupplier.get()));
        return copiedMatrix;
    }

    public static List<List<String>> removeRowsAt(List<List<String>> original, int index) {
        if (original.isEmpty()) {
            return original;
        }
        return IntStream.range(0, original.size())
                .filter(i -> index != i).mapToObj(original::get)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static List<List<String>> removeLastRow(List<List<String>> original) {
        return removeRowsAt(original, (original.size() - 1));
    }

    public static List<List<String>> removeColumnsAt(List<List<String>> original, int index) {

        List<List<String>> modifiedMatrix = FXCollections.emptyObservableList();

        if (!original.isEmpty()) {

            final List<List<String>> copiedMatrix = copy(original);
            modifiedMatrix = copiedMatrix.stream()
                    .map(l -> {
                        List<String> out = FXCollections.observableArrayList();
                        for (int i = 0; i < l.size(); i++) {
                            if (index == i) {
                                continue;
                            }
                            out.add(l.get(i));
                        }
                        return out;
                    })
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }
        return modifiedMatrix;
    }

    /**
     * Inserts new columns at the given indices. Does not modify the given matrix.
     * Returns a modifed copy of the given container
     *
     * @param original 2D {@code List<String>} as container to modify
     * @param index    a {@code List<Integer>} as given indices
     * @return a 2D {@code List<String>} as resulting matrix.
     */
    public static List<List<String>> insertColumnsAt(List<List<String>> original, int index, Supplier<String> defaultValue) {

        List<List<String>> modifiedMatrix = FXCollections.emptyObservableList();

        if (!original.isEmpty()) {

            final List<List<String>> copiedMatrix = copy(original);
            modifiedMatrix = copiedMatrix.stream()
                    .map(l -> {
                        List<String> out = FXCollections.observableArrayList();
                        for (int i = 0; i < l.size(); i++) {
                            if (index == i) {
                                out.add(defaultValue.get());
                            }
                            out.add(l.get(i));
                        }
                        return out;
                    })
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }
        return modifiedMatrix;
    }

    public static List<String> newRow(int len, Supplier<String> defaultValue) {
        if (len < 0) {
            throw new IllegalStateException("len < 0!");
        }
        if (len == 0) {
            return FXCollections.emptyObservableList();
        }
        return IntStream.range(0, len)
                .mapToObj(i -> defaultValue.get())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static List<List<String>> removeLastColumn(List<List<String>> original) {
        final int cols = original.get(0).size();
        List<List<String>> out = original.stream()
                .map(row -> FXCollections.observableArrayList(row.subList(0, cols - 1)))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return out;
    }

    public static List<List<String>> insertRowsAt(List<List<String>> original, int index, Supplier<String> defaultValue) {
        final int len = original.get(0).size();
        Iterator<? extends List<String>> it = original.iterator();
        return IntStream.range(0, original.size() + 1)
                .mapToObj(i -> (index == i) ? (newRow(len, defaultValue)) : (it.next()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static <T> List<T> swapRowsAt(List<T> original, int row1Idx, int row2Idx) {
        List<T> copy = FXCollections.observableList(original);
        T row1 = copy.get(row1Idx);
        copy.set(row1Idx, copy.get(row2Idx));
        copy.set(row2Idx, row1);
        return copy;
    }

    public static List<List<String>> swapColumnsAt(List<List<String>> original, int col1Idx, int col2Idx) {
        List<List<String>> copy = transpose(original);
        List<String> col1 = copy.get(col1Idx);
        copy.set(col1Idx, copy.get(col2Idx));
        copy.set(col2Idx, col1);
        return transpose(copy);
    }
}