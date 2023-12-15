package de.domjos.myarchivedatabase.model.media.song;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"songId", "albumId"},
        indices = {@Index(value = {"songId"}), @Index(value = {"albumId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Song.class, parentColumns = {"id"}, childColumns = {"songId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class SongAlbumCrossRef {
    private long songId;
    private long albumId;

    public SongAlbumCrossRef() {
        this.songId = 0L;
        this.albumId = 0L;
    }

    public long getSongId() {
        return this.songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
