package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;

public final class LibraryPersonWithGames {
    @Embedded
    private Person person;

    @Relation(
            entity = Library.class,
            parentColumn = "id",
            entityColumn = "person"
    )
    private List<LibraryWithGames> libraryWithGames;

    public LibraryPersonWithGames() {
        super();

        this.person = null;
        this.libraryWithGames = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<LibraryWithGames> getLibraryWithGames() {
        return this.libraryWithGames;
    }

    public void setLibraryWithGames(List<LibraryWithGames> libraryWithGames) {
        this.libraryWithGames = libraryWithGames;
    }
}
