package de.adesso.tools.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by mmoehler on 20.02.16.
 */
public class ListFunctions {
    public static <T> ObservableList<T> insertElementsAt(ObservableList<T> original, List<Integer> indices, Supplier<T> defaultValue) {
        Iterator<T> it = original.iterator();
        ObservableList<T> out = IntStream.range(0, original.size() + indices.size())
                .mapToObj(k -> indices.contains(k) ? defaultValue.get() : it.next())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        return out;
    }

    public static <T> ObservableList<T> removeElementsAt(ObservableList<T> original, List<Integer> indices) {
        ObservableList<T> out = IntStream.range(0, original.size())
                .filter(i -> !indices.contains(i))
                .mapToObj(k -> original.get(k))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        /*

        Collections.sort(indices, (a, b) -> b.intValue() - a.intValue());
        ObservableList<T> out = FXCollections.observableArrayList();
        for (int i = 0; i < original.size(); i++) {
            if(indices.contains(i)) continue;
            out.add(original.get(i));
        }

        */

        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }
}
