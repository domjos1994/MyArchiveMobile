package de.domjos.myarchivedatabase.model.media.album;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedatabase.model.media.song.SongAlbumCrossRef;

public final class AlbumWithSongs {
    @Embedded
    private Album album;

    @Relation(
            entity = Song.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = SongAlbumCrossRef.class, parentColumn = "albumId", entityColumn = "songId")
    )
    private List<Song> songs;

    public AlbumWithSongs() {
        this.album = null;
        this.songs = new LinkedList<>();
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
