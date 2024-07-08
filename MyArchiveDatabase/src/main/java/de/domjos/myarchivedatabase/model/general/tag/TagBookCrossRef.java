package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.book.Book;

@Entity(
        primaryKeys = {"tagId", "bookId"},
        indices = {@Index(value = {"tagId"}), @Index(value = {"bookId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Book.class, parentColumns = {"id"}, childColumns = {"bookId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class TagBookCrossRef {
    private long tagId;
    private long bookId;

    public TagBookCrossRef() {
        this.tagId = 0L;
        this.bookId = 0L;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
