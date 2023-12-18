package de.domjos.myarchivedbvalidator.model;

import androidx.room.ColumnInfo;

public final class SongObject extends MediaObject {
    private double length;
    private String path;

    public SongObject() {
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
