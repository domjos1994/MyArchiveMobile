/*
 * Copyright (C)  2019-2024 Domjos
 * This file is part of UniTrackerMobile <https://unitrackermobile.de/>.
 *
 * UniTrackerMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UniTrackerMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UniTrackerMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivelibrary.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.lang.ref.WeakReference;

import de.domjos.myarchivelibrary.tools.Notifications;
import de.domjos.myarchivelibrary.tools.Receiver;

/** @noinspection rawtypes, unchecked , unchecked , unused */
public abstract class AbstractTask<Params, Progress, Result> extends AsyncTaskExecutorService<Params, Progress, Result> {
    private final WeakReference<Context> weakReference;
    private final int icon;
    private int id = -1;
    private final String title, content;
    private final boolean showNotifications;
    private PostExecuteListener postExecuteListener;
    private PreExecuteListener preExecuteListener;
    private final boolean progress;
    protected NotificationCompat.Builder builder;
    protected final Notifications notifications;

    public AbstractTask(Activity activity, int title, int content, boolean showNotifications, int icon) {
        super(activity);

        this.notifications = new Notifications(activity);
        this.weakReference = new WeakReference<>(activity);
        this.icon = icon;
        this.title = activity.getString(title);
        this.content = activity.getString(content);
        this.showNotifications = showNotifications;
        this.progress = false;
    }

    AbstractTask(Activity activity, int title, int content, boolean showNotifications, int icon, boolean progress) {
        super(activity);

        this.notifications = new Notifications(activity);
        this.weakReference = new WeakReference<>(activity);
        this.icon = icon;
        this.title = activity.getString(title);
        this.content = activity.getString(content);
        this.showNotifications = showNotifications;
        this.progress = progress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Intent intent = new Intent(this.getContext(), Receiver.class);
        intent.putExtra("id", this.id);
        if (this.showNotifications) {
            if(this.progress) {
                this.builder = this.notifications.showProgressNotification(this.id, this.title, this.content, this.icon, intent, 100, 0);
            } else {
                this.notifications.showNotification(this.id, this.title, this.content, this.icon);
            }
        }

        if(this.preExecuteListener != null) {
            this.preExecuteListener.onPreExecute();
        }
    }

    /** @noinspection unchecked*/
    @Override
    protected void onPostExecute(Result result) {
        if (this.showNotifications) {
            this.notifications.cancelNotification(this.id);
        }
        if (this.postExecuteListener != null) {
            this.postExecuteListener.onPostExecute(result);
        }
    }

    public void printMessage(String message) {
        ((Activity) this.getContext()).runOnUiThread(() -> Notifications.printMessage(super.refActivity.get(), message, this.icon));
    }

    public void printException(Exception ex) {
        ((Activity) this.getContext()).runOnUiThread(() -> Notifications.printException(super.refActivity.get(), ex, this.icon));
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void before(PreExecuteListener preExecuteListener) {
        this.preExecuteListener = preExecuteListener;
    }

    public void after(PostExecuteListener postExecuteListener) {
        this.postExecuteListener = postExecuteListener;
    }

    protected Context getContext() {
        return this.weakReference.get();
    }

    @FunctionalInterface
    public interface PostExecuteListener<Result> {
        void onPostExecute(Result result);
    }

    @FunctionalInterface
    public interface PreExecuteListener {
        void onPreExecute();
    }
}
