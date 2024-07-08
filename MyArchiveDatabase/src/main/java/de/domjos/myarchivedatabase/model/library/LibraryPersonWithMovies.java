package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;

public final class LibraryPersonWithMovies {
    @Embedded
    private Person person;

    @Relation(
            entity = Library.class,
            parentColumn = "id",
            entityColumn = "person"
    )
    private List<LibraryWithMovies> libraryWithMovies;

    public LibraryPersonWithMovies() {
        super();

        this.person = null;
        this.libraryWithMovies = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<LibraryWithMovies> getLibraryWithMovies() {
        return this.libraryWithMovies;
    }

    public void setLibraryWithMovies(List<LibraryWithMovies> libraryWithMovies) {
        this.libraryWithMovies = libraryWithMovies;
    }
}
