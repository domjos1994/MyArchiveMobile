package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.song.Song;

@Entity(
        primaryKeys = {"companyId", "songId"},
        indices = {@Index(value = {"companyId"}), @Index(value = {"songId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Company.class, parentColumns = {"id"}, childColumns = {"companyId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Song.class, parentColumns = {"id"}, childColumns = {"songId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CompanySongCrossRef {
    private long companyId;
    private long songId;

    public CompanySongCrossRef() {
        this.companyId = 0L;
        this.songId = 0L;
    }

    public long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getSongId() {
        return this.songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
