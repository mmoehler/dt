package de.adesso.tools.common;

import de.adesso.tools.util.matrix.Matrix;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public static MatrixBuilder copy(@javax.annotation.Nonnull MatrixBuilder copy) {
        MatrixBuilder builder = new MatrixBuilder(copy.data);
        builder.n = copy.n;
        builder.m = copy.m;
        builder.transposed = copy.transposed;
        return builder;
    }

    public static <T> ObservableList<ObservableList<T>> observable(List<List<T>> l) {
        return l.stream()
                .map(i -> i.stream()
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
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
                .stream(this.data.split("[, ;]"))
                .collect(Collectors.toList());

        final int rdSize = rawData.size();
        final List<List<String>> partitioned =
                IntStream.range(0, (rdSize - 1) / n + 1)
                        .mapToObj(i -> rawData.subList(i *= n,
                                rdSize - n >= i ? i + n : rdSize))
                        .collect(Collectors.toList());

        return (transposed) ? (Matrix.transpose(partitioned)) : partitioned;
    }


}
