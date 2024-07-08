package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.game.Game;

public final class CompanyWithGames {
    @Embedded
    private Company company;

    @Relation(
            entity = Game.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyGameCrossRef.class, parentColumn = "companyId", entityColumn = "gameId")
    )
    private List<Game> games;

    public CompanyWithGames() {
        this.company = null;
        this.games = new LinkedList<>();
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
