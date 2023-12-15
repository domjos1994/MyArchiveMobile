package de.domjos.myarchivedatabase.model.library;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.game.Game;

@Entity(
        primaryKeys = {"libraryId", "gameId"},
        indices = {@Index(value = {"libraryId"}), @Index(value = {"gameId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Library.class, parentColumns = {"id"}, childColumns = {"libraryId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Game.class, parentColumns = {"id"}, childColumns = {"gameId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class LibraryGameCrossRef {
    private long libraryId;
    private long gameId;

    public LibraryGameCrossRef() {
        this.libraryId = 0L;
        this.gameId = 0L;
    }

    public long getLibraryId() {
        return this.libraryId;
    }

    public void setLibraryId(long libraryId) {
        this.libraryId = libraryId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
