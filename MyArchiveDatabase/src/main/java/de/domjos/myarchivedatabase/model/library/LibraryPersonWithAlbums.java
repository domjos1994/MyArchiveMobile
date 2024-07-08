package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;

public final class LibraryPersonWithAlbums {
    @Embedded
    private Person person;

    @Relation(
            entity = Library.class,
            parentColumn = "id",
            entityColumn = "person"
    )
    private List<LibraryWithAlbums> libraryWithAlbums;

    public LibraryPersonWithAlbums() {
        super();

        this.person = null;
        this.libraryWithAlbums = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<LibraryWithAlbums> getLibraryWithAlbums() {
        return this.libraryWithAlbums;
    }

    public void setLibraryWithAlbums(List<LibraryWithAlbums> libraryWithAlbums) {
        this.libraryWithAlbums = libraryWithAlbums;
    }
}
