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

package de.domjos.myarchivelibrary.tools;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import de.domjos.myarchivelibrary.R;

/** @noinspection unused*/
public class Notifications {
    private static NotificationChannel notificationChannel = null;

    private final NotificationManager notificationManager;
    private final String CHANNEL_ID = "UniTrackerMobile";
    private final Activity activity;
    public static final int CANCEL_CODE = 42;

    public Notifications(Activity activity) {
        this.activity = activity;
        this.notificationManager = activity.getSystemService(NotificationManager.class);


        if(Notifications.notificationChannel == null) {
            Notifications.notificationChannel = new NotificationChannel(
                    this.CHANNEL_ID,
                    this.CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(Notifications.notificationChannel);
        }
    }

    public void showNotification(int id, int title, int description, int icon) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this.activity, this.CHANNEL_ID);

        builder
                .setSmallIcon(icon)
                .setContentTitle(this.activity.getString(title))
                .setContentText(this.activity.getString(description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification =  builder.build();
        notificationManager.notify(id, notification);
    }

    public void showNotification(int id, String title, String description, int icon) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this.activity, this.CHANNEL_ID);

        builder
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification =  builder.build();
        notificationManager.notify(id, notification);
    }

    public static void printMessage(Activity activity, String message, int icon) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, activity.findViewById(R.id.custom_toast_container));
        TextView text = layout.findViewById(R.id.text);
        ImageView iv = layout.findViewById(R.id.ivIcon);
        iv.setImageDrawable(ActivityCompat.getDrawable(activity, icon));
        text.setText(message);
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static void printException(Activity activity, Exception ex, int icon) {
        Notifications.printMessage(activity, ex.getLocalizedMessage(), icon);
        Log.e("Error", ex.getLocalizedMessage() + "\n" + ex, ex);
    }

    public NotificationCompat.Builder showProgressNotification(int id, int title, int description, int icon, Intent cancelIntent, int max, int progress) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this.activity, this.CHANNEL_ID);

        if(cancelIntent != null) {
            cancelIntent.putExtra(EXTRA_NOTIFICATION_ID, id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.activity, CANCEL_CODE, cancelIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            builder.addAction(
                    R.drawable.icon_cancel,
                    activity.getString(R.string.sys_cancel),
                    pendingIntent
            );
        }

        boolean indeterminate = max == 0;
        builder
                .setSmallIcon(icon)
                .setContentTitle(this.activity.getString(title))
                .setContentText(this.activity.getString(description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(max, progress, indeterminate);
        Notification notification =  builder.build();
        notificationManager.notify(id, notification);
        return builder;
    }

    public NotificationCompat.Builder showProgressNotification(int id, String title, String description, int icon, Intent cancelIntent, int max, int progress) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this.activity, this.CHANNEL_ID);

        if(cancelIntent != null) {
            cancelIntent.putExtra(EXTRA_NOTIFICATION_ID, id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.activity, CANCEL_CODE, cancelIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            builder.addAction(
                    R.drawable.icon_cancel,
                    activity.getString(R.string.sys_cancel),
                    pendingIntent
            );
        }

        boolean indeterminate = max == 0;
        builder
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(max, progress, indeterminate);
        Notification notification =  builder.build();
        notificationManager.notify(id, notification);
        return builder;
    }

    public void cancelNotification(int id) {
        this.notificationManager.cancel(id);
    }
}

