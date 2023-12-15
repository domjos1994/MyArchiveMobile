package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.game.Game;

@Entity(
        primaryKeys = {"mediaListId", "gameId"},
        indices = {@Index(value = {"mediaListId"}), @Index(value = {"gameId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = MediaList.class, parentColumns = {"id"}, childColumns = {"mediaListId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Game.class, parentColumns = {"id"}, childColumns = {"gameId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class MediaListGameCrossRef {
    private long mediaListId;
    private long gameId;

    public MediaListGameCrossRef() {
        this.mediaListId = 0L;
        this.gameId = 0L;
    }

    public long getMediaListId() {
        return this.mediaListId;
    }

    public void setMediaListId(long mediaListId) {
        this.mediaListId = mediaListId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
