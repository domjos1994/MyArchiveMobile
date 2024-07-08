package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;

@Entity(tableName = "file_tree")
public final class FileTree extends BaseDescriptionObject {
    @ColumnInfo(name = "category")
    private long category;

    @ColumnInfo(name = "gallery")
    private boolean gallery;

    @ColumnInfo(name = "system")
    private boolean system;

    @ColumnInfo(name = "parent")
    private long parent;

    public FileTree() {
        super();

        this.category = 0L;
        this.gallery = false;
        this.system = false;
        this.parent = 0L;
    }

    public long getCategory() {
        return this.category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public boolean isGallery() {
        return this.gallery;
    }

    public void setGallery(boolean gallery) {
        this.gallery = gallery;
    }

    public boolean isSystem() {
        return this.system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public long getParent() {
        return this.parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }
}
