package de.domjos.myarchivedatabase.model.general.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.fileTree.FileTree;

public final class CategoryWithFileTree {
    @Embedded
    private Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "category"
    )
    private List<FileTree> fileTrees;

    public CategoryWithFileTree() {
        this.category = null;
        this.fileTrees = new LinkedList<>();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<FileTree> getFileTrees() {
        return fileTrees;
    }

    public void setFileTrees(List<FileTree> fileTrees) {
        this.fileTrees = fileTrees;
    }
}
