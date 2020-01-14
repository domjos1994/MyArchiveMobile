package de.domjos.myarchivelibrary.model.base;

import java.util.Date;

public class BaseObject {
    private long id;
    private long timestamp;
    private final long lastUpdated;

    public BaseObject() {
        super();

        this.id = 0;
        this.timestamp = new Date().getTime();
        this.lastUpdated = new Date().getTime();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLastUpdated() {
        return this.lastUpdated;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
