package de.adesso.tools.ui.condition.analysis;

import com.codepoetics.protonpack.StreamUtils;
import de.adesso.tools.common.MatrixBuilder;
import de.adesso.tools.common.Reserved;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.adesso.tools.common.Reserved.*;

/**
 * Created by mohler on 25.01.16.
 */
public class MissingConditions implements BinaryOperator<List<List<String>>> {

    private final BiFunction<List<String>, List<String>, List<List<String>>>[] ACTIONS;

    private final List<List<String>> internalList;

    public MissingConditions() {

        this.internalList = MatrixBuilder.on("YES,-,-,-,NO,YES,-,-,NO,NO,NO,-,NO,NO,YES,NO,NO,NO,YES,YES").dim(4,5).build();


        this.ACTIONS = new BiFunction[5];
        ACTIONS[0] = new A1();
        ACTIONS[1] = new A2();
        ACTIONS[2] = new A3();
        ACTIONS[3] = new A4();
        ACTIONS[4] = new A5();
    }

    @Override
    public List<List<String>> apply(List<List<String>> xi, List<List<String>> condition) {

        System.out.println("--------------------------------------------------------------------");
        System.out.println(String.format("%s -- %s", xi, condition));
        System.out.println("--------------------------------------------------------------------");
        List<List<Integer>> D = makeDecisionMatrix(internalList);
        List<List<Integer>> M = makeMaskMatrix(internalList);

        // The DT for processing this algorithm provides 4 conditions
        final int[] hints = new int[4];

        final List<List<String>> reduced = xi.stream().map(a -> {
            int h = 0;
            List<Tuple2<String, String>> prototype = StreamUtils
                    .zip(a.stream(), condition.get(0).stream(), (x, y) -> Tuple.of(x, y)).collect(Collectors.toList());

            hints[h++] = applyB1(prototype);
            hints[h++] = applyB2(prototype);
            hints[h++] = applyB3(prototype);
            hints[h++] = applyB4(prototype);

            final List<String> mask = List<String>.newBuilder().data(hints).build();
            List<List<String>> multiplied = M.stream().map(m -> and(m, mask)).collect(Collectors.toCollection(List<List<String>>::new));



            List<Integer> indices = StreamUtils
                    .zip(multiplied.stream(), StreamUtils.zipWithIndex(D.stream()), (aa, bb) -> {
                        // System.out.println(String.format("%s == %s", aa,
                        // bb.getValue()));
                        if (aa.equals(bb.getValue())) {
                            return bb.getIndex();
                        }
                        return -1;
                    })
                    // .peek(xyz -> System.out.println(xyz))
                    .map(qq -> qq.intValue()).filter(vv -> vv >= 0).collect(Collectors.toList());

            System.err.println(indices);

            if (indices.size() != 1) {
                throw new IllegalStateException("Used DT is ambigous!");
            }

            List<List<String>> applied = ACTIONS[indices.get(0).intValue()].apply(a, condition.get(0));

            return applied;

        }).reduce(List<List<String>>.emptyList<List<String>>(), (k, l) -> combine(k, l));

        return reduced;

    }

    static List<List<String>> combine(List<List<String>> l, List<List<String>> r) {
        return Stream.concat(l.stream(), r.stream()).distinct().collect(Collectors.toCollection(List<List<String>>::new));
    }

    /**
     * Creates the Mask-Matrix
     *
     * @param conditions
     *            - the internalList<List<String>> block of the internalList<List<String>> of a given decision table
     * @return a {@link List<List<String>>} container as Mask-Matrix
     */
    private static List<List<String>> makeMaskMatrix(List<List<String>> conditions) {
        List<List<String>> M = conditions.stream().map(a -> a.stream().map(b -> {
            Reserved c = lookup(b).get();
            switch (c) {
                case DASH:
                    return 0;
                case Y:
                case N:
                    return 1;
                default:
                    throw new IllegalStateException("Illegal code: " + b + "!");
            }
        }).collect(Collectors.toCollection(List<String>::new))).collect(Collectors.toCollection(List<List<String>>::new));
        return M;
    }

    /**
     * Creates the Decisionmatrix
     *
     * @param conditions
     *            - the internalList<List<String>> block of the internalList<List<String>> of a given decision table
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
        }).collect(Collectors.toList())).collect(Collectors.toList());
        return D;
    }

    private static List<Integer> and(List<Integer> a, List<Integer> b) {
        List<Integer> condition = StreamUtils.zip(a.stream(), b.stream(), (x, y) -> x * y)
                .collect(Collectors.toList());
        return condition;
    }

    /**
     * B3 Gibt es für mindestens eine Bedingung das Anzeigerpaar -/YES bzw. -/NO ?
     *
     * @param prototype
     * @return
     */
    private static int applyB3(final List<Tuple2<Integer, Integer>> prototype) {
        final long count = applyForCount(prototype);
        return count > 0L ? 1 : 0;
    }

    /**
     * B4 Steht in mehreren Bedingungen ein Anzeigerpaar des Types -/YES, -/NO ?
     *
     * @param prototype
     * @return
     */
    private static int applyB4(final List<Tuple2<Integer, Integer>> prototype) {
        final long count = applyForCount(prototype);
        return count > 1L ? 1 : 0;
    }

