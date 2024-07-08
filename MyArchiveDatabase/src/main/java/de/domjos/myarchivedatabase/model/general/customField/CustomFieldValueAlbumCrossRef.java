package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"customFieldValueId", "albumId"},
        indices = {@Index(value = {"customFieldValueId"}), @Index(value = {"albumId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomFieldValue.class, parentColumns = {"id"}, childColumns = {"customFieldValueId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldValueAlbumCrossRef {
    private long customFieldValueId;
    private long albumId;

    public CustomFieldValueAlbumCrossRef() {
        super();

        this.customFieldValueId = 0L;
        this.albumId = 0L;
    }

    public long getCustomFieldValueId() {
        return this.customFieldValueId;
    }

    public void setCustomFieldValueId(long customFieldValueId) {
        this.customFieldValueId = customFieldValueId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
