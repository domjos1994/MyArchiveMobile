package de.domjos.myarchivelibrary.model.media.books;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;

public final class Magazine extends Book implements DatabaseObject {
    private String edition;
    private List<String> topics;

    public Magazine() {
        super();

        this.edition = "";
        this.topics = new LinkedList<>();
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
        return "magazines";
    }
}
