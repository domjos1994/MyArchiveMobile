package de.domjos.myarchivedatabase.model.media.song;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonSongCrossRef;

public final class SongWithPersons {
    @Embedded
    private Song song;

    @Relation(
            entity = Person.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonSongCrossRef.class, parentColumn = "songId", entityColumn = "personId")
    )
    private List<Person> persons;

    public SongWithPersons() {
        this.song = null;
        this.persons = new LinkedList<>();
    }

    public Song getSong() {
        return this.song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
