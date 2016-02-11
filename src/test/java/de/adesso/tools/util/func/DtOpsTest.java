package de.adesso.tools.util.func;

import de.adesso.tools.common.ListBuilder;
import de.adesso.tools.common.MatrixBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.util.*;

import static de.adesso.tools.util.func.DtOps.copyListRemovedElementsAtIndices;
import static de.adesso.tools.util.func.DtOps.copyMatrix;
import static de.adesso.tools.util.func.DtOps.copyMatrixWithoutColumnsAtIndex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;


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
        ObservableList<ObservableList<String>> actual = copyMatrixWithoutColumnsAtIndex(original, Arrays.asList(1,3));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testRemoveAtIndices() throws Exception {
        String data = "Y,N,Y,Y";
        List<String> expected = new ArrayList<>(Arrays.asList(Y, Y));
        ObservableList<String> tmp = FXCollections.observableArrayList(ListBuilder.on(data).build());
        List<Integer> indices = Arrays.asList(1,3);
        Collections.sort(indices, (a, b) -> b.intValue() - a.intValue());
        ObservableList<String> out = FXCollections.observableArrayList();
        for (int i = 0; i < tmp.size(); i++) {
            if(indices.contains(i)) continue;
            out.add(tmp.get(i));
        }
        List<String> actual = out;
        assertThat(actual, containsInAnyOrder(expected.toArray()));

    }

    @Test
    public void testCopyMatrixWithRemovedRowAtIndices() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.<String>observable(MatrixBuilder.on("1,1,1,2,2,2,3,3,3,4,4,4,5,5,5").dim(5, 3).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.<String>observable(MatrixBuilder.on("2,2,2,4,4,4").dim(2, 3).build());
        ObservableList<ObservableList<String>> actual = DtOps.copyMatrixWithRemovedRowAtIndices(original, Arrays.asList(0,2,4));
        assertEquals(actual.size(),expected.size());

        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (;itA.hasNext()&& itE.hasNext();) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }

    @Test
    public void testCopyMatrixWithRemovedRow() throws Exception {
        ObservableList<ObservableList<String>> original = MatrixBuilder.<String>observable(MatrixBuilder.on("y,y,y,n,n,n,y,y,y").dim(3, 3).build());
        ObservableList<ObservableList<String>> expected = MatrixBuilder.<String>observable(MatrixBuilder.on("y,y,y,n,n,n").dim(2, 3).build());
        ObservableList<ObservableList<String>> actual = DtOps.copyMatrixWithRemovedRow(original);
        assertEquals(actual.size(),expected.size());

        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        Iterator<ObservableList<String>> itA = actual.iterator();
        Iterator<ObservableList<String>> itE = expected.iterator();

        for (;itA.hasNext()&& itE.hasNext();) {
            assertThat(itA.next(), containsInAnyOrder(itE.next().toArray()));
        }
    }


    @Test
    public void testCopyListRemovedElementsAtIndices() throws Exception {
        ObservableList<String> original = FXCollections.observableArrayList("0","1","2","3","4", "5");
        ObservableList<String> expected = FXCollections.observableArrayList("0","2","3","5");
        ObservableList<String> actual = copyListRemovedElementsAtIndices(original, Arrays.asList(1,4));

        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);

        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }
}