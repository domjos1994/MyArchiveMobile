package de.domjos.myarchivedatabase.model.media.album;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.media.AbstractMedia;

@Entity(tableName = "albums")
public final class Album extends AbstractMedia {
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "numberOfDisks")
    private int numberOfDisks;

    public Album() {
        super();
        this.type = "";
        this.numberOfDisks = 0;
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
}
