package de.adesso.tools.functions;

import com.google.common.collect.Lists;
import de.adesso.tools.ui.DeclarationTableViewModel;
import de.adesso.tools.ui.DeclarationsTableCell;
import de.adesso.tools.ui.DefinitionsTableCell;
import de.adesso.tools.ui.PossibleIndicatorsSupplier;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.functions.MatrixFunctions.*;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;

/**
 * Created by moehler on 11.01.2016.
 */
public final class DtFunctions {

    public static final String EMPTY_STRING = "";
    public static final String IRRELEVANT = "-";
    public static final String SPLITEX = "[,;]";
    public static final String QMARK = "?";
    public static final String RULE_HEADER = "R%02d";
    public static final String ELSE_RULE_HEADER = "ELSE";


    private DtFunctions() {
    }

    public static <T extends PossibleIndicatorsSupplier> int determineMaxColumns(List<T> indicators) {
        return indicators.stream()
                .map(x -> determineIndicatorsCount(x))
                .reduce(1, (y, z) -> y * z);
    }

    public static <T extends PossibleIndicatorsSupplier> List<Integer> determineCountIndicatorsPerRow(List<T> indicators) {
        return indicators.stream()
                .map(x -> determineIndicatorsCount(x))
                .collect(toList());
    }

    public static <T extends PossibleIndicatorsSupplier> List<String[]> determineIndicatorArrayPerRow(List<T> indicators) {
        return indicators.stream()
                .map(x -> determineIndicators(x))
                .collect(toList());
    }

