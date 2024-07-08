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

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivedatabase.model.media.AbstractMedia;

public abstract class TitleWebservice<T extends AbstractMedia> extends JSONService {
    final Context CONTEXT;
    final long SEARCH;

    TitleWebservice(Context context, long id) {
        super();
        this.CONTEXT = context;
        this.SEARCH = id;
    }

    public abstract T execute() throws JSONException, IOException;

    public abstract List<AbstractMedia> getMedia(String search) throws IOException, JSONException;

    public abstract String getTitle();

    public abstract String getUrl();

    public abstract String getType();

    @Override
    @NonNull
    public String toString() {
        return this.getType();
    }
}
