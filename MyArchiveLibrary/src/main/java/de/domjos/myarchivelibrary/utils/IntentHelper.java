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
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class IntentHelper {
    private final static int GALLERY_CODE = 999;
    private final static int CAMERA_CODE = 998;


    public static void startGalleryIntent(Activity activity) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(i, IntentHelper.GALLERY_CODE);
    }

    public static Bitmap getGalleryIntentResult(int requestCode, int resultCode, Intent intent, Activity activity) throws IOException {
        if(resultCode == RESULT_OK && requestCode == IntentHelper.GALLERY_CODE) {
            Uri selectedImage = intent.getData();

            if(selectedImage != null) {
                ContentResolver resolver = activity.getContentResolver();
                try (ParcelFileDescriptor parcelFileDescriptor = resolver.openFileDescriptor(selectedImage, "r")) {
                    if(parcelFileDescriptor != null) {
                        return BitmapFactory.decodeStream(new FileInputStream(parcelFileDescriptor.getFileDescriptor()));
                    }
                }
            }
        }
        return null;
    }


    public static void startCameraIntent(Activity activity) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(cameraIntent, IntentHelper.CAMERA_CODE);
    }

    public static Bitmap getCameraIntentResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK && requestCode == IntentHelper.CAMERA_CODE) {
            return (Bitmap) Objects.requireNonNull(intent.getExtras()).get("data");
        }
        return null;
    }

    public static void startPDFIntent(Activity activity, String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");
        activity.startActivity(intent);
    }

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