    /**
     * Ermittelt die Anzahl von -/YES bzw. -/NO Anzeigerpaaren.
     *
     * @param prototype
     * @return
     */
    private static long applyForCount(final List<Tuple2<String, String>> prototype) {
        return prototype.stream().filter(x -> isDASH(x._1()) && (isYES(x._2()) || isNO(x._2()))).count();
    }

    /**
     * B2 Sind die Anzeigerpaare für alle Bedingungen identisch?
     *
     * @param prototype
     * @return
     */
    private static int applyB2(final List<Tuple2<String, String>> prototype) {
        return prototype.stream().allMatch(Predicate.isEqual(prototype.get(0))) ? 1 : 0;
    }

    /**
     * B1 Gibt es für mindestens eine Bedingung das Anzeigerpaar YES/NO bzw. NO/YES ?
     *
     * @param prototype
     * @return
     */
    private static int applyB1(final List<Tuple2<String, String>> prototype) {
        Stream<Tuple2<String,String>> tuples = prototype.stream();
        final Optional<Tuple2<String, String>> optB1 = tuples.filter(
                x -> isNO(x._1()) && isYES(x._2()) || isNO(x._2()) && isYES(x._1()))
                .findFirst();
        return optB1.isPresent() ? 1 : 0;
    }

    public static class A1 implements BiFunction<List<String>, List<String>, List<List<String>>> {
        @Override
        public List<List<String>> apply(List<String> t, List<String> u) {
            System.out.println(String.format("A1 invoked with %s, %s!", t, u));
            List<List<String>>Builder builder = List<List<String>>.newBuilder().dim(t.size(), 1).addColumn(t);
            return builder.build();
        }
    }

    public static class A2 implements BiFunction<List<String>, List<String>, List<List<String>>> {
        @Override
        public List<List<String>> apply(List<String> t, List<String> u) {
            System.out.println(String.format("A2 invoked with %s, %s!", t, u));
            return List<List<String>>.emptyList<List<String>>();
        }
    }

    public static class A3 implements BiFunction<List<String>, List<String>, List<List<String>>> {
        @Override
        public List<List<String>> apply(List<String> t, List<String> u) {
            System.out.println(String.format("A3 invoked with %s, %s!", t, u));
            return List<List<String>>.emptyList<List<String>>();
        }
    }

    public static class A4 implements BiFunction<List<String>, List<String>, List<List<String>>> {
        @Override
        public List<List<String>> apply(List<String> t, List<String> u) {
            System.out.println(String.format("A4 invoked with %s, %s!", t, u));
            List<String> processed = StreamUtils.zip(t.stream(), u.stream(), (v, w) -> {
                Reserved l = lookup(v).get();
                Reserved r = lookup(w).get();
                switch (l) {
                    case DASH:
                        switch (r) {
                            case N:
                            case Y:
                                return r.code;
                        }
                    default:
                        return l.code;
                }
            }).collect(Collectors.toCollection(List<String>::new));
            List<List<String>>.List<List<String>>Builder builder = List<List<String>>.newBuilder().addColumn(processed);
            return builder.build();
        }
    }

    public static class A5 implements BiFunction<List<String>, List<String>, List<List<String>>> {
        @Override
        public List<List<String>> apply(List<String> l, List<String> r) {
            System.out.println(String.format("A5 invoked with %s, %s!", l, r));
            long dashes = l.stream().filter(f -> isDash(f)).count();
            int[] d = new int[l.size() * (int) dashes];
            Arrays.fill(d, 0);
            List<List<String>> ret = List<List<String>>.newBuilder().dim(l.size(), (int) dashes).data(d).build();
            int dashpos = 0;
            for (int row = 0; row < l.size(); row++) {
                Reserved rl = lookup(l.get(row)).get();
                Reserved rr = lookup(r.get(row)).get();

                if (rl == rr) {
                    final int xr = row;
                    ret.stream().forEach(x -> x.set(xr, rr.code));
                } else if (rl == DASH) {
                    final int xr = row;
                    int pos = dashpos++;
                    System.out.println(pos);
                    List<Integer> ll = new ArrayList<>();
                    IntStream.range(0, (int) dashes).forEach(i -> {
                        if (i == pos) {
                            if (rr == YES) {
                                ll.add(NO.code);
                            } else {
                                ll.add(YES.code);
                            }
                        } else if (i < pos) {
                            ll.add(DASH.code);
                        } else {
                            ll.add(rr.code);
                        }
                    });
                    Iterator<Integer> it = ll.iterator();
                    ret.stream().forEach(x -> x.set(xr, it.next()));
                }
            }
            return ret;
        }
    }

    public interface DT extends BinaryOperator<List<List<String>>> {
        public static BinaryOperator<List<List<String>>> diff() {
            return (a, b) -> new List<String>Evaluator().apply(a, b);
        }
    }

    public static void main(String[] args) {
        Symbols symbols = new Symbols();
        List<List<String>> given = List<List<String>>.newBuilder().dim(3, 4).data("YES,YES,NO,NO,YES,YES,YES,NO,NO,-,NO,YES", symbols).build();

        List<List<String>> interimResult = List<List<String>>.newBuilder().dim(3,1).dashed().build();


        List<List<String>> missingList<List<String>> = given.stream().map(x -> new List<List<String>>(x)).reduce(interimResult, DT.diff());

        System.out.println("MISSING => " + missingList<List<String>>);

    }

}
