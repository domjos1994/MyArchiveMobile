package de.domjos.myarchivedatabase.model.library;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.game.Game;

public final class LibraryWithGames {
    @Embedded
    private Library library;
    @Relation(
            entity = Game.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = LibraryGameCrossRef.class, parentColumn = "libraryId", entityColumn = "gameId")
    )
    private List<Game> games;

    public LibraryWithGames() {
        this.games = new LinkedList<>();
        this.library = null;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}