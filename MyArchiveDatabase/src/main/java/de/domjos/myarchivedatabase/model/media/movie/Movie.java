package de.domjos.myarchivedatabase.model.media.movie;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.media.AbstractMedia;

@Entity(tableName = "movies")
public final class Movie extends AbstractMedia {
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "length")
    private double length;

    @ColumnInfo(name = "path")
    private String path;

    public Movie() {
        super();
        this.type = "";
        this.length = 0.0;
        this.path = "";
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
