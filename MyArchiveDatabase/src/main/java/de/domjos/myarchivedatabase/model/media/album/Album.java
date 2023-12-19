package de.domjos.myarchivedatabase.model.media.album;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.AbstractMedia;
import de.domjos.myarchivedatabase.model.media.song.Song;

@Entity(tableName = "albums")
public final class Album extends AbstractMedia {
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "numberOfDisks")
    private int numberOfDisks;

    @Ignore
    private List<Song> songs;

    public Album() {
        super();
        this.type = "";
        this.numberOfDisks = 0;

        this.songs = new LinkedList<>();
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

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
