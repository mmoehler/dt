package de.adesso.tools.util.tuple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Tuple3<T1, T2, T3> implements Tuple, Comparable<Tuple3<T1, T2, T3>>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;

    /**
     * The 2nd element of this tuple.
     */
    public final T2 _2;

    /**
     * The 3rd element of this tuple.
     */
    public final T3 _3;

    /**
     * Constructs a tuple of three elements.
     *
     * @param t1 the 1st element
     * @param t2 the 2nd element
     * @param t3 the 3rd element
     */
    public Tuple3(T1 t1, T2 t2, T3 t3) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
    }

    public static <T1, T2, T3> Comparator<Tuple3<T1, T2, T3>> comparator(Comparator<? super T1> t1Comp, Comparator<? super T2> t2Comp, Comparator<? super T3> t3Comp) {
        return (Comparator<Tuple3<T1, T2, T3>> & Serializable) (t1, t2) -> {
            final int check1 = t1Comp.compare(t1._1, t2._1);
            if (check1 != 0) {
                return check1;
            }

            final int check2 = t2Comp.compare(t1._2, t2._2);
            if (check2 != 0) {
                return check2;
            }

            final int check3 = t3Comp.compare(t1._3, t2._3);
            if (check3 != 0) {
                return check3;
            }

            // all components are equal
            return 0;
        };
    }

    @SuppressWarnings("unchecked")
    private static <U1 extends Comparable<? super U1>, U2 extends Comparable<? super U2>, U3 extends Comparable<? super U3>> int compareTo(Tuple3<?, ?, ?> o1, Tuple3<?, ?, ?> o2) {
        final Tuple3<U1, U2, U3> t1 = (Tuple3<U1, U2, U3>) o1;
        final Tuple3<U1, U2, U3> t2 = (Tuple3<U1, U2, U3>) o2;

        final int check1 = t1._1.compareTo(t2._1);
        if (check1 != 0) {
            return check1;
        }

        final int check2 = t1._2.compareTo(t2._2);
        if (check2 != 0) {
            return check2;
        }

        final int check3 = t1._3.compareTo(t2._3);
        if (check3 != 0) {
            return check3;
        }

        // all components are equal
        return 0;
    }

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public int compareTo(Tuple3<T1, T2, T3> that) {
        return Tuple3.compareTo(this, that);
    }

    /**
     * Getter of the 1st element of this tuple.
     *
     * @return the 1st element of this Tuple.
     */
    public T1 _1() {
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

    /**
     * Getter of the 3rd element of this tuple.
     *
     * @return the 3rd element of this Tuple.
     */
    public T3 _3() {
        return _3;
    }

    @Override
    public List<?> asList() {
        return Arrays.asList(_1, _2, _3);
    }

    // -- Object

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Tuple3)) {
            return false;
        } else {
            final Tuple3<?, ?, ?> that = (Tuple3<?, ?, ?>) o;
            return Objects.equals(this._1, that._1)
                    && Objects.equals(this._2, that._2)
                    && Objects.equals(this._3, that._3);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", _1, _2, _3);
    }

}