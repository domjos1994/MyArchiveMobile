package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"customFieldId", "albumId"},
        indices = {@Index(value = {"customFieldId"}), @Index(value = {"albumId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomField.class, parentColumns = {"id"}, childColumns = {"customFieldId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldAlbumCrossRef {
    private long customFieldId;
    private long albumId;

    @ColumnInfo(name = "value")
    private String value;

    public CustomFieldAlbumCrossRef() {
        this.customFieldId = 0L;
        this.albumId = 0L;
        this.value = "";
    }

    public long getCustomFieldId() {
        return this.customFieldId;
    }

    public void setCustomFieldId(long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
