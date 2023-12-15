package de.domjos.myarchivedatabase.model.media.song;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.media.AbstractMedia;

@Entity(tableName = "songs")
public final class Song extends AbstractMedia {
    @ColumnInfo(name = "length")
    private double length;

    @ColumnInfo(name = "path")
    private String path;

    public Song() {
        super();
        this.length = 0.0;
        this.path = "";
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
