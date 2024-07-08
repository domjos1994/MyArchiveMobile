package de.domjos.myarchivedatabase.model.media.book;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueBookCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldWithValues;

public final class BookWithCustomFieldValues {
    @Embedded
    private Book book;

    @Relation(
            entity = CustomField.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CustomFieldValueBookCrossRef.class, parentColumn = "bookId", entityColumn = "customFieldValueId")
    )
    private List<CustomFieldWithValues> customFieldWithValues;

    public BookWithCustomFieldValues() {
        this.book = null;
        this.customFieldWithValues = new LinkedList<>();
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<CustomFieldWithValues> getCustomFieldWithValues() {
        return this.customFieldWithValues;
    }

    public void setCustomFieldWithValues(List<CustomFieldWithValues> customFieldWithValues) {
        this.customFieldWithValues = customFieldWithValues;
    }
}
