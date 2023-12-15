package de.domjos.myarchivedatabase.model.media.album;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonAlbumCrossRef;

public final class AlbumWithPersons {
    @Embedded
    private Album album;

    @Relation(
            entity = Person.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonAlbumCrossRef.class, parentColumn = "albumId", entityColumn = "personId")
    )
    private List<Person> persons;

    public AlbumWithPersons() {
        this.album = null;
        this.persons = new LinkedList<>();
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
