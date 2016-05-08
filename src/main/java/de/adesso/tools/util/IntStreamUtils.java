package de.adesso.tools.util;

import java.util.stream.IntStream;

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

}
