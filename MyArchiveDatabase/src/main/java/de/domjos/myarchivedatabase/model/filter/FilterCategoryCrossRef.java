package de.domjos.myarchivedatabase.model.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import de.domjos.myarchivedatabase.model.general.category.Category;

@Entity(
        primaryKeys = {"filterId", "categoryId"},
        indices = {@Index(value = {"filterId"}), @Index(value = {"categoryId"})},
        foreignKeys = {
                @ForeignKey(
                        entity = Filter.class, parentColumns = {"id"}, childColumns = {"filterId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Category.class, parentColumns = {"id"}, childColumns = {"categoryId"},
                        onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE
                )
        }
)
public final class FilterCategoryCrossRef {
    private long filterId;
    private long categoryId;

    public FilterCategoryCrossRef() {
        this.filterId = 0L;
        this.categoryId = 0L;
    }

    public long getFilterId() {
        return this.filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
