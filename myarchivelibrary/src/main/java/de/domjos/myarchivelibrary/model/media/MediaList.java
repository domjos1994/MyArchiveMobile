package de.domjos.myarchivelibrary.model.media;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;

public class MediaList extends BaseDescriptionObject implements DatabaseObject {
    private Date deadLine;
    private List<BaseMediaObject> baseMediaObjects;

    public MediaList() {
        super();

        this.deadLine = null;
        this.baseMediaObjects = new LinkedList<>();
    }

    public Date getDeadLine() {
        return this.deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    public List<BaseMediaObject> getBaseMediaObjects() {
        return this.baseMediaObjects;
    }

    public void setBaseMediaObjects(List<BaseMediaObject> baseMediaObjects) {
        this.baseMediaObjects = baseMediaObjects;
    }

    @Override
    public String getTable() {
        return "lists";
    }
}
