package de.adesso.tools.util.func;

import com.google.common.collect.Lists;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Created by moehler on 11.01.2016.
 */
public final class DtOps {

    public static final String EMPTY_STRING = "";
    public static final String IRRELEVANT = "-";

    private DtOps() {
    }

    public static int determineMaxColumns(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> x.possibleIndicatorsProperty().get().split("[,]").length)
                .reduce(1, (y, z) -> y * z);
    }

    public static List<Integer> determineCountIndicatorsPerRow(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> x.possibleIndicatorsProperty().get().split("[,;]").length)
                .collect(Collectors.toList());
    }

    public static List<String[]> determineIndicatorArrayPerRow(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> x.possibleIndicatorsProperty().get().split("[,;]"))
                .collect(Collectors.toList());
    }

    public static List<List<String>> determineIndicatorListPerRow(List<ConditionDeclTableViewModel> indicators) {
        return indicators.stream()
                .map(x -> Arrays.stream(x.possibleIndicatorsProperty().get().split("[,;]")).collect(Collectors.toList()))
                .collect(Collectors.toList());
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
        final List<List<String>> transposed = returnTranspose(permutations);
        transposed.forEach(l -> retList.add(FXCollections.observableArrayList(l)));
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

        System.err.printf(">>> %s\n", fullExpanded);

        final int internalCountColumns = min(determineMaxColumns(indicators), countColumns);

        System.err.printf(">>> %d - %d -%d\n", determineMaxColumns(indicators), countColumns, internalCountColumns);

        if (dontFillWithIndicators) {
            fullExpanded.forEach(x -> Collections.fill(x, IRRELEVANT));
        }
        fullExpanded.forEach(l -> {
            List<String> subList = l.subList(0, internalCountColumns);
            if (subList instanceof ObservableList) {
                retList.add((ObservableList)subList);
            } else {
                retList.add(FXCollections.observableList(subList));
            }
        });
        System.err.println(">>> "+retList);
        return retList;
    }

    public static ObservableList<ObservableList<String>> initialConditions(ObservableList<ObservableList<String>> indicators) {
        final int rows = indicators.size();
        ObservableList<ObservableList<String>> retList = FXCollections.observableArrayList();
        for (int r = 0; r < rows; r++) {
            retList.add(FXCollections.observableArrayList());
        }
        retList.forEach(e -> e.add("-"));
        return retList;
    }

    public static <T> List<List<T>> returnTranspose(List<List<T>> table) {
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
}