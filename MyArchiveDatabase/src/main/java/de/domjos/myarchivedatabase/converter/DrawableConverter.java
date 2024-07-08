package de.domjos.myarchivedatabase.converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@ProvidedTypeConverter
public class DrawableConverter {
    private final Context context;

    public DrawableConverter(Context context) {
        this.context = context;
    }

    @TypeConverter
    public byte[] toByte(Drawable drawable) {
        Bitmap bitmap;

        if(drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable bitmapDrawable) {
            if(bitmapDrawable.getBitmap() != null) {
                return BitmapConverter.toByte(bitmapDrawable.getBitmap());
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapConverter.toByte(bitmap);
    }

    @TypeConverter
    public Drawable toDrawable(byte[] data) {
        if(data == null) {
            return null;
        }
        ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(data);
        BitmapDrawable drawable = new BitmapDrawable(this.context.getResources(), byteArrayOutputStream);
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return drawable;
    }
}
