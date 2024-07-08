package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.movie.Movie;

public final class PersonWithBooks {
    @Embedded
    private Person person;

    @Relation(
            entity = Book.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonBookCrossRef.class, parentColumn = "personId", entityColumn = "bookId")
    )
    private List<Book> books;

    public PersonWithBooks() {
        this.person = null;
        this.books = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
