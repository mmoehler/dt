package de.adesso.tools.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by moehler ofList 10.02.2016.
 */
public final class ListBuilder {
    private String data;
    private int len;
    private boolean reversed;
    private ListBuilder() {
    }

    private ListBuilder(String data) {
        this.data = data;
    }

    public static ListBuilder ofList(@javax.annotation.Nonnull String data) {
        return new ListBuilder(data);
    }


    public static ListBuilder ofList(@javax.annotation.Nonnull int[] hints) {
        throw new UnsupportedOperationException("ListBuilder ofList(@javax.annotation.Nonnull int[]");
    }

    public static ListBuilder copy(@javax.annotation.Nonnull ListBuilder other) {
        ListBuilder builder = new ListBuilder(other.data);
        builder.len = other.len;
        builder.reversed = other.reversed;
        return builder;
    }

    public ListBuilder reversed() {
        this.reversed = true;
        return this;
    }

    /**
     * Returns a {@code List2DBuilder} built from the parameters previously set.
     *
     * @return a {@code List2DBuilder} built with parameters of this {@code List2DBuilder.Builder}
     */
    @javax.annotation.Nonnull
    public List<String> build() {
        final List<String> out = Arrays
                .stream(this.data.split("[, ;]"))
                .collect(Collectors.toList());
        return (reversed)
                ? (out.stream().sorted((x, y) -> -1).collect(Collectors.toList()))
                : out;
    }


}
