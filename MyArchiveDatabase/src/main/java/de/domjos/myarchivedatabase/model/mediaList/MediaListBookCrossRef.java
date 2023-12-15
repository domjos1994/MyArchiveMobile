package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.book.Book;

@Entity(
        primaryKeys = {"mediaListId", "bookId"},
        indices = {@Index(value = {"mediaListId"}), @Index(value = {"bookId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = MediaList.class, parentColumns = {"id"}, childColumns = {"mediaListId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Book.class, parentColumns = {"id"}, childColumns = {"bookId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class MediaListBookCrossRef {
    private long mediaListId;
    private long bookId;

    public MediaListBookCrossRef() {
        this.mediaListId = 0L;
        this.bookId = 0L;
    }

    public long getMediaListId() {
        return this.mediaListId;
    }

    public void setMediaListId(long mediaListId) {
        this.mediaListId = mediaListId;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
