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

package de.domjos.myarchivelibrary.model.media.games;

import java.util.Date;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public class Game extends BaseMediaObject implements DatabaseObject {
    private Type type;
    private double length;
    private Date lastPlayed;

    public Game() {
        super();

        this.type = null;
        this.length = 0.0;
        this.lastPlayed = null;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Date getLastPlayed() {
        if(this.lastPlayed == null) {
            return null;
        } else {
            return (Date) this.lastPlayed.clone();
        }
    }

    public void setLastPlayed(Date lastPlayed) {
        if(lastPlayed == null) {
            this.lastPlayed = null;
        } else {
            this.lastPlayed = (Date) lastPlayed.clone();
        }
    }

    @Override
    public String getTable() {
        return "games";
    }

    public enum Type {
        console,
        computer,
        mobile
    }
}
