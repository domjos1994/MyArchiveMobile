package de.domjos.myarchivelibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class IntentHelper {
    private final static int GALLERY_CODE = 999;
    private final static int CAMERA_CODE = 998;


    public static void startGalleryIntent(Activity activity) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(i, IntentHelper.GALLERY_CODE);
    }

    public static Bitmap getGalleryIntentResult(int requestCode, int resultCode, Intent intent, Activity activity) throws FileNotFoundException {
        if(resultCode == RESULT_OK && requestCode == IntentHelper.GALLERY_CODE) {
            Uri selectedImage = intent.getData();

            if(selectedImage != null) {
                ParcelFileDescriptor parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(selectedImage, "r");
                if(parcelFileDescriptor != null) {
                    return BitmapFactory.decodeStream(new FileInputStream(parcelFileDescriptor.getFileDescriptor()));
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
}
