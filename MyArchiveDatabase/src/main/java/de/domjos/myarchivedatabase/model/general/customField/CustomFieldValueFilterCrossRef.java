package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.filter.Filter;

@Entity(
        primaryKeys = {"customFieldValueId", "filterId"},
        indices = {@Index(value = {"customFieldValueId"}), @Index(value = {"filterId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomFieldValue.class, parentColumns = {"id"}, childColumns = {"customFieldValueId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Filter.class, parentColumns = {"id"}, childColumns = {"filterId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldValueFilterCrossRef {
    private long customFieldValueId;
    private long filterId;

    public CustomFieldValueFilterCrossRef() {
        super();

        this.customFieldValueId = 0L;
        this.filterId = 0L;
    }

    public long getCustomFieldValueId() {
        return this.customFieldValueId;
    }

    public void setCustomFieldValueId(long customFieldValueId) {
        this.customFieldValueId = customFieldValueId;
    }

    public long getFilterId() {
        return this.filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }
}
