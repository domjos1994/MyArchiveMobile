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

package de.domjos.myarchiveservices.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

abstract class JSONService {

    static String readUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.connect();
        String error = read(connection.getErrorStream());
        if(!error.isEmpty()) {
            return error;
        }
        return read(connection.getInputStream());
    }

    static String readUrl(URL url, List<String> params) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setRequestProperty("Accept", "application/json");
        for(String param : params) {
            connection.setRequestProperty(param.split(":")[0], param.replace(param.split(":")[0], "").trim().replace(":", "").trim());
        }
        connection.connect();
        String error = read(connection.getErrorStream());
        if(!error.isEmpty()) {
            return error;
        }
        return read(connection.getInputStream());
    }

    private static String read(InputStream inputStream) throws IOException {
        if(inputStream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            try (inputStream; streamReader; BufferedReader reader = new BufferedReader(streamReader)) {

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    String getString(JSONObject obj, String key) throws JSONException {
        if(obj.has(key)) {
            if(!obj.isNull(key)) {
                return obj.getString(key);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    Integer getInt(JSONObject obj, String key) throws JSONException {
        if(obj.has(key)) {
            if(!obj.isNull(key)) {
                return obj.getInt(key);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    Double getDouble(JSONObject obj, String key) throws JSONException {
        if(obj.has(key)) {
            if(!obj.isNull(key)) {
                return obj.getDouble(key);
            } else {
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }
}
