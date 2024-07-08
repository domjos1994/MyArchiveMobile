package de.domjos.myarchivedatabase.model.media.game;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagGameCrossRef;

public final class GameWithTags {
    @Embedded
    private Game game;
    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagGameCrossRef.class, parentColumn = "gameId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public GameWithTags() {
        this.game = null;
        this.tags = new LinkedList<>();
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}