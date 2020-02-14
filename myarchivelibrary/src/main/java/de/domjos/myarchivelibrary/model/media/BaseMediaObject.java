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

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;

public class BaseMediaObject extends BaseDescriptionObject {
    private String originalTitle;
    private Date releaseDate;
    private String code;
    private double price;
    private double ratingOwn;
    private double ratingWeb;
    private boolean lendOut;
    private BaseDescriptionObject category;
    private List<Person> persons;
    private List<Company> companies;
    private List<BaseDescriptionObject> tags;
    private List<LibraryObject> libraryObjects;
    private byte[] cover;
    private String ratingNote;

    public BaseMediaObject() {
        super();

        this.originalTitle = "";
        this.releaseDate = null;
        this.code = "";
        this.price = 0.0;
        this.category = null;
        this.persons = new LinkedList<>();
        this.companies = new LinkedList<>();
        this.tags = new LinkedList<>();
        this.libraryObjects = new LinkedList<>();

        this.ratingOwn = 0.0;
        this.ratingWeb = 0.0;
        this.ratingNote = "";
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Date getReleaseDate() {
        if(this.releaseDate != null) {
            return (Date) this.releaseDate.clone();
        } else {
            return null;
        }
    }

    public void setReleaseDate(Date releaseDate) {
        if(releaseDate != null) {
            this.releaseDate = (Date) releaseDate.clone();
        } else {
            this.releaseDate = null;
        }
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BaseDescriptionObject getCategory() {
        return this.category;
    }

    public void setCategory(BaseDescriptionObject category) {
        this.category = category;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<BaseDescriptionObject> getTags() {
        return this.tags;
    }

    public void setTags(List<BaseDescriptionObject> tags) {
        this.tags = tags;
    }

    public byte[] getCover() {
        if(this.cover != null) {
            return this.cover.clone();
        } else {
            return null;
        }
    }

    public void setCover(byte[] cover) {
        if(cover == null) {
            this.cover = null;
        } else {
            this.cover = Arrays.copyOf(cover, cover.length);
        }
    }

    public List<LibraryObject> getLibraryObjects() {
        return this.libraryObjects;
    }

    public void setLibraryObjects(List<LibraryObject> libraryObjects) {
        this.libraryObjects = libraryObjects;
    }

    public boolean isLendOut() {
        return this.lendOut;
    }

    public void setLendOut(boolean lendOut) {
        this.lendOut = lendOut;
    }

    public double getRatingOwn() {
        return this.ratingOwn;
    }

    public void setRatingOwn(double ratingOwn) {
        this.ratingOwn = ratingOwn;
    }

    public double getRatingWeb() {
        return this.ratingWeb;
    }

    public void setRatingWeb(double ratingWeb) {
        this.ratingWeb = ratingWeb;
    }

    public String getRatingNote() {
        return this.ratingNote;
    }

    public void setRatingNote(String ratingNote) {
        this.ratingNote = ratingNote;
    }
}
