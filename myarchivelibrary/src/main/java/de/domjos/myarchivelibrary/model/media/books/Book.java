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

package de.domjos.myarchivelibrary.model.media.books;

import java.util.Date;
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
    private Date lastRead;

    public Book() {
        super();

        this.type = null;
        this.numberOfPages = 0;
        this.path = "";
        this.edition = "";
        this.lastRead = null;
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

    public Date getLastRead() {
        return this.lastRead;
    }

    public void setLastRead(Date lastRead) {
        this.lastRead = lastRead;
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
