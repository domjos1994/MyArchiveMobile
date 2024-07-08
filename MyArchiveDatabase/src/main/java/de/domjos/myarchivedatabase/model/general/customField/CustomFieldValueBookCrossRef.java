package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.book.Book;

@Entity(
        primaryKeys = {"customFieldValueId", "bookId"},
        indices = {@Index(value = {"customFieldValueId"}), @Index(value = {"bookId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomFieldValue.class, parentColumns = {"id"}, childColumns = {"customFieldValueId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Book.class, parentColumns = {"id"}, childColumns = {"bookId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldValueBookCrossRef {
    private long customFieldValueId;
    private long bookId;

    public CustomFieldValueBookCrossRef() {
        super();

        this.customFieldValueId = 0L;
        this.bookId = 0L;
    }

    public long getCustomFieldValueId() {
        return this.customFieldValueId;
    }

    public void setCustomFieldValueId(long customFieldValueId) {
        this.customFieldValueId = customFieldValueId;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
