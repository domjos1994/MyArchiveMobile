package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.game.Game;

@Entity(
        primaryKeys = {"personId", "gameId"},
        indices = {@Index(value = {"personId"}), @Index(value = {"gameId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Person.class, parentColumns = {"id"}, childColumns = {"personId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Game.class, parentColumns = {"id"}, childColumns = {"gameId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class PersonGameCrossRef {
    private long personId;
    private long gameId;

    public PersonGameCrossRef() {
        this.personId = 0L;
        this.gameId = 0L;
    }

    public long getPersonId() {
        return this.personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
