package de.domjos.myarchivedatabase.model.general.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.fileTree.FileTreeFile;

public final class CategoryWithFileTreeFile {
    @Embedded
    private Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "category"
    )
    private List<FileTreeFile> fileTreeFiles;

    public CategoryWithFileTreeFile() {
        this.category = null;
        this.fileTreeFiles = new LinkedList<>();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<FileTreeFile> getFileTreeFiles() {
        return fileTreeFiles;
    }

    public void setFileTreeFiles(List<FileTreeFile> fileTrees) {
        this.fileTreeFiles = fileTrees;
    }
}
