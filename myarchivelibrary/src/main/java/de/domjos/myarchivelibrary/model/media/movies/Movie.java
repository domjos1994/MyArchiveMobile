package de.domjos.myarchivelibrary.model.media.movies;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public final class Movie extends BaseMediaObject implements DatabaseObject {
    private Type type;
    private double length;
    private String path;

    public Movie() {
        super();

        this.type = null;
        this.length = 0.0;
        this.path = "";
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
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

    @Override
    public String getTable() {
        return "movies";
    }

    public enum Type {
        DVD,
        Bluray,
        Virtual
    }
}
