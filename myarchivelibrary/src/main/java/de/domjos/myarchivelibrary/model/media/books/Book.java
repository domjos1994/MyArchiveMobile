package de.domjos.myarchivelibrary.model.media.books;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public final class Book extends BaseMediaObject implements DatabaseObject {
    private Type type;
    private int numberOfPages;
    private String path;
    private String edition;
    private List<String> topics;

    public Book() {
        super();

        this.type = null;
        this.numberOfPages = 0;
        this.path = "";
        this.edition = "";
        this.topics = new LinkedList<>();
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

    public String getEdition() {
        return this.edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public List<String> getTopics() {
        return this.topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    @Override
    public String getTable() {
        return "books";
    }

    public enum Type {
        book,
        eBook,
        magazine,
        ePaper
    }
}
