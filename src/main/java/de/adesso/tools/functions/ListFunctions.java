package de.adesso.tools.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * All additional List functions used in this program
 * Created by mmoehler on 20.02.16.
 */
public class ListFunctions {
    public static <T> ObservableList<T> insertElementsAt(ObservableList<T> original, int index, Supplier<T> defaultValue) {
        Iterator<T> it = original.iterator();
        return IntStream.range(0, original.size() + 1)
                .mapToObj(k -> (k == index) ? defaultValue.get() : it.next())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static <T> ObservableList<T> removeElementsAt(ObservableList<T> original, int index) {
        return IntStream.range(0, original.size())
                .filter(i -> index != i)
                .mapToObj(original::get)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }
}
