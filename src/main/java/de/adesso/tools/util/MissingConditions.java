package de.adesso.tools.util;

import com.codepoetics.protonpack.StreamUtils;
import de.adesso.tools.common.builder.List2DBuilder;
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
import static de.adesso.tools.analysis.completeness.detailed.Functions.makeDecisionMatrix;
import static de.adesso.tools.analysis.completeness.detailed.Functions.makeMaskMatrix;
import static de.adesso.tools.functions.List2DFunctions.transpose;
import static java.util.stream.Collectors.toList;

/**
 * Created by mohler ofList 25.01.16.
 */
public class MissingConditions implements BinaryOperator<List<List<String>>> {

    private final BiFunction<List<String>, List<String>, List<List<String>>>[] ACTIONS;
    private final Function<List<Tuple2<String, String>>, Integer>[] CONDITIONS;
    private final List<List<String>> internalList;

    public MissingConditions() {
        this.internalList = List2DBuilder.matrixOf("Y,N,N,N,N,-,Y,N,N,N,-,-,N,Y,Y,-,-,-,N,Y").dim(4, 5).build();
        this.ACTIONS = new BiFunction[]{A1, A2, A3, A4, A5};
        this.CONDITIONS = new Function[]{B1, B2, B3, B4};
    }

    private static List<Integer> logicalAnd(List<Integer> a, List<Integer> b) {
        List<Integer> condition = StreamUtils.zip(a.stream(), b.stream(), (x, y) -> x * y)
                .collect(toList());
        return condition;
    }

    public static <T> void dumpList2DItems(String msg, List<List<T>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static <T> void dumpList1DItems(String msg, List<T> list1D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list1D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void dumpList2DItems(String msg, ObservableList<ObservableList<String>> list2D) {
        System.out.println(String.format("%s >>>>>>>>>>", msg));
        list2D.forEach(i -> System.out.println("\t" + i));
        System.out.println("<<<<<<<<<<\n");
    }

    public static void main(String[] args) {
        List<List<String>> given = List2DBuilder.matrixOf("Y,Y,N,N,Y,Y,Y,N,N,-,N,Y").dim(3, 4).transposed().build();

        List<List<String>> interimResult = List2DBuilder.matrixOf("-,-,-").dim(1, 3).build();

        List<List<String>> missingList = given.stream()
                .map(x -> List2DBuilder.matrixOf(x).dim(1, x.size()).build())
                .reduce(interimResult, DT.difference());

        dumpList2DItems("RESULT", missingList);

    }

    @Override
    public List<List<String>> apply(List<List<String>> xi, List<List<String>> condition) {

        List<List<Integer>> D = transpose(makeDecisionMatrix(internalList));
        List<List<Integer>> M = transpose(makeMaskMatrix(internalList));

        //dumpList2DItems("D",D);
        //dumpList2DItems("M",M);

        List<List<String>> reduced = xi.stream().map(a -> {

            List<Tuple2<String, String>> prototype = StreamUtils
                    .zip(a.stream(), condition.get(0).stream(), Tuple::of)
                    .collect(toList());

            //dumpList1DItems("PROTO",prototype);


            // evaluate the condition and build the requested mask for the futher processing
            final List<Integer> mask = Arrays.stream(CONDITIONS)
                    .map(c -> c.apply(prototype))
                    .collect(Collectors.toList());

            //dumpList1DItems("MASK",mask);

            // perform a logical and for each Maskmatrix column (m) and the rsulting mask
            List<List<Integer>> multiplied = M.stream().map(m -> logicalAnd(m, mask)).collect(toList());

            //dumpList1DItems("MUL",multiplied);

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
                    .map(Number::intValue).filter(vv -> vv >= 0).collect(Collectors.toList());

            //dumpList1DItems("IDX",indices);

            if (indices.size() != 1) {
                throw new IllegalStateException("Used DT is ambigous!");
            }

            // perform the action at the determined index and ...
            List<List<String>> applied = ACTIONS[indices.get(0)].apply(a, condition.get(0));

            // ... return the resulting matrix
            applied = transpose(applied);
            dumpList2DItems("APL", applied);
            return applied;

        }).reduce(new ArrayList<>(), (k, l) -> Stream.concat(k.stream(), l.stream())
                .collect(toList()));
        dumpList2DItems("REDUCED", reduced);
        return reduced;

    }


    public interface DT extends BinaryOperator<List<List<String>>> {
        static BinaryOperator<List<List<String>>> difference() {
            return (a, b) -> new MissingConditions().apply(a, b);
        }


    }

}
