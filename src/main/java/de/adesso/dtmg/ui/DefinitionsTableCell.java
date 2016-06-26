package de.adesso.dtmg.ui;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.StringConverter;

import static de.adesso.dtmg.common.Reserved.*;

/**
 * Created by mohler ofList 16.01.16.
 */
public class DefinitionsTableCell<S, T> extends TableCell<S, T> {

    /**
     * Convenience converter that does nothing (converts Strings to themselves and vice-versa...).
     */
    public static final StringConverter<String> IDENTITY_CONVERTER = new StringConverter<String>() {

        @Override
        public String toString(String object) {
            return object.toUpperCase();
        }

        @Override
        public String fromString(String string) {
            return string;
        }
    };

    // Converter for converting the text in the text field to the user type, and vice-versa:
    private final StringConverter<T> converter;

    // Text field for editing
    // TODO: allow this to be a pluggable control.
    private TextField textField;


    public DefinitionsTableCell(StringConverter<T> converter) {
        this.converter = converter;

        this.textField = createTextField();

        itemProperty().addListener((obx, oldItem, newItem) -> {
            if (newItem == null) {
                setText(null);
            } else {
                setText(converter.toString(newItem));
            }
        });
        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        setAlignment(Pos.CENTER);

        textField.setOnAction(evt -> {
            commitEdit(this.converter.fromString(textField.getText()));
        });

        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitEdit(this.converter.fromString(textField.getText()));
            }
        });

        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            final KeyCode keyCode = event.getCode();
            final TableView.TableViewSelectionModel<S> selectionModel = getTableView().getSelectionModel();
            switch (keyCode) {
                case ESCAPE:
                    textField.setText(converter.toString(getItem()));
                    cancelEdit();
                    break;
                case RIGHT:
                    selectionModel.selectRightCell();
                    break;
                case LEFT:
                    selectionModel.selectLeftCell();
                    break;
                case UP:
                    selectionModel.selectAboveCell();
                    break;
                case DOWN:
                    selectionModel.selectBelowCell();
                    break;
            }
            event.consume();
        });
    }

    /**
     * Convenience method for creating an DeclarationsTableCell for a String value.
     *
     * @return
     */
    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return list -> new DefinitionsTableCell(IDENTITY_CONVERTER);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setStyle("");
        } else {
            final String newValue = getText();
            switch (newValue) {
                case QMARK:
                    setCellStyle("#ff0000");
                    break;
                case YES:
                    setCellStyle("#f3b804");
                    break;
                case NO:
                    setCellStyle("#7fc2f3");
                    break;
                case DASH:
                    setCellStyle("#fefefe");
                    break;
                case ELSE:
                    setCellStyle("#ff8000");
                    break;
                case DOACTION:
                    setCellStyle("#408000");
                    break;
                case NOTHING:
                case SPACE:
                    setStyle(NOTHING);
                    break;
                default:
                    setCellStyle("#c0c0c0");
                    break;
            }
        }
    }

    private void setCellStyle(String colorHex) {
        setStyle(String.format(
                "-fx-background-radius: 5em; " +
                        "-fx-body-color: %s; " +
                        "-fx-background-color: radial-gradient(focus-angle 254deg , focus-distance 74%% , center 50%% 50%% , radius 55%% , #ffffff, %s); " +
                        "-fx-background-insets: 1px; " +
                        "-fx-padding: 0px;", colorHex, colorHex));
    }


    private TextField createTextField() {
        textField = new TextField();
        textField.setPadding(new Insets(1, 1, 1, 1));
        textField.setBorder(Border.EMPTY);
        textField.setBackground(Background.EMPTY);
        textField.setFont(new Font(12));
        return textField;
    }

    // set the text of the text field and display the graphic
    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(converter.toString(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
    }

    // revert to text display
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    // commits the edit. Update property if possible and revert to text display
    @Override
    public void commitEdit(T item) {

        // This block is necessary to support commit ofList losing focus, because the baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply bails if we are not editing...
        if (!isEditing() && !item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                TableColumn.CellEditEvent<S, T> event = new TableColumn.CellEditEvent<>(table,
                        new TablePosition<S, T>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);

        this.updateItem(item, false);

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }
}