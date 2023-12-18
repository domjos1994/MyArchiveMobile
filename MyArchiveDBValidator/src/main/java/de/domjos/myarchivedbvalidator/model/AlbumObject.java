package de.domjos.myarchivedbvalidator.model;

import androidx.room.ColumnInfo;

import java.util.LinkedList;
import java.util.List;

public final class AlbumObject extends MediaObject {
    private String type;
    private int numberOfDisks;
    private List<SongObject> songObjects;

    public AlbumObject() {
        super();

        this.type = "";
        this.numberOfDisks = 0;
        this.songObjects = new LinkedList<>();
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfDisks() {
        return this.numberOfDisks;
    }

    public void setNumberOfDisks(int numberOfDisks) {
        this.numberOfDisks = numberOfDisks;
    }

    public List<SongObject> getSongObjects() {
        return this.songObjects;
    }

    public void setSongObjects(List<SongObject> songObjects) {
        this.songObjects = songObjects;
    }
}
