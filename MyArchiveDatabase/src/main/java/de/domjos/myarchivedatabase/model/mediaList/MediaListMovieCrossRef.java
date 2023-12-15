package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

@Entity(
        primaryKeys = {"mediaListId", "movieId"},
        indices = {@Index(value = {"mediaListId"}), @Index(value = {"movieId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = MediaList.class, parentColumns = {"id"}, childColumns = {"mediaListId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Movie.class, parentColumns = {"id"}, childColumns = {"movieId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class MediaListMovieCrossRef {
    private long mediaListId;
    private long movieId;

    public MediaListMovieCrossRef() {
        this.mediaListId = 0L;
        this.movieId = 0L;
    }

    public long getMediaListId() {
        return this.mediaListId;
    }

    public void setMediaListId(long mediaListId) {
        this.mediaListId = mediaListId;
    }

    public long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
}
