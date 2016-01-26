package de.adesso.tools.common;

import de.adesso.tools.util.func.DtOps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mohler on 25.01.16.
 */
public final class MatrixBuilder {
    private String data;
    private int n;
    private int m;
    private boolean transposed;

    private MatrixBuilder(String data) {
        this.data = data;
    }

    public static MatrixBuilder on(@javax.annotation.Nonnull String data) {
        return new MatrixBuilder(data);
    }

    public static MatrixBuilder prototype(@javax.annotation.Nonnull MatrixBuilder copy) {
        MatrixBuilder builder = new MatrixBuilder(copy.data);
        builder.n = copy.n;
        builder.m = copy.m;
        builder.transposed = copy.transposed;
        return builder;
    }

    private static <T> List<List<T>> transpose(List<List<T>> table) {
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

    private static <T> ObservableList<ObservableList<T>> transpose(ObservableList<ObservableList<T>> table) {
        ObservableList<ObservableList<T>> transposedList = FXCollections.observableArrayList();

        final int firstListSize = table.get(0).size();
        for (int i = 0; i < firstListSize; i++) {
            ObservableList<T> tempList = FXCollections.observableArrayList();
            for (ObservableList<T> row : table) {
                tempList.add(row.get(i));
            }
            transposedList.add(tempList);
        }
        return transposedList;
    }

    /**
     * Sets the {@code n} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param cols the {@code n} to set
     * @param rows the {@code m} to set
     * @return a reference to this Builder
     */
    @javax.annotation.Nonnull
    public MatrixBuilder dim(int rows, int cols) {
        this.m = rows;
        this.n = cols;
        return this;
    }

    public MatrixBuilder transposed() {
        this.transposed = true;
        return this;
    }

    /**
     * Returns a {@code MatrixBuilder} built from the parameters previously set.
     *
     * @return a {@code MatrixBuilder} built with parameters of this {@code MatrixBuilder.Builder}
     */
    @javax.annotation.Nonnull
    public List<List<String>> build() {
        final List<String> rawData = Arrays
                .stream(this.data.split("[, ;]]"))
                .collect(Collectors.toList());

        final int rdSize = rawData.size();

        final List<List<String>> partitioned =
                IntStream.range(0, (rdSize - 1) / n + 1)
                        .mapToObj(i -> rawData.subList(i *= n,
                                rdSize - n >= i ? i + n : rdSize))
                        .collect(Collectors.toList());

        return (transposed) ? (DtOps.transpose(partitioned)) : partitioned;
    }

    /**
     * Returns a {@code MatrixBuilder} built from the parameters previously set.
     *
     * @return a {@code MatrixBuilder} built with parameters of this {@code MatrixBuilder.Builder}
     */
    @javax.annotation.Nonnull
    public ObservableList<ObservableList<String>> buildObservable() {
        final ObservableList<String> rawData = Arrays
                .stream(this.data.split("[, ;]]"))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        final int rdSize = rawData.size();

        final ObservableList<ObservableList<String>> partitioned =
                IntStream.range(0, (rdSize - 1) / n + 1)
                        .mapToObj(i -> (ObservableList<String>) rawData.subList(i *= n,
                                rdSize - n >= i ? i + n : rdSize))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

        return (transposed) ? (transpose(partitioned)) : partitioned;
    }

}
