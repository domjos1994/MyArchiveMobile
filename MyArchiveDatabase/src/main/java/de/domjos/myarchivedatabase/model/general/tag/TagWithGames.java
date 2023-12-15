package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.game.Game;

public final class TagWithGames {
    @Embedded
    private Tag tag;
    @Relation(
            entity = Game.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagGameCrossRef.class, parentColumn = "tagId", entityColumn = "gameId")
    )
    private List<Game> games;

    public TagWithGames() {
        this.games = new LinkedList<>();
        this.tag = null;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}