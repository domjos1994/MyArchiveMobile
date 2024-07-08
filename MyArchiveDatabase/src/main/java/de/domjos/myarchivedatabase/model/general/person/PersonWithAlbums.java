package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class PersonWithAlbums {
    @Embedded
    private Person person;

    @Relation(
            entity = Album.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonAlbumCrossRef.class, parentColumn = "personId", entityColumn = "albumId")
    )
    private List<Album> albums;

    public PersonWithAlbums() {
        this.person = null;
        this.albums = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
}
