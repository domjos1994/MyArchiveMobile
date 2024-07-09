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

package de.domjos.myarchivelibrary.custom;

import android.app.Activity;
import android.content.Intent;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import de.domjos.myarchivelibrary.tools.Receiver;

public abstract class ProgressBarTask<Params, Result> extends AbstractTask<Params, Integer, Result> {
    private final WeakReference<ProgressBar> progressBar;
    private final int icon;
    private final String title, content;
    private final boolean showNotifications;
    protected int max;

    public ProgressBarTask(Activity activity, int title, int content, boolean showNotifications, int icon, ProgressBar progressBar) {
        super(activity, title, content, showNotifications, icon, true);

        this.progressBar = new WeakReference<>(progressBar);
        this.title = activity.getString(title);
        this.content = activity.getString(content);
        this.icon = icon;
        this.showNotifications = showNotifications;
    }

    protected void onProgressUpdate(@NonNull Integer... values) {
        int percentage = (int) (values[0] / (this.max / 100.0));
        ((Activity) this.getContext()).runOnUiThread(()->this.progressBar.get().setProgress(percentage));

        Intent intent = new Intent(this.getContext(), Receiver.class);
        int id = -1;
        intent.putExtra("id", id);
        if(this.showNotifications) {
            super.notifications.showProgressNotification(id, this.title, this.content, this.icon, intent, 100, percentage);
        }
    }
}
