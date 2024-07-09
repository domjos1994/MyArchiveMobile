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

package de.domjos.myarchivelibrary.model.general;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Date;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseObject;

public final class Person extends BaseObject implements DatabaseObject {
    private String firstName;
    private String lastName;
    private Date birthDate;
    private byte[] image;
    private String description;

    public Person() {
        super();

        this.firstName = "";
        this.lastName = "";
        this.birthDate = null;
        this.image = null;
        this.description = "";
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        if(this.birthDate != null) {
            return (Date) this.birthDate.clone();
        } else {
            return null;
        }
    }

    public void setBirthDate(Date birthDate) {
        if(birthDate != null) {
            this.birthDate = (Date) birthDate.clone();
        } else {
            this.birthDate = null;
        }
    }

    public byte[] getImage() {
        if(this.image != null) {
            return this.image.clone();
        } else {
            return null;
        }
    }

    public void setImage(byte[] image) {
        if(image == null) {
            this.image = null;
        } else {
            this.image = Arrays.copyOf(image, image.length);
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTable() {
        return "persons";
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getFirstName() + " " + this.getLastName()).trim();
    }
}
