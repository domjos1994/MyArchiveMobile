package de.domjos.myarchivedatabase.model.media.book;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonBookCrossRef;

public final class BookWithPersons {
    @Embedded
    private Book book;

    @Relation(
            entity = Person.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonBookCrossRef.class, parentColumn = "bookId", entityColumn = "personId")
    )
    private List<Person> persons;

    public BookWithPersons() {
        this.book = null;
        this.persons = new LinkedList<>();
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
