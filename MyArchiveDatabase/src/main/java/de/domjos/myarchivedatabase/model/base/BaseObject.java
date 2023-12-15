package de.domjos.myarchivedatabase.model.base;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Date;

public abstract class BaseObject {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "lastUpdated")
    private Date lastUpdated;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    public BaseObject() {
        this.id = 0L;
        this.lastUpdated = new Date();
        this.timestamp = new Date();
    }


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
