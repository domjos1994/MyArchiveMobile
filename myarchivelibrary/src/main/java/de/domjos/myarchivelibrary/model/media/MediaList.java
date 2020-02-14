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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;

public class MediaList extends BaseDescriptionObject implements DatabaseObject {
    private Date deadLine;
    private List<BaseMediaObject> baseMediaObjects;

    public MediaList() {
        super();

        this.deadLine = null;
        this.baseMediaObjects = new LinkedList<>();
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

    public List<BaseMediaObject> getBaseMediaObjects() {
        return this.baseMediaObjects;
    }

    public void setBaseMediaObjects(List<BaseMediaObject> baseMediaObjects) {
        this.baseMediaObjects = baseMediaObjects;
    }

    @Override
    public String getTable() {
        return "lists";
    }
}
