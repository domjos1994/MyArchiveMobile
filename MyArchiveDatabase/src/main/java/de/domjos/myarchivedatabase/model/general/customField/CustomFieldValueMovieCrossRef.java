package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

@Entity(
        primaryKeys = {"customFieldValueId", "movieId"},
        indices = {@Index(value = {"customFieldValueId"}), @Index(value = {"movieId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomFieldValue.class, parentColumns = {"id"}, childColumns = {"customFieldValueId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Movie.class, parentColumns = {"id"}, childColumns = {"movieId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldValueMovieCrossRef {
    private long customFieldValueId;
    private long movieId;

    public CustomFieldValueMovieCrossRef() {
        super();

        this.customFieldValueId = 0L;
        this.movieId = 0L;
    }

    public long getCustomFieldValueId() {
        return this.customFieldValueId;
    }

    public void setCustomFieldValueId(long customFieldValueId) {
        this.customFieldValueId = customFieldValueId;
    }

    public long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
}
