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

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchiveservices.R;
import de.domjos.myarchiveservices.customTasks.CustomAbstractTask;
import de.domjos.myarchiveservices.services.EANDataWebservice;

public class EANDataMovieTask extends CustomAbstractTask<String, Void, List<Movie>> {
    private final String key;

    public EANDataMovieTask(Activity activity, boolean showNotifications, int icon, String key) {
        super(activity, R.string.service_ean_data_search, R.string.service_ean_data_search_content, showNotifications, icon);
        this.key = key;
    }

    @Override
    protected List<Movie> doInBackground(String string) {
        LinkedList<Movie> movies = new LinkedList<>();

        try {
            EANDataWebservice eanDataService = new EANDataWebservice(string, this.key, super.getContext());
            Movie movie = eanDataService.executeMovie();

            if(movie != null) {
                movie.setCode(string);
                movies.add(movie);
            }
        } catch (Exception ex) {
            super.printException(ex);
        }

        return movies;
    }
}
