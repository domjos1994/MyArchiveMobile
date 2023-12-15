package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.song.Song;

@Entity(
        primaryKeys = {"tagId", "songId"},
        indices = {@Index(value = {"tagId"}), @Index(value = {"songId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Song.class, parentColumns = {"id"}, childColumns = {"songId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class TagSongCrossRef {
    private long tagId;
    private long songId;

    public TagSongCrossRef() {
        this.tagId = 0L;
        this.songId = 0L;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getSongId() {
        return this.songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
