package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class MediaListWithAlbums {
    @Embedded
    private MediaList mediaList;
    @Relation(
            entity = Album.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = MediaListAlbumCrossRef.class, parentColumn = "mediaListId", entityColumn = "albumId")
    )
    private List<Album> albums;

    public MediaListWithAlbums() {
        this.albums = new LinkedList<>();
        this.mediaList = null;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public MediaList getMediaList() {
        return this.mediaList;
    }

    public void setMediaList(MediaList mediaList) {
        this.mediaList = mediaList;
    }
}