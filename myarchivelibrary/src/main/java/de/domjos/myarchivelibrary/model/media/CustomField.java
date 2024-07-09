/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2024 Dominic Joas.
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

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;

public class CustomField extends BaseDescriptionObject implements DatabaseObject {
    private String type;
    private String allowedValues;
    private boolean books;
    private boolean movies;
    private boolean games;
    private boolean albums;

    public CustomField() {
        super();

        this.type = "";
        this.allowedValues = "";
        this.books = false;
        this.movies = false;
        this.games = false;
        this.albums = false;
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

    public boolean isAlbums() {
        return this.albums;
    }

    public void setAlbums(boolean albums) {
        this.albums = albums;
    }

    @Override
    public String getTable() {
        return "customFields";
    }
}
