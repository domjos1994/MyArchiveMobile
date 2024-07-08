package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;

@Entity(tableName = "customFields")
public final class CustomField extends BaseDescriptionObject {
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "allowedValues")
    private String allowedValues;

    @ColumnInfo(name = "albums")
    private boolean albums;

    @ColumnInfo(name = "books")
    private boolean books;

    @ColumnInfo(name = "games")
    private boolean games;

    @ColumnInfo(name = "movies")
    private boolean movies;

    public CustomField() {
        super();

        this.type = "";
        this.allowedValues = "";
        this.albums = false;
        this.books = false;
        this.games = false;
        this.movies = false;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAllowedValues() {
        return this.allowedValues;
    }

    public void setAllowedValues(String allowedValues) {
        this.allowedValues = allowedValues;
    }

    public boolean isAlbums() {
        return this.albums;
    }

    public void setAlbums(boolean albums) {
        this.albums = albums;
    }

    public boolean isBooks() {
        return this.books;
    }

    public void setBooks(boolean books) {
        this.books = books;
    }

    public boolean isGames() {
        return this.games;
    }

    public void setGames(boolean games) {
        this.games = games;
    }

    public boolean isMovies() {
        return this.movies;
    }

    public void setMovies(boolean movies) {
        this.movies = movies;
    }
}
