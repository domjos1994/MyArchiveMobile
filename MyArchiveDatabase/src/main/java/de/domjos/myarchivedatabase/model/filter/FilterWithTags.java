package de.domjos.myarchivedatabase.model.filter;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;

public final class FilterWithTags {
    @Embedded
    private Filter filter;

    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = FilterTagCrossRef.class, parentColumn = "filterId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public FilterWithTags() {
        this.filter = null;
        this.tags = new LinkedList<>();
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
