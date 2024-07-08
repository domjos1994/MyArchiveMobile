package de.domjos.myarchivedatabase.model.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.general.tag.Tag;

@Entity(
        primaryKeys = {"filterId", "tagId"},
        indices = {@Index(value = {"filterId"}), @Index(value = {"tagId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Filter.class, parentColumns = {"id"}, childColumns = {"filterId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Tag.class, parentColumns = {"id"}, childColumns = {"tagId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class FilterTagCrossRef {
    private long filterId;
    private long tagId;

    public FilterTagCrossRef() {
        this.filterId = 0L;
        this.tagId = 0L;
    }

    public long getFilterId() {
        return this.filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
