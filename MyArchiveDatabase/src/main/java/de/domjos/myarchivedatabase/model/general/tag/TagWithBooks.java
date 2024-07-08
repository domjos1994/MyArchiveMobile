package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.book.Book;

public final class TagWithBooks {
    @Embedded
    private Tag tag;
    @Relation(
            entity = Book.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagBookCrossRef.class, parentColumn = "tagId", entityColumn = "bookId")
    )
    private List<Book> books;

    public TagWithBooks() {
        this.books = new LinkedList<>();
        this.tag = null;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}