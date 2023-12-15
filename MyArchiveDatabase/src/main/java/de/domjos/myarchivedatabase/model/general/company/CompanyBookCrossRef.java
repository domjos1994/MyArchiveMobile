package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.book.Book;

@Entity(
        primaryKeys = {"companyId", "bookId"},
        indices = {@Index(value = {"companyId"}), @Index(value = {"bookId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Company.class, parentColumns = {"id"}, childColumns = {"companyId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Book.class, parentColumns = {"id"}, childColumns = {"bookId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CompanyBookCrossRef {
    private long companyId;
    private long bookId;

    public CompanyBookCrossRef() {
        this.companyId = 0L;
        this.bookId = 0L;
    }

    public long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
