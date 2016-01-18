package de.adesso.tools.ui.dialogs;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * Created by mohler on 18.01.16.
 */
public final class Dialogs {
    private Dialogs() {
        super();
    }

    public static Integer acceptOrDefineRuleCountDialog(int calculatedCount) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(calculatedCount));
        dialog.setTitle("(D)ecision (T)able (M)odeler");
        final String s1 = "The defined condition declarations will produce the shown number of combinations.\n";
        final String s2 = "Accept this number or define how many rules should be defined.\n";
        final String s3 = "Note: Additional condition combination columns can be defined later!\n";
        dialog.setHeaderText(s1 + '\n' + s2 + '\n' + s3);
        dialog.setContentText("Please enter your number of combinations:");
        Optional<String> result = dialog.showAndWait();
        final Optional<Integer> count = result.flatMap(a -> (null == a) ? Optional.empty() : Optional.of(Integer.valueOf(a)));
        return count.orElse(10); // TODO Make the count in case of errors configurable!
    }


}
