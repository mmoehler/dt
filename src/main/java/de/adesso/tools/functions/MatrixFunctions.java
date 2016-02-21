package de.adesso.tools.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mmoehler on 13.02.16.
 */
public final class MatrixFunctions {

    public static final String QMARK = "?";

    private MatrixFunctions() {
        super();
    }

    public static <T> ObservableList<ObservableList<T>> transpose(ObservableList<ObservableList<T>> table) {

        if (null == table) throw new IllegalArgumentException("Table to transpose is null");

        if (table.isEmpty()) return table;

        ObservableList<ObservableList<T>> transposedObservableList = FXCollections.observableArrayList();

        final int firstObservableListSize = table.get(0).size();
        for (int i = 0; i < firstObservableListSize; i++) {
            ObservableList<T> tempObservableList = FXCollections.observableArrayList();
            for (ObservableList<T> row : table) {
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
     * @param <T>
     * @return an ObservableList as copy of {@code src}
     */
    public static <T> ObservableList<ObservableList<T>> copy(ObservableList<ObservableList<T>> original) {
        return original.stream()
                .map(e -> e.stream()
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)))
                .collect(Collectors.toCollection(() -> FXCollections.observableArrayList()));
    }

    public static <T> ObservableList<T> copyRow(ObservableList<T> original) {
        return original.stream()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static <T> ObservableList<ObservableList<T>> addRow(ObservableList<ObservableList<T>> original,
                                                               Supplier<T> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<T>> copiedMatrix = copy(original);
        ObservableList<T> copiedRow = copyRow(original.get(0));
        Collections.fill(copiedRow, valueSupplier.get());
        copiedMatrix.add(copiedRow);
        return copiedMatrix;
    }

    public static <T> ObservableList<ObservableList<T>> addColumn(ObservableList<ObservableList<T>> original, Supplier<T> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<T>> copiedMatrix = copy(original);
        copiedMatrix.stream().forEach(l -> l.add(valueSupplier.get()));
        return copiedMatrix;
    }

    public static <T> ObservableList<ObservableList<T>> removeRowsAt(ObservableList<ObservableList<T>> original, List<Integer> indices) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<T>> out = IntStream.range(0, original.size())
                .filter(i -> !indices.contains(i)).mapToObj(original::get)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return out;
    }

    public static <T> ObservableList<ObservableList<T>> removeLastRow(ObservableList<ObservableList<T>> original) {
        ArrayList<Integer> arg = new ArrayList<>();
        arg.add(original.size() - 1);
        return removeRowsAt(original, arg);
    }

    public static <T> ObservableList<ObservableList<T>> removeColumnsAt(ObservableList<ObservableList<T>> original, List<Integer> indices) {

        Collections.sort(indices, (a, b) -> b.intValue() - a.intValue());

        if (original.isEmpty()) {
            return original;
        }

        final ObservableList<ObservableList<T>> copiedMatrix = copy(original);
        final ObservableList<ObservableList<T>> modifiedMatrix = copiedMatrix.stream()
                .map(l -> {
                    ObservableList<T> out = FXCollections.observableArrayList();
                    for (int i = 0; i < l.size(); i++) {
                        if (indices.contains(i)) continue;
                        out.add(l.get(i));
                    }
                    return out;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return modifiedMatrix;
    }

    /**
     * Inserts new columns at the given indices. Does not modify the given matrix.
     * Returns a modifed copy of the given container
     *
     * @param original 2D {@code ObservableList<T>} as container to modify
     * @param indices  a {@code List<Integer>} as given indices
     * @param <T>      the general container type
     * @return a 2D {@code ObservableList<T>} as resulting matrix.
     */
    public static <T> ObservableList<ObservableList<T>> insertColumnsAt(ObservableList<ObservableList<T>> original, List<Integer> indices, Supplier<T> defaultValue) {
        ObservableList<ObservableList<T>> out = original.stream().map(row -> {
            Iterator<T> rowIt = row.iterator();
            return IntStream.range(0, row.size() + indices.size())
                    .mapToObj(col -> (indices.contains(col)) ? defaultValue.get() : rowIt.next())
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }).collect(Collectors.toCollection(FXCollections::observableArrayList));

        return out;
    }

    public static <T> ObservableList<T> newRow(int len, Supplier<T> defaultValue) {
        if (len < 0) {
            throw new IllegalStateException("len < 0!");
        }
        if (len == 0) {
            return FXCollections.emptyObservableList();
        }
        final ObservableList<T> out = IntStream.range(0, len)
                .mapToObj(i -> defaultValue.get())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return out;
    }

    public static <T> ObservableList<ObservableList<T>> removeLastColumn(ObservableList<ObservableList<T>> original) {
        final int cols = original.get(0).size();
        ObservableList<ObservableList<T>> out = original.stream()
                .map(row -> FXCollections.observableArrayList(row.subList(0, cols - 1)))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return out;
    }

    public static <T> ObservableList<ObservableList<T>> insertRowsAt(ObservableList<ObservableList<T>> original, List<Integer> integers, Supplier<T> defaultValue) {
        final int len = original.get(0).size();
        Iterator<ObservableList<T>> it = original.iterator();
        ObservableList<ObservableList<T>> out = IntStream.range(0, original.size() + integers.size())
                .mapToObj(i -> (integers.contains(i)) ? (newRow(len, defaultValue)) : (it.next()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return out;
    }

}
