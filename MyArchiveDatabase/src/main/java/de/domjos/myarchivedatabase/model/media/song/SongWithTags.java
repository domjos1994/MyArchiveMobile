package de.domjos.myarchivedatabase.model.media.song;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagSongCrossRef;

public final class SongWithTags {
    @Embedded
    private Song song;
    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagSongCrossRef.class, parentColumn = "songId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public SongWithTags() {
        this.song = null;
        this.tags = new LinkedList<>();
    }

    public Song getSong() {
        return this.song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}