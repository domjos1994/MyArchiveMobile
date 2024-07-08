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

package de.domjos.myarchivelibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class IntentHelper {

    public static boolean startYoutubeIntent(String link, Activity activity) {
        boolean install = false;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(link));
        try {
            intent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
            activity.startActivity(intent);
            install = true;
        } catch (Exception ignored) {}
        return install;
    }

    public static void startBrowserIntent(String link, Activity activity) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        activity.startActivity(i);
    }
}
