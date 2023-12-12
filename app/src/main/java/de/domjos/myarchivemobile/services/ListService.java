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
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class ListService extends JobService {
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
            for(MediaList mediaList : this.database.getMediaLists("", MainActivity.GLOBALS.getSettings(this.context).getMediaCount(), MainActivity.GLOBALS.getOffset("list"))) {
                this.checkListObject(mediaList);
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

    private void checkListObject(MediaList mediaList) {
        String message = this.context.getString(R.string.lists_msg);
        boolean hasDeadLine = false;
        Calendar deadLineDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        if(mediaList.getDeadLine() != null) {
            deadLineDate.setTime(mediaList.getDeadLine());
            message = String.format(message, mediaList.getTitle());
            hasDeadLine = true;
        }
        if(!hasDeadLine) {
            return;
        }
        if(deadLineDate.get(Calendar.YEAR)==today.get(Calendar.YEAR) && deadLineDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            this.notifications.add(MessageHelper.showNotification(this.context, message, message, R.drawable.icon_library));
        }
    }
}
