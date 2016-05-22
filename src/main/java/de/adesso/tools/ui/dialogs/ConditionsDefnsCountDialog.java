package de.adesso.tools.ui.dialogs;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Created by mohler ofList 18.01.16.
 */
public class ConditionsDefnsCountDialog extends Dialog<Tuple2<Integer, Boolean>> {

    private final GridPane grid;
    private final Label label;
    private final CheckBox checkBox;
    private final TextField textField;
    private final String defaultNumberValue;


    public ConditionsDefnsCountDialog() {
        this("", false);
    }

    public ConditionsDefnsCountDialog(@NamedArg("defaultNumberValue") String defaultNumberValue,
                                      @NamedArg("defaultBooleanValue") boolean defaultBooleanValue) {
        final DialogPane dialogPane = getDialogPane();

        // -- textfield
        this.textField = new TextField(defaultNumberValue);
        this.textField.setMaxWidth(Double.MAX_VALUE);

        this.checkBox = new CheckBox("Populate Condition Combinations?");
        this.checkBox.setMaxWidth(Double.MAX_VALUE);
        this.checkBox.setSelected(defaultBooleanValue);

        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setFillWidth(textField, true);

        // -- label
        label = createContentLabel(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());

        this.defaultNumberValue = defaultNumberValue;
        boolean defaultBooleanValue1 = defaultBooleanValue;

        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE
                    ? Tuple.of(Integer.valueOf(textField.getText()), checkBox.isSelected())
                    : null;
        });
    }

    private static Label createContentLabel(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.getStyleClass().add("content");
        label.setWrapText(true);
        label.setPrefWidth(360);
        return label;
    }

    public final TextField getEditor() {
        return textField;
    }

    public final String getDefaultNumberValue() {
        return defaultNumberValue;
    }

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);
        grid.add(checkBox, 0, 1);
        getDialogPane().setContent(grid);

        Platform.runLater(textField::requestFocus);
    }
}
