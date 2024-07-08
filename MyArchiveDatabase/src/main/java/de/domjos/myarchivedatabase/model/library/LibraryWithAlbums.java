package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class LibraryWithAlbums {
    @Embedded
    private Library library;
    @Relation(
            entity = Album.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = LibraryAlbumCrossRef.class, parentColumn = "libraryId", entityColumn = "albumId")
    )
    private List<Album> albums;

    public LibraryWithAlbums() {
        this.albums = new LinkedList<>();
        this.library = null;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}