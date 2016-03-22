package de.adesso.tools.util;

import com.codepoetics.protonpack.StreamUtils;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.adesso.tools.analysis.completeness.detailed.Actions.*;
import static de.adesso.tools.analysis.completeness.detailed.Conditions.*;
import static de.adesso.tools.common.Reserved.*;
import static java.util.stream.Collectors.toList;

/**
 * Created by mohler on 25.01.16.
 */
public class MissingConditions implements BinaryOperator<List<List<String>>> {

    private final BiFunction<List<String>, List<String>, List<List<String>>>[] ACTIONS;
    private final Function<List<Tuple2<String, String>>, Integer>[] CONDITIONS;
    private final List<List<String>> internalList;

    public MissingConditions() {
        this.internalList = MatrixBuilder.on("Y,-,-,-,N,Y,-,-,N,N,N,-,N,N,Y,N,N,N,Y,Y").dim(4, 5).build();
        this.ACTIONS = new BiFunction[]{A1, A2, A3, A4, A5};
        this.CONDITIONS = new Function[]{B1, B2, B3, B4};
    }

    @Override
    public List<List<String>> apply(List<List<String>> xi, List<List<String>> condition) {

        dumpTableItems("LEFT", xi);
        dumpTableItems("RIGHT", condition);



        List<List<Integer>> D = makeDecisionMatrix(internalList);
        List<List<Integer>> M = makeMaskMatrix(internalList);


        final List<List<String>> reduced = xi.stream().map(a -> {

            List<Tuple2<String, String>> prototype = StreamUtils
                    .zip(a.stream(), condition.get(0).stream(), (x, y) -> Tuple.of(x, y))
                    .collect(toList());

            // evaluate the condition and build the requested mask for the futher processing
            final List<Integer> mask = Arrays.stream(CONDITIONS)
                    .map(c -> c.apply(prototype))
                    .collect(Collectors.toList());

            // perform a logical and for each Maskmatrix column (m) and the rsulting mask
            List<List<Integer>> multiplied = M.stream().map(m -> logicalAnd(m, mask)).collect(toList());

            //return the index of the Decisionmatrix colum where the column equals the result of the
            // multiplication process above. Exactly one indexis expected!
            List<Integer> indices = StreamUtils
                    .zip(multiplied.stream(), StreamUtils.zipWithIndex(D.stream()), (aa, bb) -> {
                        if (aa.equals(bb.getValue())) {
                            return bb.getIndex();
                        }
                        return -1;
                    })
                    // .peek(xyz -> System.out.println(xyz))
                    .map(qq -> qq.intValue()).filter(vv -> vv >= 0).collect(Collectors.toList());

            if (indices.size() != 1) {
                throw new IllegalStateException("Used DT is ambigous!");
            }

            // perform the action at the determined index and ...
            List<List<String>> applied = ACTIONS[indices.get(0).intValue()].apply(a, condition.get(0));

            // ... return the resulting matrix
            dumpTableItems("APPLIED", applied);
            return applied;

        }).reduce(new ArrayList<>(), (k, l) -> Stream.concat(k.stream(), l.stream()).distinct().collect(toList()));

        dumpTableItems("REDUCED", reduced);

        return reduced;

    }

    /**
     * Creates the Mask-Matrix
     *
     * @param conditions - the internalList<List<String>> block of the internalList<List<String>> of a given decision table
     * @return a {@link List<List<String>>} container as Mask-Matrix
     */
    private static List<List<Integer>> makeMaskMatrix(List<List<String>> conditions) {
        List<List<Integer>> M = conditions.stream().map(a -> a.stream().map(b -> {
            switch (b) {
                case DASH:
                    return 0;
                case YES:
                case NO:
                    return 1;
                default:
                    throw new IllegalStateException("Illegal code: " + b + "!");
            }
        }).collect(toList())).collect(toList());
        return M;
    }

    /**
     * Creates the Decisionmatrix
     *
     * @param conditions - the internalList<List<String>> block of the internalList<List<String>> of a given decision table
     * @return a {@link List<List<String>>} container as Decisionmatrix
     */
    private static List<List<Integer>> makeDecisionMatrix(List<List<String>> conditions) {
        List<List<Integer>> D = conditions.stream().map(a -> a.stream().map(b -> {
            switch (b) {
                case DASH:
                case NO:
                    return 0;
                case YES:
                    return 1;
                default:
                    throw new IllegalStateException("Illegal code: " + b + "!");
            }
        }).collect(toList())).collect(toList());
        return D;
    }

    private static List<Integer> logicalAnd(List<Integer> a, List<Integer> b) {
        List<Integer> condition = StreamUtils.zip(a.stream(), b.stream(), (x, y) -> x * y)
                .collect(toList());
        return condition;
    }

    public interface DT extends BinaryOperator<List<List<String>>> {
        public static BinaryOperator<List<List<String>>> difference() {
            return (a, b) -> new MissingConditions().apply(a, b);
        }


    }

    public static <T> void dumpTableItems(String msg, List<List<T>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpTableItems(String msg, ObservableList<ObservableList<String>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }


    public static void main(String[] args) {
        List<List<String>> given = MatrixBuilder.on("Y,Y,N,N,Y,Y,Y,N,N,-,N,Y").dim(3, 4).transposed().build();

        dumpTableItems("INPUT", given);

        List<List<String>> interimResult = MatrixBuilder.on("-,-,-").dim(3, 1).build();

        List<List<String>> missingList = given.stream().map(x -> MatrixBuilder.on(x).dim(x.size(),1).build()).reduce(interimResult, DT.difference());

        dumpTableItems("RESULT", missingList);

    }

}
