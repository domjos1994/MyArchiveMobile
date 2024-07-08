package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

@Entity(
        primaryKeys = {"personId", "movieId"},
        indices = {@Index(value = {"personId"}), @Index(value = {"movieId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Person.class, parentColumns = {"id"}, childColumns = {"personId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Movie.class, parentColumns = {"id"}, childColumns = {"movieId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class PersonMovieCrossRef {
    private long personId;
    private long movieId;

    public PersonMovieCrossRef() {
        this.personId = 0L;
        this.movieId = 0L;
    }

    public long getPersonId() {
        return this.personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
}
