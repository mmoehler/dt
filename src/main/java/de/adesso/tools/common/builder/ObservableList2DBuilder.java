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

import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.functions.ObservableList2DFunctions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.adesso.tools.functions.MoreCollectors.toObservableList;

/**
 * A List2D builder
 * A List2D builder
 * Created by mohler ofList 25.01.16.
 */
public class ObservableList2DBuilder {
    private String data;
    private List<String> dataList;
    private int n;
    private int m;
    private boolean transposed;

    protected ObservableList2DBuilder() {
    }

    protected ObservableList2DBuilder(String data) {
        this.data = data;
    }

    protected ObservableList2DBuilder(List<String> data) {
        this.dataList = data;
    }

    public static ObservableList2DBuilder empty() {
        return new ObservableList2DBuilder();
    }

    public static ObservableList2DBuilder observable2DOf(@javax.annotation.Nonnull String data) {
        return new ObservableList2DBuilder(data);
    }

    public static ObservableList2DBuilder observable2DOf(@javax.annotation.Nonnull List<String> data) {
        return new ObservableList2DBuilder(data);
    }

    public static ObservableList2DBuilder observable2DOf(@javax.annotation.Nonnull String[] data) {
        List<String> dataList = Arrays.stream(data).collect(Collectors.toList());
        return new ObservableList2DBuilder(dataList);
    }

    public static ObservableList2DBuilder copy(@javax.annotation.Nonnull ObservableList2DBuilder copy) {
        ObservableList2DBuilder builder = (copy.dataList == null) ? new ObservableList2DBuilder(copy.data) : new ObservableList2DBuilder(copy.dataList);
        builder.n = copy.n;
        builder.m = copy.m;
        builder.transposed = copy.transposed;
        return builder;
    }

    /**
     * Sets the {@code n} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param cols the {@code n} to set
     * @param rows the {@code m} to set
     * @return a reference to this Builder
     */
    @javax.annotation.Nonnull
    public ObservableList2DBuilder dim(int rows, int cols) {
        this.m = rows;
        this.n = cols;
        return this;
    }

    public ObservableList2DBuilder transposed() {
        this.transposed = true;
        return this;
    }

    /**
     * Returns a {@code List2DBuilder} built from the parameters previously set.
     *
     * @return a {@code List2DBuilder} built with parameters of this {@code List2DBuilder.Builder}
     */
    @javax.annotation.Nonnull
    public ObservableList<ObservableList<String>> build() {
        if (null == data & null == dataList) {
            return FXCollections.emptyObservableList();
        }
        ObservableList<ObservableList<String>> partitioned = (null == this.dataList) ? build4DataString() : build4DataList();

        return partitioned;
    }

    private ObservableList<ObservableList<String>> build4DataString() {
        final List<String> rawData = Arrays
                .stream(this.data.split("[, ;]"))
                .collect(toObservableList());
        return partitionIt(rawData);
    }

    private ObservableList<ObservableList<String>> build4DataList() {
        final List<String> rawData = dataList;
        return partitionIt(rawData);
    }

    private ObservableList<ObservableList<String>> partitionIt(List<String> rawData) {
        final int rdSize = rawData.size();

        ObservableList<ObservableList<String>> lists = IntStream.range(0, (rdSize - 1) / n + 1)
                .mapToObj(i -> FXCollections.observableArrayList(rawData.subList(i *= n,
                        rdSize - n >= i ? i + n : rdSize)))
                .collect(toObservableList());

        if (transposed) {
            lists = Stream.of(lists).map(ObservableList2DFunctions.transpose()).collect(MoreCollectors.toSingleObject());
        }

        return lists;

    }
}
