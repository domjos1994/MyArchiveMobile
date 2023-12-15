package de.domjos.myarchivedatabase.model.media.book;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.media.AbstractMedia;

@Entity(tableName = "books")
public final class Book extends AbstractMedia {
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "numberOfPages")
    private int numberOfPages;

    @ColumnInfo(name = "path")
    private String path;

    @ColumnInfo(name = "edition")
    private String edition;

    @ColumnInfo(name = "topics")
    private String topics;


    public Book() {
        super();

        this.type = "";
        this.numberOfPages = 0;
        this.path = "";
        this.edition = "";
        this.topics = "";
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfPages() {
        return this.numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEdition() {
        return this.edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getTopics() {
        return this.topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }
}
