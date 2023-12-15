package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

public final class TagWithMovies {
    @Embedded
    private Tag tag;
    @Relation(
            entity = Movie.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagMovieCrossRef.class, parentColumn = "tagId", entityColumn = "movieId")
    )
    private List<Movie> movies;

    public TagWithMovies() {
        this.movies = new LinkedList<>();
        this.tag = null;
    }

    public List<Movie> getMovies() {
        return this.movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}