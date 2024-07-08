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

package de.domjos.myarchivemobile.settings;

import android.content.Context;

import java.util.LinkedHashMap;
import java.util.Map;

import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivemobile.activities.MainActivity;

public final class Globals {
    private Database database;
    private Settings settings;
    private boolean network;
    private final Map<String, Integer> page;

    public static final String BOOKS = "books";
    public static final String MOVIES = "movies";
    public static final String MUSIC = "music";
    public static final String GAMES = "games";
    public static final String HOME = "home";
    public static final String SEARCH = "search";
    public static final String RESET = "reset";
    public static final String LIBRARY = "library";
    public static final String LISTS = "lists";


    public Globals() {
        this.database = null;
        this.settings = null;
        this.network = true;
        this.page = new LinkedHashMap<>();
    }

    public Database getDatabase() {
        return this.database;
    }

    public Database getDatabase(Context context) {
        if(this.database == null) {
            this.database = new Database(context);
        }
        return this.database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Settings getSettings(Context context) {
        if(this.settings == null) {
            this.settings = new Settings(context);
        }
        return this.settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean isNetwork() {
        return this.network;
    }

    public void setNetwork(boolean network) {
        this.network = network;
    }

    public void setPage(int page, String key) {
        boolean reset = key.contains(Globals.RESET);
        key = key.replace(Globals.RESET, "");
        if(reset) {
            page = 0;
        }

        this.page.put(key, page * MainActivity.GLOBALS.getSettings().getMediaCount());
    }

    public int getPage(String key) {
        boolean reset = key.contains(Globals.RESET);
        key = key.replace(Globals.RESET, "");

        if(!this.page.containsKey(key))  {
            this.page.put(key, 0);
        }
        if(reset) {
            this.page.put(key, 0);
        }

        Integer number = this.page.get(key);
        if(number != null) {
            return number / MainActivity.GLOBALS.getSettings().getMediaCount();
        }
        return 0;
    }

    public int getOffset(String key) {
        boolean reset = key.contains(Globals.RESET);
        key = key.replace(Globals.RESET, "");

        if(!this.page.containsKey(key))  {
            this.page.put(key, 0);
        }
        if(reset) {
            this.page.put(key, 0);
        }

        Integer number = this.page.get(key);
        if(number != null) {
            return number;
        }
        return 0;
    }
}
