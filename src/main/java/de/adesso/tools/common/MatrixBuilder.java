package de.adesso.tools.common;

import de.adesso.tools.functions.MatrixFunctions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A List2D builder
 * A List2D builder
 * Created by mohler on 25.01.16.
 */
public class MatrixBuilder {
    private String data;
    private List<String> dataList;
    private int n;
    private int m;
    private boolean transposed;

    protected MatrixBuilder() {
        ;
    }

    protected MatrixBuilder(String data) {
        this.data = data;
    }

    protected MatrixBuilder(List<String> data) {
        this.dataList = data;
    }

    public static MatrixBuilder empty() {
        return new MatrixBuilder();
    }

    public static MatrixBuilder on(@javax.annotation.Nonnull String data) {
        return new MatrixBuilder(data);
    }

    public static MatrixBuilder on(@javax.annotation.Nonnull List<String> data) {
        return new MatrixBuilder(data);
    }

    public static MatrixBuilder on(@javax.annotation.Nonnull String[] data) {
        List<String> dataList = Arrays.stream(data).collect(Collectors.toList());
        return new MatrixBuilder(dataList);
    }

    public static MatrixBuilder copy(@javax.annotation.Nonnull MatrixBuilder copy) {
        MatrixBuilder builder = (copy.dataList == null) ? new MatrixBuilder(copy.data) : new MatrixBuilder(copy.dataList);
        builder.n = copy.n;
        builder.m = copy.m;
        builder.transposed = copy.transposed;
        return builder;
    }

    public static ObservableList<ObservableList<String>> observable(List<List<String>> l) {
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
        if (null == data & null == dataList) {
            return emptyMatrix(m);
        }
        final List<List<String>> partitioned = (null == this.dataList) ? build4DataString() : build4DataList();
        return (transposed) ? (MatrixFunctions.transpose(partitioned)) : partitioned;
    }

    private List<List<String>> emptyMatrix(int rows) {
        return IntStream.range(0, rows)
                .mapToObj(ArrayList<String>::new)
                .collect(Collectors.toCollection(ArrayList<List<String>>::new));

    }

    private List<List<String>> build4DataString() {
        final List<String> rawData = Arrays
                .stream(this.data.split("[, ;]"))
                .collect(Collectors.toList());
        return partitionIt(rawData);
    }

    private List<List<String>> build4DataList() {
        final List<String> rawData = dataList;
        return partitionIt(rawData);
    }

    private List<List<String>> partitionIt(List<String> rawData) {
        final int rdSize = rawData.size();
        return IntStream.range(0, (rdSize - 1) / n + 1)
                .mapToObj(i -> rawData.subList(i *= n,
                        rdSize - n >= i ? i + n : rdSize))
                .collect(Collectors.toList());
    }
}
