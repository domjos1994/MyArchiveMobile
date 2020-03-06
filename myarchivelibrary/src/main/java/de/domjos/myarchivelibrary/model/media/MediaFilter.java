/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
    private boolean list;
    private MediaList mediaList;
    private String customFields;

    public MediaFilter() {
        super();

        this.search = "";
        this.categories = "";
        this.tags = "";
        this.books = true;
        this.movies = true;
        this.games = true;
        this.music = true;
        this.customFields = "";
        this.list = false;
        this.mediaList = null;
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

    public String getCustomFields() {
        return this.customFields;
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }

    public boolean isList() {
        return this.list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public MediaList getMediaList() {
        return this.mediaList;
    }

    public void setMediaList(MediaList mediaList) {
        this.mediaList = mediaList;
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
