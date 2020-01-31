package de.domjos.myarchivelibrary.model.media;

import androidx.annotation.NonNull;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseTitleObject;

public class MediaFilter extends BaseTitleObject implements DatabaseObject {
    private String search;
    private String categories;
    private String tags;
    private boolean books;
    private boolean movies;
    private boolean games;
    private boolean music;

    public MediaFilter() {
        super();

        this.search = "";
        this.categories = "";
        this.tags = "";
        this.books = true;
        this.movies = true;
        this.games = true;
        this.music = true;
    }

    public String getSearch() {
        return this.search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCategories() {
        return this.categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isBooks() {
        return this.books;
    }

    public void setBooks(boolean books) {
        this.books = books;
    }

    public boolean isMovies() {
        return this.movies;
    }

    public void setMovies(boolean movies) {
        this.movies = movies;
    }

    public boolean isGames() {
        return this.games;
    }

    public void setGames(boolean games) {
        this.games = games;
    }

    public boolean isMusic() {
        return this.music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getTitle();
    }

    @Override
    public String getTable() {
        return "filters";
    }
}
