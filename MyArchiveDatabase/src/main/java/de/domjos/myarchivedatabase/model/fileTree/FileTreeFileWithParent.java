package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

public final class FileTreeFileWithParent {
    @Embedded
    private FileTreeFile fileTreeFile;
    @Relation(
            parentColumn = "id",
            entityColumn = "parent"
    )
    private List<FileTree> fileTrees;

    public FileTreeFileWithParent() {
        this.fileTreeFile = null;
        this.fileTrees = new LinkedList<>();
    }

    public FileTreeFile getFileTreeFile() {
        return fileTreeFile;
    }

    public void setFileTreeFile(FileTreeFile fileTreeFile) {
        this.fileTreeFile = fileTreeFile;
    }

    public List<FileTree> getFileTrees() {
        return fileTrees;
    }

    public void setFileTrees(List<FileTree> fileTrees) {
        this.fileTrees = fileTrees;
    }
}
