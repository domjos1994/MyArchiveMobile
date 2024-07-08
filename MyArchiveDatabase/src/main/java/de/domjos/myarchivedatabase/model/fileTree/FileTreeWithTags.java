package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;

public final class FileTreeWithTags {
    @Embedded
    private FileTree fileTree;

    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = FileTreeTagCrossRef.class, parentColumn = "fileTreeId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public FileTreeWithTags() {
        super();

        this.fileTree = null;
        this.tags = new LinkedList<>();
    }

    public FileTree getFileTree() {
        return this.fileTree;
    }

    public void setFileTree(FileTree fileTree) {
        this.fileTree = fileTree;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
