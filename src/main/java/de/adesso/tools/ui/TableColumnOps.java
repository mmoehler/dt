package de.adesso.tools.ui;

import de.adesso.tools.ui.condition.ConditionDefnsTableCell;
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

    public static final String COND_ROW_HEADER_2 = "R%02d";
    public static final String COND_ROW_HEADER_1 = "ELSE";

    private TableColumnOps() {
    }


    public static TableColumn<List<String>, String> createTableColumn(int x) {

        System.err.println(">>> " + x);

        String tpl = (0 == x) ? COND_ROW_HEADER_1 : COND_ROW_HEADER_2;
        TableColumn<List<String>, String> tc = new TableColumn(String.format(tpl, x + 1));

        tc.setCellFactory(ConditionDefnsTableCell.forTableColumn());

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

