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

package de.domjos.myarchivemobile.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class LibraryService extends JobService {
    private Database database;
    private Context context;
    private List<Integer> notifications;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.context = this.getApplicationContext();
        try {
            this.database = new Database(this.context);
            this.notifications = new LinkedList<>();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }

        try {
            for(Book object : this.database.getBooks("", MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("library"))) {
                this.checkLibraryObject(object);
            }
            for(Movie object : this.database.getMovies("", MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("library"))) {
                this.checkLibraryObject(object);
            }
            for(Album object : this.database.getAlbums("", MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("library"))) {
                this.checkLibraryObject(object);
            }
            for(Game object : this.database.getGames("", MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("library"))) {
                this.checkLibraryObject(object);
            }
        } catch (Exception ignored) {} finally {
            this.database.close();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        for(Integer id : this.notifications) {
            MessageHelper.stopNotification(this.getApplicationContext(), id);
        }
        this.database.close();
        return true;
    }

    private void checkLibraryObject(BaseMediaObject mediaObject) {
        String message = this.context.getString(R.string.library_msg);
        boolean lendOut = false;
        Calendar lendOutDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        for(LibraryObject libraryObject : mediaObject.getLibraryObjects()) {
            if(libraryObject.getReturned() == null) {
                lendOutDate.setTime(libraryObject.getDeadLine());
                lendOutDate.add(Calendar.DAY_OF_YEAR, libraryObject.getNumberOfDays());
                lendOutDate.add(Calendar.DAY_OF_YEAR, libraryObject.getNumberOfWeeks() * 7);
                message = String.format(message, mediaObject.getTitle(), (libraryObject.getPerson().getFirstName() + " " + libraryObject.getPerson().getLastName()).trim());
                lendOut = true;
                break;
            }
        }
        if(!lendOut) {
            return;
        }
        if(lendOutDate.get(Calendar.YEAR)==today.get(Calendar.YEAR) && lendOutDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            this.notifications.add(MessageHelper.showNotification(this.context, message, message, R.drawable.icon_library));
        }
    }
}
