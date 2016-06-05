package de.adesso.dtmg.util.tuple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Tuple2<T1, T2> implements Tuple, Comparable<Tuple2<T1, T2>>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The 1st element of this tuple.
     */
    private final T1 _1;

    /**
     * The 2nd element of this tuple.
     */
    private final T2 _2;

    /**
     * Constructs a tuple of two elements.
     *
     * @param t1 the 1st element
     * @param t2 the 2nd element
     */
    public Tuple2(T1 t1, T2 t2) {
        this._1 = t1;
        this._2 = t2;
    }

    public static <T1, T2> Comparator<Tuple2<T1, T2>> comparator(Comparator<? super T1> t1Comp,
                                                                 Comparator<? super T2> t2Comp) {
        return (Comparator<Tuple2<T1, T2>> & Serializable) (t1, t2) -> {
            final int check1 = t1Comp.compare(t1._1, t2._1);
            if (check1 != 0) {
                return check1;
            }

            final int check2 = t2Comp.compare(t1._2, t2._2);
            if (check2 != 0) {
                return check2;
            }

            // all components are equal
            return 0;
        };
    }

    @SuppressWarnings("unchecked")
    private static <U1 extends Comparable<? super U1>, U2 extends Comparable<? super U2>> int compareTo(Tuple2<?, ?> o1,
                                                                                                        Tuple2<?, ?> o2) {
        final Tuple2<U1, U2> t1 = (Tuple2<U1, U2>) o1;
        final Tuple2<U1, U2> t2 = (Tuple2<U1, U2>) o2;

        final int check1 = t1._1.compareTo(t2._1);
        if (check1 != 0) {
            return check1;
        }

        final int check2 = t1._2.compareTo(t2._2);
        if (check2 != 0) {
            return check2;
        }

        // all components are equal
        return 0;
    }

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public int compareTo(Tuple2<T1, T2> that) {
        return Tuple2.compareTo(this, that);
    }

    /**
     * Getter of the 1st element of this tuple.
     *
     * @return the 1st element of this Tuple.
     */
    public T1 _1() {
        return _1;
    }

    public T1 $1() {
        return _1;
    }

    /**
     * Getter of the 2nd element of this tuple.
     *
     * @return the 2nd element of this Tuple.
     */
    public T2 _2() {
        return _2;
    }

    public T2 $2() {
        return _2;
    }

    @Override
    public List<?> asList() {
        return Arrays.asList(_1, _2);
    }

    // -- Object

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Tuple2)) {
            return false;
        } else {
            final Tuple2<?, ?> that = (Tuple2<?, ?>) o;
            return Objects.equals(this._1, that._1) && Objects.equals(this._2, that._2);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", _1, _2);
    }

}