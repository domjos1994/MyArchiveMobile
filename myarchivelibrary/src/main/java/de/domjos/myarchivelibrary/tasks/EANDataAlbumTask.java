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

package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.tasks.AbstractTask;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.services.EANDataWebservice;

public class EANDataAlbumTask extends AbstractTask<String, Void, List<Album>> {
    private String key;

    public EANDataAlbumTask(Activity activity, boolean showNotifications, int icon, String key) {
        super(activity, R.string.service_ean_data_search, R.string.service_ean_data_search_content, showNotifications, icon);
        this.key = key;
    }

    @Override
    protected List<Album> doInBackground(String... strings) {
        LinkedList<Album> albums = new LinkedList<>();

        for(String code : strings) {
            try {
                EANDataWebservice eanDataService = new EANDataWebservice(code, this.key, super.getContext());
                Album album = eanDataService.executeAlbum();

                if(album != null) {
                    album.setCode(code);
                    albums.add(album);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return albums;
    }
}
