package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"companyId", "albumId"},
        indices = {@Index(value = {"companyId"}), @Index(value = {"albumId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Company.class, parentColumns = {"id"}, childColumns = {"companyId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CompanyAlbumCrossRef {
    private long companyId;
    private long albumId;

    public CompanyAlbumCrossRef() {
        this.companyId = 0L;
        this.albumId = 0L;
    }

    public long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
