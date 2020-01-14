package de.domjos.myarchivelibrary.model.media.games;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public class Game extends BaseMediaObject implements DatabaseObject {
    private Type type;
    private double length;

    public Game() {
        super();

        this.type = null;
        this.length = 0.0;
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

    @Override
    public String getTable() {
        return "games";
    }

    public enum Type {
        console,
        computer,
        mobile
    }
}
