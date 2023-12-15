package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.book.Book;

public final class LibraryWithBooks {
    @Embedded
    private Library library;
    @Relation(
            entity = Book.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = LibraryBookCrossRef.class, parentColumn = "libraryId", entityColumn = "bookId")
    )
    private List<Book> books;

    public LibraryWithBooks() {
        this.books = new LinkedList<>();
        this.library = null;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}