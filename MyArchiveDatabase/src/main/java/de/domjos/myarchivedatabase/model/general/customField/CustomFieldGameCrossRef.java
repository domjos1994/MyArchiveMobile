package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.game.Game;

@Entity(
        primaryKeys = {"customFieldId", "gameId"},
        indices = {@Index(value = {"customFieldId"}), @Index(value = {"gameId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomField.class, parentColumns = {"id"}, childColumns = {"customFieldId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Game.class, parentColumns = {"id"}, childColumns = {"gameId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldGameCrossRef {
    private long customFieldId;
    private long gameId;

    public CustomFieldGameCrossRef() {
        this.customFieldId = 0L;
        this.gameId = 0L;
    }

    public long getCustomFieldId() {
        return this.customFieldId;
    }

    public void setCustomFieldId(long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
