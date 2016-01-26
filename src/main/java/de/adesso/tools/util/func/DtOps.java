package de.adesso.tools.util.func;

import com.google.common.collect.Lists;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Created by moehler on 11.01.2016.
 */
public final class DtOps {

    public static final String EMPTY_STRING = "";
    public static final String IRRELEVANT = "-";
    public static final String SPLITEX = "[,;]";
    public static final String QMARK = "?";


    private DtOps() {
    }

    public static int determineMaxColumns(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> determineIndicatorsCount(x))
                .reduce(1, (y, z) -> y * z);
    }

    public static List<Integer> determineCountIndicatorsPerRow(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> determineIndicatorsCount(x))
                .collect(Collectors.toList());
    }

    public static List<String[]> determineIndicatorArrayPerRow(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> determineIndicators(x))
                .collect(Collectors.toList());
    }

    public static List<List<String>> determineIndicatorListPerRow(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> Arrays.stream(determineIndicators(x)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static String[] determineIndicators(ConditionDeclTableViewModel x) {
        return x.possibleIndicatorsProperty().get().split(SPLITEX);
    }

    private static int determineIndicatorsCount(ConditionDeclTableViewModel x) {
        return determineIndicators(x).length;
    }


    /**
     * Retrns a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view.
     * This matriyx contains all possible compinations of the given condition indicators a conditions block of this decision table
     *
     * @param indicators a list of {@link ConditionDeclTableViewModel}s as declaration of the different conditions
     * @return a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view
     */
    public static ObservableList<ObservableList<String>> fullExpandConditions(List<ConditionDeclTableViewModel> indicators) {
        // TODO Define Preconditions if neccessary!
        final ObservableList<ObservableList<String>> retList = FXCollections.observableArrayList();
        final List<List<String>> rawIndicators = determineIndicatorListPerRow(indicators);
        final List<List<String>> permutations = permutations(rawIndicators);
        final List<List<String>> transposed = transpose(permutations);
        transposed.forEach(l -> {
            l.add(0,"");
            retList.add(FXCollections.observableArrayList(l));
        });
        return retList;
    }

    /**
     * @param indicators   a list of {@link ConditionDeclTableViewModel}s as declaration of the different conditions
     * @param countColumns an {@code int} as cont of columns which sould be generated and initalized
     * @return a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view
     */
    public static ObservableList<ObservableList<String>> limitedExpandConditions(List<ConditionDeclTableViewModel> indicators, int countColumns) {
        return limitedExpandConditions(indicators, countColumns, false);
    }

    /**
     * @param indicators             a list of {@link ConditionDeclTableViewModel}s as declaration of the different conditions
     * @param countColumns           an {@code int} as cont of columns which sould be generated and initalized
     * @param dontFillWithIndicators if {@code true} then all cells of the returned matrix are filled with empty strings
     *                               otherwise the generated indicaqtor permutations are used for the initialization of te matrix
     * @return a 2d matrix composed by nested {@link ObservableList}s as data prepared for the condition tavke view
     */
    public static ObservableList<ObservableList<String>> limitedExpandConditions(List<ConditionDeclTableViewModel> indicators, int countColumns, boolean dontFillWithIndicators) {
        final ObservableList<ObservableList<String>> retList = FXCollections.observableArrayList();
        final ObservableList<ObservableList<String>> fullExpanded = fullExpandConditions(indicators);
        final int internalCountColumns = min(determineMaxColumns(indicators), countColumns) + 1; // +1 for else rule
        if (dontFillWithIndicators) {
            fullExpanded.forEach(x -> {
                Collections.fill(x, QMARK);
                x.set(0,EMPTY_STRING); // .. the else rule
            });
        }
        fullExpanded.forEach(l -> {
            List<String> subList = l.subList(0, internalCountColumns);
            if (subList instanceof ObservableList) {
                retList.add((ObservableList) subList);
            } else {
                retList.add(FXCollections.observableList(subList));
            }
        });
        return retList;
    }

    public static <T> List<List<T>> transpose(List<List<T>> table) {
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

    public static <T> List<List<T>> permutations(List<List<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<List<T>> res = Lists.newLinkedList();
            recursivePermutation(collections, res, 0, new LinkedList<T>());
            return res;
        }
    }

    /**
     * Recursive implementation for {@link #permutations(List)} }
     */
    private static <T> void recursivePermutation(List<? extends List<T>> ori, List<List<T>> res, int d, List<T> current) {
        if (d == ori.size()) {
            res.add(current);
            return;
        }
        List<? extends T> currentCollection = ori.get(d);
        for (T element : currentCollection) {
            List<T> copy = Lists.newLinkedList(current);
            copy.add(element);
            recursivePermutation(ori, res, d + 1, copy);
        }
    }

    /**
     * Copies a nested {@code ObservableList}
     *
     * @param original the ObservableList which should be copied
     * @param <T>
     * @return an ObservableList as copyMatrix of {@code src}
     */
    public static <T> ObservableList<ObservableList<T>> copyMatrix(ObservableList<ObservableList<T>> original) {
        return original.stream()
                .map(e -> e.stream()
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)))
                .collect(Collectors.toCollection(() -> FXCollections.observableArrayList()));
    }

    public static <T> ObservableList<T> copyRow(ObservableList<T> original) {
        return original.stream()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static <T> ObservableList<ObservableList<T>> copyMatrixWithAddedRow(ObservableList<ObservableList<T>> original,
                                                                               Supplier<T> valueSupplier,
                                                                               Supplier<T> noValueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<T>> copiedMatrix = copyMatrix(original);
        ObservableList<T> copiedRow = copyRow(original.get(0));
        Collections.fill(copiedRow, valueSupplier.get());
        copiedRow.set(0,noValueSupplier.get()); // the else rule
        copiedMatrix.add(copiedRow);
        return copiedMatrix;
    }

    public static <T> ObservableList<ObservableList<T>> copyMatrixWithAddedColumn(ObservableList<ObservableList<T>> original, Supplier<T> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<T>> copiedMatrix = copyMatrix(original);
        copiedMatrix.stream().forEach(l -> l.add(valueSupplier.get()));
        return copiedMatrix;
    }

}