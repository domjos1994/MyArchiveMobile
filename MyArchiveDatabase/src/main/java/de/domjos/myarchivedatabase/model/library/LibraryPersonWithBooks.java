package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;

public final class LibraryPersonWithBooks {
    @Embedded
    private Person person;
    
    @Relation(
            entity = Library.class,
            parentColumn = "id",
            entityColumn = "person"
    )
    private List<LibraryWithBooks> libraryWithBooks;

    public LibraryPersonWithBooks() {
        super();
        
        this.person = null;
        this.libraryWithBooks = new LinkedList<>();
    }
    
    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<LibraryWithBooks> getLibraryWithBooks() {
        return this.libraryWithBooks;
    }

    public void setLibraryWithBooks(List<LibraryWithBooks> libraryWithBooks) {
        this.libraryWithBooks = libraryWithBooks;
    }
}
