package de.domjos.myarchivedatabase.model.base;

import androidx.room.ColumnInfo;

public abstract class BaseDescriptionObject extends BaseTitleObject {
    @ColumnInfo(name = "description")
    private String description;

    public BaseDescriptionObject() {
        super();
        this.description = "";
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
