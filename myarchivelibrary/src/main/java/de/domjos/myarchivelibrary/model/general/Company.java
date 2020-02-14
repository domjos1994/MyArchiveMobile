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

package de.domjos.myarchivelibrary.model.general;

import java.util.Arrays;
import java.util.Date;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;

public final class Company extends BaseDescriptionObject implements DatabaseObject {
    private Date foundation;
    private byte[] cover;

    public Company() {
        super();

        this.foundation = null;
        this.cover = null;
    }

    public Date getFoundation() {
        if(this.foundation != null) {
            return (Date) this.foundation.clone();
        } else {
            return null;
        }
    }

    public void setFoundation(Date foundation) {
        if(foundation != null) {
            this.foundation = (Date) foundation.clone();
        } else {
            this.foundation = null;
        }
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

    @Override
    public String getTable() {
        return "companies";
    }
}
