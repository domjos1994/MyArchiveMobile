package de.domjos.myarchivedatabase.converter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class BitmapConverter {

    @TypeConverter
    public static Bitmap toBitmap(byte[] data) {
        if(data == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @TypeConverter
    public static byte[] toByte(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, blob);
        return blob.toByteArray();
    }
}
