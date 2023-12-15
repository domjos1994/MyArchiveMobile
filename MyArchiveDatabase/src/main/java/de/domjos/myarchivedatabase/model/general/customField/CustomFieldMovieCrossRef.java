package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

@Entity(
        primaryKeys = {"customFieldId", "movieId"},
        indices = {@Index(value = {"customFieldId"}), @Index(value = {"movieId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomField.class, parentColumns = {"id"}, childColumns = {"customFieldId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Movie.class, parentColumns = {"id"}, childColumns = {"movieId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldMovieCrossRef {
    private long customFieldId;
    private long movieId;

    public CustomFieldMovieCrossRef() {
        this.customFieldId = 0L;
        this.movieId = 0L;
    }

    public long getCustomFieldId() {
        return this.customFieldId;
    }

    public void setCustomFieldId(long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
}
