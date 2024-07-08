package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;

public final class FileTreeFileWithTags {
    @Embedded
    private FileTreeFile fileTreeFile;

    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = FileTreeFileTagCrossRef.class, parentColumn = "fileTreeFileId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public FileTreeFileWithTags() {
        super();

        this.fileTreeFile = null;
        this.tags = new LinkedList<>();
    }

    public FileTreeFile getFileTreeFile() {
        return this.fileTreeFile;
    }

    public void setFileTreeFile(FileTreeFile fileTreeFile) {
        this.fileTreeFile = fileTreeFile;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
