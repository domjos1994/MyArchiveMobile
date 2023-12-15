package de.domjos.myarchivedatabase.model.media.book;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagBookCrossRef;

public final class BookWithTags {
    @Embedded
    private Book book;
    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagBookCrossRef.class, parentColumn = "bookId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public BookWithTags() {
        this.book = null;
        this.tags = new LinkedList<>();
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}