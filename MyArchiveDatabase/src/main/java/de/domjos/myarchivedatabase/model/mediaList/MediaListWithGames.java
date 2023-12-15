package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.game.Game;

public final class MediaListWithGames {
    @Embedded
    private MediaList mediaList;
    @Relation(
            entity = Game.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = MediaListGameCrossRef.class, parentColumn = "mediaListId", entityColumn = "gameId")
    )
    private List<Game> games;

    public MediaListWithGames() {
        this.games = new LinkedList<>();
        this.mediaList = null;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public MediaList getMediaList() {
        return this.mediaList;
    }

    public void setMediaList(MediaList mediaList) {
        this.mediaList = mediaList;
    }
}