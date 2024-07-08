package de.domjos.myarchivedatabase.model.filter;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueFilterCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldWithValues;

public final class FilterWithCustomFieldValues {
    @Embedded
    private Filter filter;

    @Relation(
            entity = CustomField.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CustomFieldValueFilterCrossRef.class, parentColumn = "filterId", entityColumn = "customFieldValueId")
    )
    private List<CustomFieldWithValues> customFieldWithValues;

    public FilterWithCustomFieldValues() {
        this.filter = null;
        this.customFieldWithValues = new LinkedList<>();
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public List<CustomFieldWithValues> getCustomFieldWithValues() {
        return this.customFieldWithValues;
    }

    public void setCustomFieldWithValues(List<CustomFieldWithValues> customFieldWithValues) {
        this.customFieldWithValues = customFieldWithValues;
    }
}
