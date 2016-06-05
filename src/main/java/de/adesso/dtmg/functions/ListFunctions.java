package de.adesso.dtmg.functions;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * All additional List functions used in this program
 * Created by mmoehler ofList 20.02.16.
 */
public class ListFunctions {

    public static Function<List<List<String>>, List<List<Integer>>> indicesOfDuplicateActions() {

        return (actions) -> {
            final ListMultimap<List<String>, Integer> tmp =
                    Multimaps.newListMultimap(new HashMap<List<String>, Collection<Integer>>(), () -> new LinkedList<Integer>());

            IntStream.range(0,actions.size()).forEach(i -> tmp.put(actions.get(i), i));
            tmp.asMap().entrySet().removeIf(entry -> entry.getValue().size()<2);

            return tmp.asMap().values().stream()
                    .map(o -> o.stream()
                            .peek(System.out::println)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
        };
    }


    public static <T> List<T> insertElementsAt(List<T> original, int index, Supplier<T> defaultValue) {
        Iterator<T> it = original.iterator();
        return IntStream.range(0, original.size() + 1)
                .mapToObj(k -> (k == index) ? defaultValue.get() : it.next())
                .collect(Collectors.toList());
    }

    public static <T> List<T> removeElementsAt(List<T> original, int index) {
        return IntStream.range(0, original.size())
                .filter(i -> index != i)
                .mapToObj(original::get)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }

}
