package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.book.Book;

@Entity(
        primaryKeys = {"customFieldId", "bookId"},
        indices = {@Index(value = {"customFieldId"}), @Index(value = {"bookId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomField.class, parentColumns = {"id"}, childColumns = {"customFieldId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Book.class, parentColumns = {"id"}, childColumns = {"bookId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldBookCrossRef {
    private long customFieldId;
    private long bookId;

    public CustomFieldBookCrossRef() {
        this.customFieldId = 0L;
        this.bookId = 0L;
    }

    public long getCustomFieldId() {
        return this.customFieldId;
    }

    public void setCustomFieldId(long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
