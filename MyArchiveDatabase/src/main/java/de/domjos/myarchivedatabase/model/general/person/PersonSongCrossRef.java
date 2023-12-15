package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.song.Song;

@Entity(
        primaryKeys = {"personId", "songId"},
        indices = {@Index(value = {"personId"}), @Index(value = {"songId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Person.class, parentColumns = {"id"}, childColumns = {"personId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Song.class, parentColumns = {"id"}, childColumns = {"songId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class PersonSongCrossRef {
    private long personId;
    private long songId;

    public PersonSongCrossRef() {
        this.personId = 0L;
        this.songId = 0L;
    }

    public long getPersonId() {
        return this.personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getSongId() {
        return this.songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
