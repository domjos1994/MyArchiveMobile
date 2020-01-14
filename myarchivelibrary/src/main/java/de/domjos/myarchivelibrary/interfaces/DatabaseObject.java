package de.domjos.myarchivelibrary.interfaces;

public interface DatabaseObject {

    void setId(long id);
    long getId();

    long getLastUpdated();

    void setTimestamp(long timestamp);
    long getTimestamp();

    String getTable();
}
