package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"mediaListId", "albumId"},
        indices = {@Index(value = {"mediaListId"}), @Index(value = {"albumId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = MediaList.class, parentColumns = {"id"}, childColumns = {"mediaListId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class MediaListAlbumCrossRef {
    private long mediaListId;
    private long albumId;

    public MediaListAlbumCrossRef() {
        this.mediaListId = 0L;
        this.albumId = 0L;
    }

    public long getMediaListId() {
        return this.mediaListId;
    }

    public void setMediaListId(long mediaListId) {
        this.mediaListId = mediaListId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
