package de.domjos.myarchivedatabase.model.library;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.book.Book;

@Entity(
        primaryKeys = {"libraryId", "bookId"},
        indices = {@Index(value = {"libraryId"}), @Index(value = {"bookId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Library.class, parentColumns = {"id"}, childColumns = {"libraryId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Book.class, parentColumns = {"id"}, childColumns = {"bookId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class LibraryBookCrossRef {
    private long libraryId;
    private long bookId;

    public LibraryBookCrossRef() {
        this.libraryId = 0L;
        this.bookId = 0L;
    }

    public long getLibraryId() {
        return this.libraryId;
    }

    public void setLibraryId(long libraryId) {
        this.libraryId = libraryId;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
