package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.movie.Movie;

public final class CompanyWithMovies {
    @Embedded
    private Company company;

    @Relation(
            entity = Movie.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyMovieCrossRef.class, parentColumn = "companyId", entityColumn = "movieId")
    )
    private List<Movie> movies;

    public CompanyWithMovies() {
        this.company = null;
        this.movies = new LinkedList<>();
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Movie> getMovies() {
        return this.movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
