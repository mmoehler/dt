package de.adesso.tools.util.tuple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Tuple5<T1, T2, T3, T4, T5> implements Tuple, Comparable<Tuple5<T1, T2, T3, T4, T5>>, Serializable {

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
     * The 4th element of this tuple.
     */
    public final T4 _4;

    /**
     * The 5th element of this tuple.
     */
    public final T5 _5;

    /**
     * Constructs a tuple of 5 elements.
     *
     * @param t1 the 1st element
     * @param t2 the 2nd element
     * @param t3 the 3rd element
     * @param t4 the 4th element
     * @param t5 the 5th element
     */
    public Tuple5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
        this._5 = t5;
    }

    public static <T1, T2, T3, T4, T5> Comparator<Tuple5<T1, T2, T3, T4, T5>> comparator(Comparator<? super T1> t1Comp, Comparator<? super T2> t2Comp, Comparator<? super T3> t3Comp, Comparator<? super T4> t4Comp, Comparator<? super T5> t5Comp) {
        return (Comparator<Tuple5<T1, T2, T3, T4, T5>> & Serializable) (t1, t2) -> {
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

            final int check4 = t4Comp.compare(t1._4, t2._4);
            if (check4 != 0) {
                return check4;
            }

            final int check5 = t5Comp.compare(t1._5, t2._5);
            if (check5 != 0) {
                return check5;
            }

            // all components are equal
            return 0;
        };
    }

    @SuppressWarnings("unchecked")
    private static <U1 extends Comparable<? super U1>, U2 extends Comparable<? super U2>, U3 extends Comparable<? super U3>, U4 extends Comparable<? super U4>, U5 extends Comparable<? super U5>> int compareTo(Tuple5<?, ?, ?, ?, ?> o1, Tuple5<?, ?, ?, ?, ?> o2) {
        final Tuple5<U1, U2, U3, U4, U5> t1 = (Tuple5<U1, U2, U3, U4, U5>) o1;
        final Tuple5<U1, U2, U3, U4, U5> t2 = (Tuple5<U1, U2, U3, U4, U5>) o2;

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

        final int check4 = t1._4.compareTo(t2._4);
        if (check4 != 0) {
            return check4;
        }

        final int check5 = t1._5.compareTo(t2._5);
        if (check5 != 0) {
            return check5;
        }

        // all components are equal
        return 0;
    }

    @Override
    public int arity() {
        return 5;
    }

    @Override
    public int compareTo(Tuple5<T1, T2, T3, T4, T5> that) {
        return Tuple5.compareTo(this, that);
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

    /**
     * Getter of the 4th element of this tuple.
     *
     * @return the 4th element of this Tuple.
     */
    public T4 _4() {
        return _4;
    }

    /**
     * Getter of the 5th element of this tuple.
     *
     * @return the 5th element of this Tuple.
     */
    public T5 _5() {
        return _5;
    }

    @Override
    public List<?> asList() {
        return Arrays.asList(_1, _2, _3, _4, _5);
    }

    // -- Object

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Tuple5)) {
            return false;
        } else {
            final Tuple5<?, ?, ?, ?, ?> that = (Tuple5<?, ?, ?, ?, ?>) o;
            return Objects.equals(this._1, that._1)
                    && Objects.equals(this._2, that._2)
                    && Objects.equals(this._3, that._3)
                    && Objects.equals(this._4, that._4)
                    && Objects.equals(this._5, that._5);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s, %s, %s)", _1, _2, _3, _4, _5);
    }

}