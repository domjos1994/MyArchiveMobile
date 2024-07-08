package de.domjos.myarchivedatabase.model.fileTree;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.general.tag.Tag;

@Entity(
        primaryKeys = {"fileTreeFileId", "tagId"},
        indices = {@Index(value = {"fileTreeFileId"}), @Index(value = {"tagId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = FileTreeFile.class, parentColumns = {"id"}, childColumns = {"fileTreeFileId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class FileTreeFileTagCrossRef {
    private long fileTreeFileId;
    private long tagId;

    public FileTreeFileTagCrossRef() {
        this.fileTreeFileId = 0L;
        this.tagId = 0L;
    }

    public long getFileTreeFileId() {
        return this.fileTreeFileId;
    }

    public void setFileTreeFileId(long fileTreeFileId) {
        this.fileTreeFileId = fileTreeFileId;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
