package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.general.tag.Tag;

@Entity(
        primaryKeys = {"fileTreeId", "tagId"},
        indices = {@Index(value = {"fileTreeId"}), @Index(value = {"tagId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = FileTree.class, parentColumns = {"id"}, childColumns = {"fileTreeId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class FileTreeTagCrossRef {
    private long fileTreeId;
    private long tagId;

    public FileTreeTagCrossRef() {
        this.fileTreeId = 0L;
        this.tagId = 0L;
    }

    public long getFileTreeId() {
        return this.fileTreeId;
    }

    public void setFileTreeId(long fileTreeId) {
        this.fileTreeId = fileTreeId;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
