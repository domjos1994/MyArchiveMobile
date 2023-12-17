package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.base.BaseObject;

@Entity(tableName = "customFieldValues")
public final class CustomFieldValue extends BaseObject {
    @ColumnInfo(name = "customField")
    private long customField;

    @ColumnInfo(name = "value")
    private String value;

    public CustomFieldValue() {
        super();

        this.customField = 0L;
        this.value = "";
    }

    public long getCustomField() {
        return this.customField;
    }

    public void setCustomField(long customField) {
        this.customField = customField;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
