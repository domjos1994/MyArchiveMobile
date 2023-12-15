package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.media.game.Game;

@Entity(
        primaryKeys = {"companyId", "gameId"},
        indices = {@Index(value = {"companyId"}), @Index(value = {"gameId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Company.class, parentColumns = {"id"}, childColumns = {"companyId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Game.class, parentColumns = {"id"}, childColumns = {"gameId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class CompanyGameCrossRef {
    private long companyId;
    private long gameId;

    public CompanyGameCrossRef() {
        this.companyId = 0L;
        this.gameId = 0L;
    }

    public long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
