package de.domjos.myarchivedatabase.model.media.game;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.media.AbstractMedia;

@Entity(tableName = "games")
public final class Game extends AbstractMedia {
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "length")
    private double length;

    public Game() {
        super();

        this.type = "";
        this.length = 0.0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
