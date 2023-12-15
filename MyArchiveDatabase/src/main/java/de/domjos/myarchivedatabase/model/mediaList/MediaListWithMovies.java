package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

public final class MediaListWithMovies {
    @Embedded
    private MediaList mediaList;
    @Relation(
            entity = Movie.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = MediaListMovieCrossRef.class, parentColumn = "mediaListId", entityColumn = "movieId")
    )
    private List<Movie> movies;

    public MediaListWithMovies() {
        this.movies = new LinkedList<>();
        this.mediaList = null;
    }

    public List<Movie> getMovies() {
        return this.movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public MediaList getMediaList() {
        return this.mediaList;
    }

    public void setMediaList(MediaList mediaList) {
        this.mediaList = mediaList;
    }
}