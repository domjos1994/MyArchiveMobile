package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.book.Book;

@Entity(
        primaryKeys = {"personId", "bookId"},
        indices = {@Index(value = {"personId"}), @Index(value = {"bookId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Person.class, parentColumns = {"id"}, childColumns = {"personId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Book.class, parentColumns = {"id"}, childColumns = {"bookId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class PersonBookCrossRef {
    private long personId;
    private long bookId;

    public PersonBookCrossRef() {
        this.personId = 0L;
        this.bookId = 0L;
    }

    public long getPersonId() {
        return this.personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
