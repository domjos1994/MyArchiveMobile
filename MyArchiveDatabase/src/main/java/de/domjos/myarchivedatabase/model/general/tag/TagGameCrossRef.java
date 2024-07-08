package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.game.Game;

@Entity(
        primaryKeys = {"tagId", "gameId"},
        indices = {@Index(value = {"tagId"}), @Index(value = {"gameId"})},
        foreignKeys = {
            @ForeignKey(
                    entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Game.class, parentColumns = {"id"}, childColumns = {"gameId"},
                    onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
            )
        }
)
public final class TagGameCrossRef {
    private long tagId;
    private long gameId;

    public TagGameCrossRef() {
        this.tagId = 0L;
        this.gameId = 0L;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
