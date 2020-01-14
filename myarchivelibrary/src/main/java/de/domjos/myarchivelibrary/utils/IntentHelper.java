package de.domjos.myarchivelibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class IntentHelper {
    private final static int GALLERY_CODE = 999;
    private final static int CAMERA_CODE = 998;

    public static void startScan(Activity activity) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
        scanIntegrator.initiateScan();
    }

    public static String getScanResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if(intentResult != null) {
                return intentResult.getContents();
            }
        }
        return "";
    }


    public static void startGalleryIntent(Activity activity) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(i, IntentHelper.GALLERY_CODE);
    }

    public static Bitmap getGalleryIntentResult(int requestCode, int resultCode, Intent intent, Activity activity) {
        if(resultCode == RESULT_OK && requestCode == IntentHelper.GALLERY_CODE) {
            Uri selectedImage = intent.getData();

            if(selectedImage != null) {
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = activity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if(cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    return BitmapFactory.decodeFile(picturePath);
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
