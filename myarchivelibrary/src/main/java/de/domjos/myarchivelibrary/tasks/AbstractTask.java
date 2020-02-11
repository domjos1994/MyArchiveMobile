/*
 * Copyright (C)  2019 Domjos
 * This file is part of UniTrackerMobile <https://github.com/domjos1994/UniTrackerMobile>.
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

package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import de.domjos.customwidgets.utils.MessageHelper;

public abstract class AbstractTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private WeakReference<Context> weakReference;
    private final int icon;
    private int id;
    private final String title, content;
    private boolean showNotifications;
    private PostExecuteListener postExecuteListener;

    AbstractTask(Activity activity, int title, int content, boolean showNotifications, int icon) {
        super();
        this.weakReference = new WeakReference<>(activity);
        this.icon = icon;
        this.title = activity.getString(title);
        this.content = activity.getString(content);
        this.showNotifications = showNotifications;
    }

    public AbstractTask(Activity activity, String title, int content, boolean showNotifications, int icon) {
        super();
        this.weakReference = new WeakReference<>(activity);
        this.icon = icon;
        this.title = title;
        this.content = activity.getString(content);
        this.showNotifications = showNotifications;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.showNotifications) {
            this.id = MessageHelper.startProgressNotification((Activity) this.getContext(), this.title, this.content, this.icon);
        }
        this.before();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (this.showNotifications) {
            MessageHelper.stopNotification((Activity) this.getContext(), this.id);
        }
        if (this.postExecuteListener != null) {
            this.postExecuteListener.onPostExecute(result);
        }
    }

    public void printException(Exception ex) {
        ((Activity) this.getContext()).runOnUiThread(() -> MessageHelper.printException(ex, this.icon, this.getContext()));
    }

    protected abstract void before();

    public void after(PostExecuteListener postExecuteListener) {
        this.postExecuteListener = postExecuteListener;
    }

    Object returnTemp(Object o) {
        if (o == null) {
            return null;
        } else {
            if (o.equals(0) || o.equals("")) {
                return null;
            } else {
                Object tmp;
                try {
                    tmp = Long.parseLong(String.valueOf(o));
                } catch (Exception ex) {
                    tmp = o;
                }
                return tmp;
            }
        }
    }


    protected Context getContext() {
        return this.weakReference.get();
    }

    public abstract static class PostExecuteListener<Result> {
        public abstract void onPostExecute(Result result);
    }
}
