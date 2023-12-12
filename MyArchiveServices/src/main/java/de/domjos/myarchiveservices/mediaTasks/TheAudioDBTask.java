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

package de.domjos.myarchiveservices.mediaTasks;

import android.app.Activity;

import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchiveservices.customTasks.CustomAbstractTask;
import de.domjos.myarchiveservices.services.AudioDBWebservice;

public class TheAudioDBTask extends CustomAbstractTask<Long[], Void, List<Album>> {

    public TheAudioDBTask(Activity activity, boolean showNotifications, int icon) {
        super(activity, R.string.service_audio_db_search, R.string.service_audio_db_search_content, showNotifications, icon);
    }

    @Override
    protected List<Album> doInBackground(Long... ids) {
        LinkedList<Album> movies = new LinkedList<>();

        for(Long id : ids) {
            try {
                AudioDBWebservice movieDBWebService = new AudioDBWebservice(super.getContext(), id);
                Album album = movieDBWebService.execute();

                if(album != null) {
                    movies.add(album);
                }
            } catch (InterruptedIOException ignored) {
            } catch (UnknownHostException ex) {
                this.printMessage(getContext().getString(R.string.sys_no_internet));
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return movies;
    }
}
