package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

@Entity(
        primaryKeys = {"companyId", "movieId"},
        indices = {@Index(value = {"companyId"}), @Index(value = {"movieId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Company.class, parentColumns = {"id"}, childColumns = {"companyId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Movie.class, parentColumns = {"id"}, childColumns = {"movieId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CompanyMovieCrossRef {
    private long companyId;
    private long movieId;

    public CompanyMovieCrossRef() {
        this.companyId = 0L;
        this.movieId = 0L;
    }

    public long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getMovieId() {
        return this.movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
}
