package de.adesso.dtmg.util;

import java.util.Spliterator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Created by mmoehler ofList 21.02.16.
 */
public final class IntStreamUtils {
    private IntStreamUtils() {
    }

    public static IntStream revRange(int from, int to) {
        return IntStream.range(from, to)
                .map(i -> to - i + from - 1);
    }

    public static IntStream zip(IntStream lefts, IntStream rights, IntBinaryOperator combiner) {
        return StreamSupport.intStream(ZippingIntSpliterator.zipping(lefts.spliterator(), rights.spliterator(), combiner), false);
    }

}

class ZippingIntSpliterator implements Spliterator.OfInt {

    static OfInt zipping(OfInt lefts, OfInt rights, IntBinaryOperator combiner) {
        return new ZippingIntSpliterator(lefts, rights, combiner);
    }

    private final Spliterator<Integer> lefts;
    private final Spliterator<Integer> rights;
    private final IntBinaryOperator combiner;
    private boolean rightHadNext = false;

    private ZippingIntSpliterator(OfInt lefts, OfInt rights, IntBinaryOperator combiner) {
        this.lefts = lefts;
        this.rights = rights;
        this.combiner = combiner;
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        rightHadNext = false;
        boolean leftHadNext = lefts.tryAdvance(l ->
                rights.tryAdvance(r -> {
                    rightHadNext = true;
                    action.accept(combiner.applyAsInt(l, r));
                }));
        return leftHadNext && rightHadNext;
    }

    @Override
    public OfInt trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Math.min(lefts.estimateSize(), rights.estimateSize());
    }

    @Override
    public int characteristics() {
        return lefts.characteristics() & rights.characteristics()
                & ~(Spliterator.DISTINCT | Spliterator.SORTED);
    }

}
