package de.domjos.myarchivedatabase.model.media.movie;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyMovieCrossRef;

public final class MovieWithCompanies {
    @Embedded
    private Movie movie;

    @Relation(
            entity = Company.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyMovieCrossRef.class, parentColumn = "movieId", entityColumn = "companyId")
    )
    private List<Company> companies;

    public MovieWithCompanies() {
        this.movie = null;
        this.companies = new LinkedList<>();
    }

    public Movie getMovie() {
        return this.movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
}
