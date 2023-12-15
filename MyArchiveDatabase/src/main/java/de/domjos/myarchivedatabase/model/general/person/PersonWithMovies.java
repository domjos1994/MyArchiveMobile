package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

public final class PersonWithMovies {
    @Embedded
    private Person person;

    @Relation(
            entity = Movie.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonMovieCrossRef.class, parentColumn = "personId", entityColumn = "movieId")
    )
    private List<Movie> movies;

    public PersonWithMovies() {
        this.person = null;
        this.movies = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Movie> getMovies() {
        return this.movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
