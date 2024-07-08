package de.domjos.myarchivedatabase.model.library;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

@Entity(
        primaryKeys = {"libraryId", "movieId"},
        indices = {@Index(value = {"libraryId"}), @Index(value = {"movieId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Library.class, parentColumns = {"id"}, childColumns = {"libraryId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Movie.class, parentColumns = {"id"}, childColumns = {"movieId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class LibraryMovieCrossRef {
    private long libraryId;
    private long movieId;

    public LibraryMovieCrossRef() {
        this.libraryId = 0L;
        this.movieId = 0L;
    }

    public long getLibraryId() {
        return this.libraryId;
    }

    public void setLibraryId(long libraryId) {
        this.libraryId = libraryId;
    }

    public long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
}
