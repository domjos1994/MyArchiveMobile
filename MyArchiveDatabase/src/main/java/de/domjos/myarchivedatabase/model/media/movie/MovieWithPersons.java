package de.domjos.myarchivedatabase.model.media.movie;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonMovieCrossRef;

public final class MovieWithPersons {
    @Embedded
    private Movie movie;

    @Relation(
            entity = Person.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonMovieCrossRef.class, parentColumn = "movieId", entityColumn = "personId")
    )
    private List<Person> persons;

    public MovieWithPersons() {
        this.movie = null;
        this.persons = new LinkedList<>();
    }

    public Movie getMovie() {
        return this.movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
