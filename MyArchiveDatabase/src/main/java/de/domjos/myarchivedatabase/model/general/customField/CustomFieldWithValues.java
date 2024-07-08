package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

public final class CustomFieldWithValues {
    @Embedded
    private CustomField customField;

    @Relation(
            parentColumn = "id",
            entityColumn = "customField"
    )
    private List<CustomFieldValue> customFieldValues;

    public CustomFieldWithValues() {
        super();

        this.customField = null;
        this.customFieldValues = new LinkedList<>();
    }

    public CustomField getCustomField() {
        return this.customField;
    }

    public void setCustomField(CustomField customField) {
        this.customField = customField;
    }

    public List<CustomFieldValue> getCustomFieldValues() {
        return this.customFieldValues;
    }

    public void setCustomFieldValues(List<CustomFieldValue> customFieldValues) {
        this.customFieldValues = customFieldValues;
    }
}
