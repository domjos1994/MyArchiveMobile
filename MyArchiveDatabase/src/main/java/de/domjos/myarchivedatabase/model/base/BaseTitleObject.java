package de.domjos.myarchivedatabase.model.base;

import androidx.room.ColumnInfo;

public abstract class BaseTitleObject extends BaseObject {
    @ColumnInfo(name = "title")
    private String title;

    public BaseTitleObject() {
        super();
        this.title = "";
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
