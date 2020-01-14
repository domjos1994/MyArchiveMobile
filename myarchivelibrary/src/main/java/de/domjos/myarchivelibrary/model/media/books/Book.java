package de.domjos.myarchivelibrary.model.media.books;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public class Book extends BaseMediaObject implements DatabaseObject {
    private Type type;
    private int numberOfPages;
    private String path;

    public Book() {
        super();

        this.type = null;
        this.numberOfPages = 0;
        this.path = "";
    }

    public Book.Type getType() {
        return this.type;
    }

    public void setType(Type type) {
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

    @Override
    public String getTable() {
        return "books";
    }

    public enum Type {
        book,
        ebook,
        magazine,
        ePaper
    }
}
