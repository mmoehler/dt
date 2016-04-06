package de.adesso.tools.ui.scopes;

import de.adesso.tools.model.ConditionDecl;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;

public class ContactDialogScope implements Scope {

    public static String RESET_DIALOG_PAGE = "contact_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "contact_ok_before_commit";
    public static String COMMIT = "contact_commit";
    public static String RESET_FORMS = "contact_reset";

    private final ObjectProperty<ConditionDecl> contactToEdit = new SimpleObjectProperty<>(this, "contactToEdit");

    private final BooleanProperty contactFormValid = new SimpleBooleanProperty();
    private final BooleanProperty addressFormValid = new SimpleBooleanProperty();
    private final BooleanProperty bothFormsValid = new SimpleBooleanProperty();
    private final StringProperty dialogTitle = new SimpleStringProperty();


    public BooleanProperty contactFormValidProperty() {
        return this.contactFormValid;
    }

    public boolean isContactFormValid() {
        return this.contactFormValidProperty().get();
    }

    public void setContactFormValid(final boolean contactFormValid) {
        this.contactFormValidProperty().set(contactFormValid);
    }

    public BooleanProperty addressFormValidProperty() {
        return this.addressFormValid;
    }

    public boolean isAddressFormValid() {
        return this.addressFormValidProperty().get();
    }

    public void setAddressFormValid(final boolean addressFormValid) {
        this.addressFormValidProperty().set(addressFormValid);
    }

    public ObjectProperty<ConditionDecl> contactToEditProperty() {
        return this.contactToEdit;
    }


    public ConditionDecl getContactToEdit() {
        return this.contactToEditProperty().get();
    }


    public void setContactToEdit(final ConditionDecl conditionDeclToEdit) {
        this.contactToEditProperty().set(conditionDeclToEdit);
    }

    public final BooleanProperty bothFormsValidProperty() {
        return this.bothFormsValid;
    }


    public final boolean isBothFormsValid() {
        return this.bothFormsValidProperty().get();
    }


    public final void setBothFormsValid(final boolean bothFormsValid) {
        this.bothFormsValidProperty().set(bothFormsValid);
    }

    public final StringProperty dialogTitleProperty() {
        return this.dialogTitle;
    }


    public final java.lang.String getDialogTitle() {
        return this.dialogTitleProperty().get();
    }


    public final void setDialogTitle(final java.lang.String dialogTitle) {
        this.dialogTitleProperty().set(dialogTitle);
    }


}
