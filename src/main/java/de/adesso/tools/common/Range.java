package de.adesso.tools.common;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Created by moehler on 12.02.2016.
 */
public class Range  implements Iterable<Integer>, Iterator<Integer> {
    private final Integer from;
    private final Integer to;
    private Integer next;

    @Override
    public Iterator<Integer> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return next < to;
    }

    @Override
    public Integer next() {
        return next++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range integers = (Range) o;

        if (!from.equals(integers.from)) return false;
        return to.equals(integers.to);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    private Range(Builder builder) {
        from = builder.from;
        to = builder.to;
        next = to;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(@Nonnull Range copy) {
        Builder builder = new Builder();
        builder.from = copy.from;
        builder.to = copy.to;

        return builder;
    }


    /**
     * {@code Range} builder static inner class.
     */
    public static final class Builder {
        private Integer from;
        private Integer to;
        private Builder() {
        }

        /**
         * Sets the {@code from} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code from} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withFrom(@Nonnull Integer val) {
            from = val;
            return this;
        }

        /**
         * Sets the {@code to} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code to} to set
         * @return a reference to this Builder
         */
        @Nonnull
        public Builder withTo(@Nonnull Integer val) {
            to = val;
            return this;
        }

        /**
         * Returns a {@code Range} built from the parameters previously set.
         *
         * @return a {@code Range} built with parameters of this {@code Range.Builder}
         */
        @Nonnull
        public Range build() {
            return new Range(this);
        }
    }
}
