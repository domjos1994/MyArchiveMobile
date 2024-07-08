package de.domjos.myarchivedatabase.model.media.album;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyAlbumCrossRef;

public final class AlbumWithCompanies {
    @Embedded
    private Album album;

    @Relation(
            entity = Company.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyAlbumCrossRef.class, parentColumn = "albumId", entityColumn = "companyId")
    )
    private List<Company> companies;

    public AlbumWithCompanies() {
        this.album = null;
        this.companies = new LinkedList<>();
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
}
