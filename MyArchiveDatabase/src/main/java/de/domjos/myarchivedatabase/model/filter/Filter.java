package de.domjos.myarchivedatabase.model.filter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import de.domjos.myarchivedatabase.model.base.BaseTitleObject;

@Entity(tableName = "filters")
public final class Filter extends BaseTitleObject {
    @ColumnInfo(name = "search")
    private String search;

    @ColumnInfo(name = "searchInAlbums")
    private boolean albums;

    @ColumnInfo(name = "searchInBooks")
    private boolean books;

    @ColumnInfo(name = "searchInGames")
    private boolean games;

    @ColumnInfo(name = "searchInMovies")
    private boolean movies;

    @ColumnInfo(name = "movies")
    private boolean mediaLists;

    public Filter() {
        super();

        this.search = "";
        this.albums = false;
        this.books = false;
        this.games = false;
        this.movies = false;
        this.mediaLists = false;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean isAlbums() {
        return albums;
    }

    public void setAlbums(boolean albums) {
        this.albums = albums;
    }

    public boolean isBooks() {
        return books;
    }

    public void setBooks(boolean books) {
        this.books = books;
    }

    public boolean isGames() {
        return games;
    }

    public void setGames(boolean games) {
        this.games = games;
    }

    public boolean isMovies() {
        return movies;
    }

    public void setMovies(boolean movies) {
        this.movies = movies;
    }

    public boolean isMediaLists() {
        return mediaLists;
    }

    public void setMediaLists(boolean mediaLists) {
        this.mediaLists = mediaLists;
    }
}
