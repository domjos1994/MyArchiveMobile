package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.book.Book;

public final class CompanyWithBooks {
    @Embedded
    private Company company;

    @Relation(
            entity = Book.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyBookCrossRef.class, parentColumn = "companyId", entityColumn = "bookId")
    )
    private List<Book> books;

    public CompanyWithBooks() {
        this.company = null;
        this.books = new LinkedList<>();
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
