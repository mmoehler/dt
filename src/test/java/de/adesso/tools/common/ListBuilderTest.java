package de.adesso.tools.common;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by moehler on 10.02.2016.
 */
public class ListBuilderTest {

    public final static String Y = "Y";
    public final static String N = "N";

    @Test
    public void testBuild() {
        String data = "Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N";
        List<String> expected = new ArrayList<>(Arrays.asList(Y, Y, Y, Y, Y, Y, N, N, Y, N, Y, N));
        System.out.println("expected = " + expected);
        List<String> actual = ListBuilder.on(data).build();
        System.out.println("actual   = " + actual);
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

}