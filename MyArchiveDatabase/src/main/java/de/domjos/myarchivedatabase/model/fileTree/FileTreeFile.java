package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;

@Entity(tableName = "file_tree_file")
public final class FileTreeFile extends BaseDescriptionObject {
    @ColumnInfo(name = "category")
    private long category;

    @ColumnInfo(name = "parent")
    private long parent;

    @ColumnInfo(name = "internalId")
    private long internalId;

    @ColumnInfo(name = "internalTable")
    private String internalTable;

    @ColumnInfo(name = "internalColumn")
    private String internalColumn;

    @ColumnInfo(name = "pathToFile")
    private String pathToFile;

    public FileTreeFile() {
        super();

        this.category = 0L;
        this.parent = 0L;
        this.internalId = 0L;
        this.internalTable = "";
        this.internalColumn = "";
        this.pathToFile = "";
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getInternalTable() {
        return internalTable;
    }

    public void setInternalTable(String internalTable) {
        this.internalTable = internalTable;
    }

    public String getInternalColumn() {
        return internalColumn;
    }

    public void setInternalColumn(String internalColumn) {
        this.internalColumn = internalColumn;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }
}
