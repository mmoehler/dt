package de.adesso.tools.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Created by mohler on 16.01.16.
 */
public final class TableColumnOps {

    private TableColumnOps() {
    }


    public static TableColumn<List<String>, String> createTableColumn(int x) {
        TableColumn<List<String>, String> tc = new TableColumn(String.format("R%02d", x + 1));

        tc.setCellFactory(DtCell.forTableColumn());

        tc.setOnEditCommit(
                (t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).set(t.getTablePosition().getColumn(), t.getNewValue());
                });

        tc.setCellValueFactory(
                (features) -> new SimpleStringProperty((features.getValue().get(x))
                ));

        tc.setPrefWidth(40);
        tc.setMinWidth(40);
        tc.setResizable(false);

        return tc;
    }

    public static <S> TableColumn<S, String> createTableColumn(String columnName, String propertyName, int prefWidth,
                                                               int minWidth, int maxWidth, boolean resizable,
                                                               EventHandler<CellEditEvent<S,String>> value) {
        TableColumn<S,String> col = new TableColumn<>(columnName);
        col.setMinWidth(minWidth);
        col.setPrefWidth(prefWidth);
        col.setMaxWidth(maxWidth);
        col.setResizable(resizable);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(DtCell.forTableColumn());
        col.setOnEditCommit(value);
        return col;
    }


}

