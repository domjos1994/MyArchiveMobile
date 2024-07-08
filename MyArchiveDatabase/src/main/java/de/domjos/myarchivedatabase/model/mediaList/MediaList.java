package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;

@Entity(tableName = "mediaList")
public final class MediaList extends BaseDescriptionObject {
    @ColumnInfo(name = "deadLine")
    private Date deadline;

    public MediaList() {
        super();
        this.deadline = null;
    }

    public Date getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
