package de.domjos.myarchivedatabase.model.media.song;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class SongWithAlbums {
    @Embedded
    private Song song;

    @Relation(
            entity = Album.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = SongAlbumCrossRef.class, parentColumn = "songId", entityColumn = "albumId")
    )
    private List<Album> albums;

    public SongWithAlbums() {
        this.song = null;
        this.albums = new LinkedList<>();
    }

    public Song getSong() {
        return this.song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
}
