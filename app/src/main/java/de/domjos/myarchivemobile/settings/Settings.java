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
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class Settings {
    private SharedPreferences sharedPreferences;
    private SharedPreferences userPreferences;

    public Settings(Context context) {

        this.sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        this.userPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(String key, T defVal) {
        if(defVal instanceof String) {
            return (T) this.sharedPreferences.getString(key, (String) defVal);
        }
        if(defVal instanceof Float) {
            return (T) (Float) this.sharedPreferences.getFloat(key, (Float) defVal);
        }
        if(defVal instanceof Integer) {
            return (T) (Integer) this.sharedPreferences.getInt(key, (Integer) defVal);
        }
        if(defVal instanceof Boolean) {
            return (T) (Boolean) this.sharedPreferences.getBoolean(key, (Boolean) defVal);
        }
        if(defVal instanceof Long) {
            return (T) (Long) this.sharedPreferences.getLong(key, (Long) defVal);
        }

        return null;
    }

    public <T> void setSetting(String key, T object) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        if(object instanceof String) {
            editor.putString(key, (String) object);
        }
        if(object instanceof Float) {
            editor.putFloat(key, (Float) object);
        }
        if(object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        }
        if(object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        }
        if(object instanceof Long) {
            editor.putLong(key, (Long) object);
        }
        editor.apply();
    }

    public boolean isNotifications() {
        return this.userPreferences.getBoolean("swtNotifications", true);
    }

    public boolean isDebugMode() {
        return this.userPreferences.getBoolean("swtDebugMode", false);
    }

    public String getEANDataKey() {
        return this.userPreferences.getString("txtEANDataKey", "");
    }

    public String getGoogleBooksKey() {
        return this.userPreferences.getString("txtGoogleBooksKey", "");
    }

    public String getIGDBKey() {
        return this.userPreferences.getString("txtIGDBKey", "");
    }

    public String getMovieDBKey() {
        return this.userPreferences.getString("txtMovieDBKey", "");
    }
}
