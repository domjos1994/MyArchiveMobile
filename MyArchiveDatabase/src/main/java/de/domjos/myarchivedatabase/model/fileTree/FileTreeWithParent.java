package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

public final class FileTreeWithParent {
    @Embedded
    private FileTree fileTree;
    @Relation(
            parentColumn = "id",
            entityColumn = "parent"
    )
    private List<FileTree> fileTrees;

    public FileTreeWithParent() {
        this.fileTree = null;
        this.fileTrees = new LinkedList<>();
    }

    public FileTree getFileTree() {
        return fileTree;
    }

    public void setFileTree(FileTree fileTree) {
        this.fileTree = fileTree;
    }

    public List<FileTree> getFileTrees() {
        return fileTrees;
    }

    public void setFileTrees(List<FileTree> fileTrees) {
        this.fileTrees = fileTrees;
    }
}
