package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class TagWithAlbums {
    @Embedded
    private Tag tag;
    @Relation(
            entity = Album.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagAlbumCrossRef.class, parentColumn = "tagId", entityColumn = "albumId")
    )
    private List<Album> albums;

    public TagWithAlbums() {
        this.albums = new LinkedList<>();
        this.tag = null;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}