package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

public final class LibraryWithMovies {
    @Embedded
    private Library library;
    @Relation(
            entity = Movie.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = LibraryMovieCrossRef.class, parentColumn = "libraryId", entityColumn = "movieId")
    )
    private List<Movie> movies;

    public LibraryWithMovies() {
        this.movies = new LinkedList<>();
        this.library = null;
    }

    public List<Movie> getMovies() {
        return this.movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}