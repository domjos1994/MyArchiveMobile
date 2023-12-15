package de.domjos.myarchivedatabase.model.mediaList;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.book.Book;

public final class MediaListWithBooks {
    @Embedded
    private MediaList mediaList;
    @Relation(
            entity = Book.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = MediaListBookCrossRef.class, parentColumn = "mediaListId", entityColumn = "bookId")
    )
    private List<Book> books;

    public MediaListWithBooks() {
        this.books = new LinkedList<>();
        this.mediaList = null;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public MediaList getMediaList() {
        return this.mediaList;
    }

    public void setMediaList(MediaList mediaList) {
        this.mediaList = mediaList;
    }
}