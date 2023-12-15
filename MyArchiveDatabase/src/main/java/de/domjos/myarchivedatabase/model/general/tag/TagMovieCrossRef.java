package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

@Entity(
        primaryKeys = {"tagId", "movieId"},
        indices = {@Index(value = {"tagId"}), @Index(value = {"movieId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Movie.class, parentColumns = {"id"}, childColumns = {"movieId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class TagMovieCrossRef {
    private long tagId;
    private long movieId;

    public TagMovieCrossRef() {
        this.tagId = 0L;
        this.movieId = 0L;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
}
