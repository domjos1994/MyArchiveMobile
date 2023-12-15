package de.domjos.myarchivedatabase.model.general.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.book.Book;

public final class CategoryWithBooks {
    @Embedded
    private Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "category"
    )
    private List<Book> books;

    public CategoryWithBooks() {
        this.category = null;
        this.books = new LinkedList<>();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
