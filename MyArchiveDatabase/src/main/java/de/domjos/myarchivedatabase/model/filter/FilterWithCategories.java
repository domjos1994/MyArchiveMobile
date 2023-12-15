package de.domjos.myarchivedatabase.model.filter;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.category.Category;

public final class FilterWithCategories {
    @Embedded
    private Filter filter;

    @Relation(
            entity = Category.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = FilterCategoryCrossRef.class, parentColumn = "filterId", entityColumn = "categoryId")
    )
    private List<Category> categories;

    public FilterWithCategories() {
        this.filter = null;
        this.categories = new LinkedList<>();
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
