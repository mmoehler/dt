package de.adesso.tools.functions;

import de.adesso.tools.common.MatrixBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;

import static de.adesso.tools.functions.MatrixFunctions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;


/**
 * Created by moehler on 09.02.2016.
 */
public class MatrixFunctionsTest {
    public final static String Y = "Y";
    public final static String N = "N";
    public static final String QMARK = "?";

    @org.testng.annotations.Test
    public void testCopyMatrix() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> actual = copy(original);
        assertThat(original, equalTo(actual));
    }

    @org.testng.annotations.Test
    public void testRemoveColumnsAtIndices() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("Y,Y,Y,Y,Y,Y,N,N,Y,N,Y,N").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(MatrixBuilder.on("Y,Y,Y,N,Y,Y").dim(3, 2).build());
        ObservableList<ObservableList<String>> actual = removeColumnsAt(original, Arrays.asList(1, 3));
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }

    }

    @Test
    public void testRemoveLastColumn() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("1,2,3,4,1,2,3,4,1,2,3,4").dim(3, 4).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(MatrixBuilder.on("1,2,3,1,2,3,1,2,3").dim(3, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.removeLastColumn(original);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }

    }

    @Test
    public void testInsertColumnsAt() throws Exception {
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(MatrixBuilder.on("1,?,1,2,?,2,3,?,3").dim(3, 3).build());
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("1,1,2,2,3,3").dim(3, 2).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.insertColumnsAt(original, Arrays.asList(1), () -> QMARK);

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testRemoveRowsAtIndices() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.<String>observable(MatrixBuilder.on("1,1,1,2,2,2,3,3,3,4,4,4,5,5,5").dim(5, 3).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.<String>observable(MatrixBuilder.on("2,2,2,4,4,4").dim(2, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.removeRowsAt(original, Arrays.asList(0, 2, 4));
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testRemoveLastRow() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.<String>observable(MatrixBuilder.on("y,y,y,n,n,n,y,y,y").dim(3, 3).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.<String>observable(MatrixBuilder.on("y,y,y,n,n,n").dim(2, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.removeLastRow(original);
        assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testInsertRowsAt() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("1,2,3,4,5,6,7,8,9").dim(3, 3).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(MatrixBuilder.on("1,2,3,?,?,?,4,5,6,?,?,?,7,8,9").dim(5, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.insertRowsAt(original, Arrays.asList(1, 3), () -> QMARK);

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        assertEquals(actual.size(), expected.size());

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (; itA.hasNext() && itE.hasNext(); ) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testTranspose() throws Exception {
        ObservableList<ObservableList<String>> expected = MatrixBuilder.observable(MatrixBuilder.on("1,2,3,1,2,3,1,2,3").dim(3, 3).build());
        ObservableList<ObservableList<String>> original = MatrixBuilder.observable(MatrixBuilder.on("1,2,3,?,?,?,1,2,3,?,?,?,1,2,3").dim(5, 3).build());
        ObservableList<ObservableList<String>> actual = MatrixFunctions.transpose(original);
        //assertEquals(actual.size(), expected.size());

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        actual.remove(2);

        System.out.println("actual   = " + actual);

        actual = MatrixFunctions.transpose(actual);

        System.out.println("actual   = " + actual);
    }


    // TODO Move the following Tests to ListsTest !!

    @Test
    public void testCopyListRemovedElementsAtIndices() throws Exception {
        ObservableList<String> original = FXCollections.observableArrayList("0", "1", "2", "3", "4", "5");
        ObservableList<String> expected = FXCollections.observableArrayList("0", "2", "3", "5");
        ObservableList<String> actual = ListFunctions.removeElementsAt(original, Arrays.asList(1, 4));

        System.out.println("original = " + original);
        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }
}