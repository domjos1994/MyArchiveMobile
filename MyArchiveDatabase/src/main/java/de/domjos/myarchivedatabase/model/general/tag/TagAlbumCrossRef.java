package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"tagId", "albumId"},
        indices = {@Index(value = {"tagId"}), @Index(value = {"albumId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class TagAlbumCrossRef {
    private long tagId;
    private long albumId;

    public TagAlbumCrossRef() {
        this.tagId = 0L;
        this.albumId = 0L;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
