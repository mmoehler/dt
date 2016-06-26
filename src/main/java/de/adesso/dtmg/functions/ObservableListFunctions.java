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

package de.adesso.dtmg.functions;

import com.google.common.base.Preconditions;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.function.Function;

/**
 * All additional List functions used in this program
 * Created by mmoehler ofList 20.02.16.
 */
public final class ObservableListFunctions {
    /**
     * take n, applied to a list xs, returns the prefix of xs of length n, or xs itself if n > length xs:
     */
    public static <E> ObservableList<E> take(ObservableList<E> ol, int count) {
        if (count <= 0 || ol.size() <= count) return FXCollections.emptyObservableList();
        if (count > ol.size()) return FXCollections.observableArrayList(ol);
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(ol.subList(0, count - 1)));
    }

    /**
     * drop n xs returns the suffix of xs after the first n elements, or [] if n > length xs
     */
    public static <E> ObservableList<E> drop(ObservableList<E> ol, int count) {
        if (count <= 0) return FXCollections.observableArrayList(ol);
        if (count >= ol.size()) return FXCollections.emptyObservableList();
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(ol.subList(count, ol.size() - 1)));
    }

    /**
     * splitAt n xs returns a tuple where first element is xs prefix of length n and second element is the remainder of the lis
     */
    public static <E> Tuple2<ObservableList<E>, ObservableList<E>> splitAt(ObservableList<E> ol, int count) {
        if (count <= 0) return Tuple.of(FXCollections.emptyObservableList(), ol);
        if (count >= ol.size()) return Tuple.of(ol, FXCollections.emptyObservableList());
        return Tuple.of(take(ol, count), drop(ol, count));
    }

    public static <E> ObservableList<E> concat(ObservableList<E> ol, ObservableList<E> or) {
        return FXCollections.unmodifiableObservableList(FXCollections.concat(ol, or));
    }

    /**
     * Extract the first element of a list, which must be non-empty.
     */
    public static <E> E head(ObservableList<E> ol) {
        Preconditions.checkArgument(!ol.isEmpty());
        return ol.get(0);
    }

    public static <E> Function<ObservableList<E>,E> head() {
        return l  -> head(l);
    }

    /**
     * Extract the elements after the head of a list, which must be non-empty.
     */
    public static <E> ObservableList<E> tail(ObservableList<E> ol) {
        Preconditions.checkArgument(!ol.isEmpty());
        return drop(ol, 1);
    }

    public static <E> Function<ObservableList<E>,ObservableList<E>> tail() {
        return l  -> tail(l);
    }

    /**
     * Extract the last element of a list, which must be finite and non-empty.
     */
    public static <E> E last(ObservableList<E> ol) {
        Preconditions.checkArgument(!ol.isEmpty());
        return ol.get(ol.size() - 1);
    }

    public static <E> Function<ObservableList<E>,E> last() {
        return l  -> last(l);
    }

    /**
     * Return all the elements of a list except the last one. The list must be non-empty.
     */
    public static <E> ObservableList<E> init(ObservableList<E> ol) {
        Preconditions.checkArgument(!ol.isEmpty());
        return take(ol, ol.size() - 1);
    }

    public static <E> Function<ObservableList<E>,ObservableList<E>> init() {
        return l  -> init(l);
    }

    private ObservableListFunctions() {
    }
}

