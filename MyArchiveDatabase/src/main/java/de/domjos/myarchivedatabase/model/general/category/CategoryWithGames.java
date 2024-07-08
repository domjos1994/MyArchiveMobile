package de.domjos.myarchivedatabase.model.general.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.game.Game;

public final class CategoryWithGames {
    @Embedded
    private Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "category"
    )
    private List<Game> games;

    public CategoryWithGames() {
        this.category = null;
        this.games = new LinkedList<>();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
