package de.domjos.myarchivedatabase.model.media.book;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyBookCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyMovieCrossRef;
import de.domjos.myarchivedatabase.model.media.movie.Movie;

public final class BookWithCompanies {
    @Embedded
    private Book book;

    @Relation(
            entity = Company.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyBookCrossRef.class, parentColumn = "bookId", entityColumn = "companyId")
    )
    private List<Company> companies;

    public BookWithCompanies() {
        this.book = null;
        this.companies = new LinkedList<>();
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
}
