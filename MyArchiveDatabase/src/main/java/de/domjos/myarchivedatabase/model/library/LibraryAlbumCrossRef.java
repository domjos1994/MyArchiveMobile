package de.domjos.myarchivedatabase.model.library;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.album.Album;

@Entity(
        primaryKeys = {"libraryId", "albumId"},
        indices = {@Index(value = {"libraryId"}), @Index(value = {"albumId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Library.class, parentColumns = {"id"}, childColumns = {"libraryId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Album.class, parentColumns = {"id"}, childColumns = {"albumId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class LibraryAlbumCrossRef {
    private long libraryId;
    private long albumId;

    public LibraryAlbumCrossRef() {
        this.libraryId = 0L;
        this.albumId = 0L;
    }

    public long getLibraryId() {
        return this.libraryId;
    }

    public void setLibraryId(long libraryId) {
        this.libraryId = libraryId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
