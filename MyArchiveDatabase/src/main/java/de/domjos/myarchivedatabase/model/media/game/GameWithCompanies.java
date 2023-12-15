package de.domjos.myarchivedatabase.model.media.game;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyGameCrossRef;

public final class GameWithCompanies {
    @Embedded
    private Game game;

    @Relation(
            entity = Company.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyGameCrossRef.class, parentColumn = "gameId", entityColumn = "companyId")
    )
    private List<Company> companies;

    public GameWithCompanies() {
        this.game = null;
        this.companies = new LinkedList<>();
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
}
