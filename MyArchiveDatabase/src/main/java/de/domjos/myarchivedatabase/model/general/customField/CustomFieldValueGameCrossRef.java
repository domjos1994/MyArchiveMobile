package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.game.Game;

@Entity(
        primaryKeys = {"customFieldValueId", "gameId"},
        indices = {@Index(value = {"customFieldValueId"}), @Index(value = {"gameId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = CustomFieldValue.class, parentColumns = {"id"}, childColumns = {"customFieldValueId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Game.class, parentColumns = {"id"}, childColumns = {"gameId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CustomFieldValueGameCrossRef {
    private long customFieldValueId;
    private long gameId;

    public CustomFieldValueGameCrossRef() {
        super();

        this.customFieldValueId = 0L;
        this.gameId = 0L;
    }

    public long getCustomFieldValueId() {
        return this.customFieldValueId;
    }

    public void setCustomFieldValueId(long customFieldValueId) {
        this.customFieldValueId = customFieldValueId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
