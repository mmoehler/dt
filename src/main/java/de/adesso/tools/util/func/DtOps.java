package de.adesso.tools.util.func;

import com.google.common.collect.Lists;
import de.adesso.tools.ui.PossibleIndicatorsSupplier;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
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

    public static <T extends PossibleIndicatorsSupplier> int determineMaxColumns(List<T> indicators) {
        return indicators.stream()
                .map(x -> determineIndicatorsCount(x))
                .reduce(1, (y, z) -> y * z);
    }

    public static <T extends PossibleIndicatorsSupplier> List<Integer> determineCountIndicatorsPerRow(List<T> indicators) {
        return indicators.stream()
                .map(x -> determineIndicatorsCount(x))
                .collect(Collectors.toList());
    }

    public static <T extends PossibleIndicatorsSupplier> List<String[]> determineIndicatorArrayPerRow(List<T> indicators) {
        return indicators.stream()
                .map(x -> determineIndicators(x))
                .collect(Collectors.toList());
    }

    public static <T extends PossibleIndicatorsSupplier> List<List<String>> determineIndicatorListPerRow(List<T> indicators) {
        return indicators.stream()
                .map(x -> Arrays.stream(determineIndicators(x)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static <T extends PossibleIndicatorsSupplier> String[] determineIndicators(T x) {
        return x.possibleIndicatorsProperty().get().split(SPLITEX);
    }

    private static <T extends PossibleIndicatorsSupplier> int determineIndicatorsCount(T x) {
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
        transposed.forEach(l -> retList.add(FXCollections.observableArrayList(l)));
        return retList;
    }

    public static ObservableList<ObservableList<String>> fullExpandActions(List<ActionDeclTableViewModel> indicators, int countColumns) {
        // TODO Define Preconditions if neccessary!
        final ObservableList<ObservableList<String>> retList = FXCollections.observableArrayList();
        final int rowCount = indicators.size();

        if (0 < rowCount) {

            String[][] rawData = new String[rowCount][countColumns];
            final List<List<String>> transposed = Arrays.stream(rawData).map(s -> {
                Arrays.fill(s, QMARK);
                return new ArrayList<String>(Arrays.asList(s));
            }).collect(Collectors.toList());

            transposed.forEach(l -> {
                l.add(0, "");
                retList.add(FXCollections.observableArrayList(l));
            });

        }

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
        final int internalCountColumns = min(determineMaxColumns(indicators), countColumns);
        if (dontFillWithIndicators) {
            fullExpanded.forEach(x -> {
                Collections.fill(x, QMARK);
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

    public static ObservableList<ObservableList<String>> fillActions(List<ActionDeclTableViewModel> indicators, int countColumns) {
        final ObservableList<ObservableList<String>> retList = FXCollections.observableArrayList();
        final ObservableList<ObservableList<String>> fullExpanded = fullExpandActions(indicators, countColumns);
        final int internalCountColumns = countColumns;
        fullExpanded.forEach(l -> {
            if (l instanceof ObservableList) {
                retList.add((ObservableList) l);
            } else {
                retList.add(FXCollections.observableList(l));
            }
        });
        return retList;
    }

    public static <T> ObservableList<ObservableList<T>> transposeObservable(ObservableList<ObservableList<T>> table) {

        if (null == table) throw new IllegalArgumentException("Table to transpose is null");

        if (table.isEmpty()) return table;

        ObservableList<ObservableList<T>> transposedObservableList = FXCollections.observableArrayList();

        final int firstObservableListSize = table.get(0).size();
        for (int i = 0; i < firstObservableListSize; i++) {
            ObservableList<T> tempObservableList = FXCollections.observableArrayList();
            for (ObservableList<T> row : table) {
                tempObservableList.add(row.get(i));
            }
            transposedObservableList.add(tempObservableList);
        }
        return transposedObservableList;
    }


    public static <T> List<List<T>> transpose(List<List<T>> table) {

        if (null == table) throw new IllegalArgumentException("Table to transpose is null");

        if (table.isEmpty()) return table;

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
                                                                               Supplier<T> valueSupplier) {
        if (original.isEmpty()) {
            return original;
        }
        ObservableList<ObservableList<T>> copiedMatrix = copyMatrix(original);
        ObservableList<T> copiedRow = copyRow(original.get(0));
        Collections.fill(copiedRow, valueSupplier.get());
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

    public static <T> ObservableList<ObservableList<T>> copyMatrixWithoutColumnsWithIndex(ObservableList<ObservableList<T>> original, List<Integer> indices) {

        Collections.sort(indices, (a,b) -> b.intValue() - a.intValue());

        if (original.isEmpty()) {
            return original;
        }

        final ObservableList<ObservableList<T>> copiedMatrix = copyMatrix(original);
        final ObservableList<ObservableList<T>> modifiedMatrix = copiedMatrix.stream()
                .map(l -> removeAtIndices(l, indices))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        return modifiedMatrix;
    }

    public static <T> ObservableList<T> removeAtIndices(ObservableList<T> ol, List<Integer> indices) {
        Collections.sort(indices, (a,b) -> b.intValue() - a.intValue());
        ObservableList<T> out = FXCollections.observableArrayList();
        for (int i = 0; i < ol.size(); i++) {
            if(indices.contains(i)) continue;
            out.add(ol.get(i));
        }
        return out;
    }

}