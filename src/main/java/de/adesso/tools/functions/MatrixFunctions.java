package de.adesso.tools.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public static ObservableList<ObservableList<String>> transpose(ObservableList<ObservableList<String>> table) {

        if (null == table) throw new IllegalArgumentException("Table to transpose is null");

        if (table.isEmpty()) return table;

        ObservableList<ObservableList<String>> transposedObservableList = FXCollections.observableArrayList();

        final int firstObservableListSize = table.get(0).size();
        for (int i = 0; i < firstObservableListSize; i++) {
            ObservableList<String> tempObservableList = FXCollections.observableArrayList();
            for (ObservableList<String> row : table) {
                tempObservableList.add(row.get(i));
            }
            transposedObservableList.add(tempObservableList);
        }
        return transposedObservableList;
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
     * Copies a nested {@code ObservableList}
     *
     * @param original the ObservableList which should be copied
     * @return an ObservableList as copy of {@code src}
     */
    public static ObservableList<ObservableList<String>> copy(ObservableList<ObservableList<String>> original) {
        return FXCollections.observableArrayList(original);
    }

    public static ObservableList<String> copyRow(ObservableList<String> original) {
        return FXCollections.observableArrayList(original);
    }

    public static ObservableList<ObservableList<String>> addRow(ObservableList<ObservableList<String>> original,
                                                               Supplier<String> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<String>> copiedMatrix = copy(original);
        ObservableList<String> copiedRow = copyRow(original.get(0));
        Collections.fill(copiedRow, valueSupplier.get());
        copiedMatrix.add(copiedRow);
        return copiedMatrix;
    }

    public static ObservableList<ObservableList<String>> addColumn(ObservableList<ObservableList<String>> original, Supplier<String> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<String>> copiedMatrix = copy(original);
        copiedMatrix.stream().forEach(l -> l.add(valueSupplier.get()));
        return copiedMatrix;
    }

    public static ObservableList<ObservableList<String>> removeRowsAt(ObservableList<ObservableList<String>> original, int index) {
        if (original.isEmpty()) {
            return original;
        }
        return IntStream.range(0, original.size())
                .filter(i -> index != i).mapToObj(original::get)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static ObservableList<ObservableList<String>> removeLastRow(ObservableList<ObservableList<String>> original) {
        return removeRowsAt(original, (original.size() - 1));
    }

    public static ObservableList<ObservableList<String>> removeColumnsAt(ObservableList<ObservableList<String>> original, int index) {

        ObservableList<ObservableList<String>> modifiedMatrix = FXCollections.emptyObservableList();

        if (!original.isEmpty()) {

            final ObservableList<ObservableList<String>> copiedMatrix = copy(original);
            modifiedMatrix = copiedMatrix.stream()
                    .map(l -> {
                        ObservableList<String> out = FXCollections.observableArrayList();
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
     * @param original 2D {@code ObservableList<String>} as container to modify
     * @param index    a {@code List<Integer>} as given indices
     * @return a 2D {@code ObservableList<String>} as resulting matrix.
     */
    public static ObservableList<ObservableList<String>> insertColumnsAt(ObservableList<ObservableList<String>> original, int index, Supplier<String> defaultValue) {

        ObservableList<ObservableList<String>> modifiedMatrix = FXCollections.emptyObservableList();

        if (!original.isEmpty()) {

            final ObservableList<ObservableList<String>> copiedMatrix = copy(original);
            modifiedMatrix = copiedMatrix.stream()
                    .map(l -> {
                        ObservableList<String> out = FXCollections.observableArrayList();
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

    public static ObservableList<String> newRow(int len, Supplier<String> defaultValue) {
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

    public static ObservableList<ObservableList<String>> removeLastColumn(ObservableList<ObservableList<String>> original) {
        final int cols = original.get(0).size();
        ObservableList<ObservableList<String>> out = original.stream()
                .map(row -> FXCollections.observableArrayList(row.subList(0, cols - 1)))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return out;
    }

    public static ObservableList<ObservableList<String>> insertRowsAt(ObservableList<ObservableList<String>> original, int index, Supplier<String> defaultValue) {
        final int len = original.get(0).size();
        Iterator<ObservableList<String>> it = original.iterator();
        return IntStream.range(0, original.size() + 1)
                .mapToObj(i -> (index == i) ? (newRow(len, defaultValue)) : (it.next()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static ObservableList<ObservableList<String>> swapRowsAt(ObservableList<ObservableList<String>> original, int row1Idx, int row2Idx) {
        ObservableList<ObservableList<String>> copy = copy(original);
        ObservableList<String> row1 = copy.get(row1Idx);
        copy.set(row1Idx, copy.get(row2Idx));
        copy.set(row2Idx, row1);
        return copy;
    }

    public static ObservableList<ObservableList<String>> swapColumnsAt(ObservableList<ObservableList<String>> original, int col1Idx, int col2Idx) {
        ObservableList<ObservableList<String>> copy = transpose(original);
        ObservableList<String> col1 = copy.get(col1Idx);
        copy.set(col1Idx, copy.get(col2Idx));
        copy.set(col2Idx, col1);
        return transpose(copy);
    }
}