    public static <T extends PossibleIndicatorsSupplier> List<List<String>> determineIndicatorListPerRow(List<T> indicators) {
        return indicators.stream()
                .map(x -> Arrays.stream(determineIndicators(x)).collect(toList()))
                .collect(toList());
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
            }).collect(toList());

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


    public static void doInsertColumns(ObservableList<ObservableList<String>> conditionDefns,
                                       ObservableList<ObservableList<String>> actionDefns,
                                       TableView conditionTable,
                                       TableView actionTable,
                                       Object[] value, Supplier<String> defaultDefValue) {

        final List<Integer> indices = determineColumnIndices(conditionTable, actionTable, value);
        if (!indices.isEmpty()) {

            int newCols = conditionTable.getColumns().size() - indices.size();
            conditionTable.getColumns().clear();
            actionTable.getColumns().clear();

            IntStream.range(0, newCols).forEach(i -> {
                conditionTable.getColumns().add(createTableColumn(i));
                actionTable.getColumns().add(createTableColumn(i));
            });

            final ObservableList<ObservableList<String>> newConDefs = MatrixFunctions.insertColumnsAt(conditionDefns, indices, () -> "?");
            final ObservableList<ObservableList<String>> newActDefs = MatrixFunctions.insertColumnsAt(actionDefns, indices, () -> "?");

            conditionDefns.clear();
            newConDefs.forEach(conditionDefns::add);
            actionDefns.clear();
            newActDefs.forEach(actionDefns::add);

            conditionTable.refresh();
            actionTable.refresh();
        }

    }

    public static <T extends DeclarationTableViewModel, R, C, D> void doInsertRows(ObservableList<T> decls,
                                                                                   ObservableList<ObservableList<R>> defns,
                                                                                   TableView<C> declarations,
                                                                                   TableView<D> definitions,
                                                                                   Object[] value, Supplier<T> defaultDecl, Supplier<R> defaultDefValue) {

        List<Integer> indices = determineRowIndices(declarations, definitions, value);

        if (!indices.isEmpty()) {

            ObservableList<T> newDecls = ListFunctions.insertElementsAt(decls, indices, defaultDecl);
            ObservableList<ObservableList<R>> newDefs = insertRowsAt(defns, indices, defaultDefValue);

            decls.clear();
            newDecls.forEach(decls::add);
            defns.clear();
            newDefs.forEach(defns::add);

            declarations.refresh();
            definitions.refresh();
        }
    }

    public static void doRemoveColumns(ObservableList<ObservableList<String>> conditionDefns,
                                       ObservableList<ObservableList<String>> actionDefns,
                                       TableView<ObservableList<String>> conditionTable,
                                       TableView<ObservableList<String>> actionTable,
                                       Object[] value) {


        final List<Integer> indices = determineColumnIndices(conditionTable, actionTable, value);
        if (!indices.isEmpty()) {

            int newCols = conditionTable.getColumns().size() - indices.size();
            conditionTable.getColumns().clear();
            actionTable.getColumns().clear();

            IntStream.range(0, newCols).forEach(i -> {
                conditionTable.getColumns().add(createTableColumn(i));
                actionTable.getColumns().add(createTableColumn(i));
            });

            final ObservableList<ObservableList<String>> newConDefs = MatrixFunctions.removeColumnsAt(conditionDefns, indices);
            final ObservableList<ObservableList<String>> newActDefs = MatrixFunctions.removeColumnsAt(actionDefns, indices);

            conditionDefns.clear();
            newConDefs.forEach(conditionDefns::add);
            actionDefns.clear();
            newActDefs.forEach(actionDefns::add);

            conditionTable.refresh();
            actionTable.refresh();
        }
    }

    public static <T extends DeclarationTableViewModel, R, C> void doRemoveRows(ObservableList<T> decls,
                                                                                ObservableList<ObservableList<R>> defns,
                                                                                TableView<C> declarations,
                                                                                TableView<C> definitions, Object[] value) {

        List<Integer> indices = determineRowIndices(declarations, definitions, value);

        if (!indices.isEmpty()) {

            ObservableList<T> newDecls = ListFunctions.removeElementsAt(decls, indices);
            ObservableList<ObservableList<R>> newDefs = removeRowsAt(defns, indices);

            decls.clear();
            newDecls.forEach(decls::add);
            defns.clear();
            newDefs.forEach(defns::add);

            Arrays.asList(declarations, definitions).forEach(TableView::refresh);

        }
    }

    public static void doMoveColumns(ObservableList<ObservableList<String>> conditionDefinitions,
                                     ObservableList<ObservableList<String>> actionDefinitions,
                                     TableView conditionTable, TableView actionTable, Object[] value,
                                     boolean toRight) {
        final List<Integer> indices = determineColumnIndices(conditionTable, actionTable, value);
        if (!indices.isEmpty()) {
            final int c1Idx = indices.get(0);
            int c2Idx = 0;
            if (toRight) {
                if (c1Idx < conditionTable.getColumns().size() - 1) {
                    c2Idx = (c1Idx + 1);
                }

            } else {
                if (c1Idx > 0) {
                    c2Idx = (c1Idx - 1);
                }
            }
            ObservableList<ObservableList<String>> newConditionDefns = swapColumnsAt(conditionDefinitions, c1Idx, c2Idx);
            ObservableList<ObservableList<String>> newActionDefns = swapColumnsAt(actionDefinitions, c1Idx, c2Idx);
            conditionDefinitions.clear();
            newConditionDefns.forEach(conditionDefinitions::add);
            actionDefinitions.clear();
            newActionDefns.forEach(actionDefinitions::add);
        }

    }

    public static void doMoveRows(ObservableList<ObservableList<String>> conditionDefinitions,
                            ObservableList<ObservableList<String>> actionDefinitions,
                            TableView conditionTable,
                            TableView actionTable, Object[] value, boolean toUpper) {
        final List<Integer> indices = determineRowIndices(conditionTable, actionTable, value);
        if (!indices.isEmpty()) {
            int r2Idx = 0;
            final int r1Idx = indices.get(0);
            if (toUpper) {
                if (r1Idx > 0) {
                    r2Idx = (r1Idx - 1);
                }

            } else {
                if (r1Idx < conditionTable.getColumns().size() - 1) {
                    r2Idx = (r1Idx + 1);
                }
            }

            ObservableList<ObservableList<String>> newConditionDefns = swapColumnsAt(conditionDefinitions, r1Idx, r2Idx);
            ObservableList<ObservableList<String>> newActionDefns = swapColumnsAt(actionDefinitions, r1Idx, r2Idx);
            conditionDefinitions.clear();
            newConditionDefns.forEach(conditionDefinitions::add);
            actionDefinitions.clear();
            newActionDefns.forEach(actionDefinitions::add);
        }

    }

    private static <T, U> List<Integer> determineColumnIndices(TableView<T> tableView0, TableView<U> tableView1, Object[] value) {
        List<TablePosition> indices = determineIndices(tableView0, tableView1, value);
        return indices.stream().map(t -> t.getColumn()).collect(Collectors.toList());
    }

    private static <T, U> List<Integer> determineRowIndices(TableView<T> tableView0, TableView<U> tableView1, Object[] value) {
        List<TablePosition> indices = determineIndices(tableView0, tableView1, value);
        return indices.stream().map(t -> t.getRow()).collect(Collectors.toList());
    }

    private static <T, U> List<TablePosition> determineIndices(TableView<T> tableView0, TableView<U> tableView1, Object[] value) {
        List<TablePosition> indices = Collections.emptyList();
        if (null != value && value.length == 1) {
            // indices = (List<Integer>) value[0];
        } else {
            Optional<TablePosition> cellPos = getSelectedCell(tableView0);
            indices = new ArrayList<>(1);
            if (cellPos.isPresent()) {
                indices.add(cellPos.get());
            } else {
                cellPos = getSelectedCell(tableView1);
                if (cellPos.isPresent()) {
                    indices.add(cellPos.get());
                }
            }
        }
        return indices;
    }


    public static <T> void updateColHeaders(TableView<T>... tables) {
        Arrays.stream(tables).forEach(t -> {
            int counter[] = {1};
            t.getColumns().forEach(c -> c.setText(String.format(RULE_HEADER, counter[0]++)));
        });
    }

    public static <C> Optional<TablePosition> getSelectedCell(TableView<C> table) {
        final ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        return (selectedCells.isEmpty()) ? Optional.empty() : Optional.of(selectedCells.get(0));
    }

    public static TableColumn<ObservableList<String>, String> createTableColumn(int x) {

        String tpl = RULE_HEADER;
        TableColumn<ObservableList<String>, String> tc = new TableColumn(String.format(tpl, x + 1));

        tc.setCellFactory(DefinitionsTableCell.forTableColumn());

        tc.setOnEditCommit(
                (t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).set(t.getTablePosition().getColumn(), t.getNewValue());
                });

        tc.setCellValueFactory(
                (features) -> new SimpleStringProperty((features.getValue().get(x))
                ));

        tc.setPrefWidth(40);
        tc.setMinWidth(40);
        tc.setResizable(false);

        return tc;
    }

    public static <S> TableColumn<S, String> createTableColumn(String columnName, String propertyName, int prefWidth,
                                                               int minWidth, int maxWidth, boolean resizable,
                                                               Pos alignment, EventHandler<TableColumn.CellEditEvent<S, String>> value) {
        TableColumn<S, String> col = new TableColumn<>(columnName);
        col.setMinWidth(minWidth);
        col.setPrefWidth(prefWidth);
        col.setMaxWidth(maxWidth);
        col.setResizable(resizable);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(DeclarationsTableCell.forTableColumn(alignment));
        col.setOnEditCommit(value);
        return col;
    }
}