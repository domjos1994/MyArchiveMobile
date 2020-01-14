package de.domjos.myarchivelibrary.model.media.music;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public final class Song extends BaseMediaObject implements DatabaseObject {
    private double length;
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

    @Override
    public String getTable() {
        return "songs";
    }
}
