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

package de.adesso.tools.common.builder;

import de.adesso.tools.functions.List2DFunctions;
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
 * Created by mohler ofList 25.01.16.
 */
public class List2DBuilder {
    private String data;
    private List<String> dataList;
    private int n;
    private int m;
    private boolean transposed;

    protected List2DBuilder() {
        ;
    }

    protected List2DBuilder(String data) {
        this.data = data;
    }

    protected List2DBuilder(List<String> data) {
        this.dataList = data;
    }

    public static List2DBuilder empty() {
        return new List2DBuilder();
    }

    public static List2DBuilder matrixOf(@javax.annotation.Nonnull String data) {
        return new List2DBuilder(data);
    }

    public static List2DBuilder matrixOf(@javax.annotation.Nonnull List<String> data) {
        return new List2DBuilder(data);
    }

    public static List2DBuilder matrixOf(@javax.annotation.Nonnull String[] data) {
        List<String> dataList = Arrays.stream(data).collect(Collectors.toList());
        return new List2DBuilder(dataList);
    }

    public static List2DBuilder copy(@javax.annotation.Nonnull List2DBuilder copy) {
        List2DBuilder builder = (copy.dataList == null) ? new List2DBuilder(copy.data) : new List2DBuilder(copy.dataList);
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
    public List2DBuilder dim(int rows, int cols) {
        this.m = rows;
        this.n = cols;
        return this;
    }

    public List2DBuilder transposed() {
        this.transposed = true;
        return this;
    }

    /**
     * Returns a {@code List2DBuilder} built from the parameters previously set.
     *
     * @return a {@code List2DBuilder} built with parameters of this {@code List2DBuilder.Builder}
     */
    @javax.annotation.Nonnull
    public List<List<String>> build() {
        if (null == data & null == dataList) {
            return emptyMatrix(m);
        }
        final List<List<String>> partitioned = (null == this.dataList) ? build4DataString() : build4DataList();
        return (transposed) ? (List2DFunctions.transpose(partitioned)) : partitioned;
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
