package de.adesso.tools.analysis;

import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple3;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.util.func.DtOps.*;

/**
 * Created by mohler on 24.01.16.
 */
public final class ConditionCompletenessCheck {

    public static final String IRRELEVANT = "-";

    private ConditionCompletenessCheck() {
        super();
    }

    /**
     * Cmpleteness I - 
     * @param decls
     * @param defns
     * @return
     */
    public static Tuple3<Boolean, Integer, Integer> isFormalComplete(ObservableList<ConditionDeclTableViewModel> decls, ObservableList<ObservableList<String>> defns) {
        final List<Integer> indicatorsPerRow = determineCountIndicatorsPerRow(decls);
        final List<List<Integer>> list = IntStream.range(0, defns.size()).mapToObj(i -> defns.get(i).stream().map(j -> {
            if (IRRELEVANT.equals(j)) {
                return indicatorsPerRow.get(i);
            }
            return 1;
        }).collect(Collectors.toList())).collect(Collectors.toList());
        List<List<Integer>> transposed = transpose(list);
        final Integer reduced = transposed.stream().map(l -> l.stream().reduce(1, (a, b) -> a * b)).reduce(0, (c, d) -> c + d);
        final Integer all = determineMaxColumns(decls);
        return Tuple.of(all != reduced, all, reduced);
    }




}
