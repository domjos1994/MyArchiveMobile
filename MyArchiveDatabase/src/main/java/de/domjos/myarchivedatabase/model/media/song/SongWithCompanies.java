package de.domjos.myarchivedatabase.model.media.song;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanySongCrossRef;

public final class SongWithCompanies {
    @Embedded
    private Song song;

    @Relation(
            entity = Company.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanySongCrossRef.class, parentColumn = "songId", entityColumn = "companyId")
    )
    private List<Company> companies;

    public SongWithCompanies() {
        this.song = null;
        this.companies = new LinkedList<>();
    }

    public Song getSong() {
        return this.song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public List<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
}
