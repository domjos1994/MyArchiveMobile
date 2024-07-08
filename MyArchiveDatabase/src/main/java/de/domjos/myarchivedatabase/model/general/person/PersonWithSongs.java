package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.song.Song;

public final class PersonWithSongs {
    @Embedded
    private Person person;

    @Relation(
            entity = Song.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonSongCrossRef.class, parentColumn = "personId", entityColumn = "songId")
    )
    private List<Song> songs;

    public PersonWithSongs() {
        this.person = null;
        this.songs = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
