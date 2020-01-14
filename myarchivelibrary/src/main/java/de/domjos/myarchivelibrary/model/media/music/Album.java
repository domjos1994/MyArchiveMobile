package de.domjos.myarchivelibrary.model.media.music;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public final class Album extends BaseMediaObject implements DatabaseObject {
    private Type type;
    private int numberOfDisks;
    private double length;
    private List<Song> songs;

    public Album() {
        super();

        this.type = null;
        this.numberOfDisks = 0;
        this.length = 0.0;
        this.songs = new LinkedList<>();
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getNumberOfDisks() {
        return this.numberOfDisks;
    }

    public void setNumberOfDisks(int numberOfDisks) {
        this.numberOfDisks = numberOfDisks;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public String getTable() {
        return "albums";
    }

    public enum Type {
        AudioCD,
        Vinyl,
        MP3
    }
}
