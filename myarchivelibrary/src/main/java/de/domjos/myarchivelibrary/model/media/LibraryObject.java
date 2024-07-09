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

import java.util.Date;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseObject;
import de.domjos.myarchivelibrary.model.general.Person;

public class LibraryObject extends BaseObject implements DatabaseObject {
    private Person person;
    private int numberOfDays;
    private int numberOfWeeks;
    private Date deadLine;
    private Date returned;

    public LibraryObject() {
        super();

        this.person = null;
        this.numberOfDays = 0;
        this.numberOfWeeks = 0;
        this.deadLine = null;
        this.returned = null;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public int getNumberOfWeeks() {
        return this.numberOfWeeks;
    }

    public void setNumberOfWeeks(int numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public Date getDeadLine() {
        if(this.deadLine != null) {
            return (Date) this.deadLine.clone();
        } else {
            return null;
        }
    }

    public void setDeadLine(Date deadLine) {
        if(deadLine != null) {
            this.deadLine = (Date) deadLine.clone();
        } else {
            this.deadLine = null;
        }
    }

    public Date getReturned() {
        if(this.returned != null) {
            return (Date) this.returned.clone();
        } else {
            return null;
        }
    }

    public void setReturned(Date returned) {
        if(returned != null) {
            this.returned = (Date) returned.clone();
        } else {
            this.returned = null;
        }
    }

    @Override
    public String getTable() {
        return "library";
    }
}
