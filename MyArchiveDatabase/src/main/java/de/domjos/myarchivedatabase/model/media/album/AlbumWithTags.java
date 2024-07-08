package de.domjos.myarchivedatabase.model.media.album;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagAlbumCrossRef;

public final class AlbumWithTags {
    @Embedded
    private Album album;
    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagAlbumCrossRef.class, parentColumn = "albumId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public AlbumWithTags() {
        this.album = null;
        this.tags = new LinkedList<>();
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}