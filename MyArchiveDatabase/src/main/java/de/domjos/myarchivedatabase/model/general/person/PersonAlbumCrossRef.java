package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"personId", "albumId"},
        indices = {@Index(value = {"personId"}), @Index(value = {"albumId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Person.class, parentColumns = {"id"}, childColumns = {"personId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class PersonAlbumCrossRef {
    private long personId;
    private long albumId;

    public PersonAlbumCrossRef() {
        this.personId = 0L;
        this.albumId = 0L;
    }

    public long getPersonId() {
        return this.personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
