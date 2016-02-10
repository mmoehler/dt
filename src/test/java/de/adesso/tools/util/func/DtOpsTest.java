package de.adesso.tools.util.func;

import de.adesso.tools.common.ListBuilder;
import de.adesso.tools.common.MatrixBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.adesso.tools.util.func.DtOps.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;


/**
 * Created by moehler on 09.02.2016.
 */
public class DtOpsTest {
    public final static String Y = "Y";
    public final static String N = "N";

    @org.testng.annotations.Test
    public void testCopyMatrix() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> actual = copyMatrix(original);
        assertThat(original,equalTo(actual));
    }

    @org.testng.annotations.Test
    public void testCopyMatrixWithoutColumnsWithIndex() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(MatrixBuilder.on("Y,Y,Y,N,Y,Y").dim(3, 2).build());
        ObservableList<ObservableList<String>> actual = copyMatrixWithoutColumnsWithIndex(original, Arrays.asList(1,3));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testRemoveAtIndices() throws Exception {
        String data = "Y,N,Y,Y";
        List<String> expected = new ArrayList<>(Arrays.asList(Y, Y));
        ObservableList<String> tmp = FXCollections.observableArrayList(ListBuilder.on(data).build());
        List<String> actual = removeAtIndices(tmp, Arrays.asList(1,3));
        assertThat(actual, containsInAnyOrder(expected.toArray()));

    }
}