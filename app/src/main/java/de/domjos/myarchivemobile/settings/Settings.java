package de.domjos.myarchivemobile.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.domjos.customwidgets.utils.Crypto;
import de.domjos.myarchivemobile.R;

public final class Settings {
    private SharedPreferences sharedPreferences;
    private SharedPreferences userPreferences;
    private Crypto crypto;

    // keys
    public final static String DB_PASSWORD = "DB_PASSWORD";

    public Settings(Context context) throws InvalidKeySpecException, NoSuchAlgorithmException {

        this.sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        this.userPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.crypto = new Crypto(context, context.getString(R.string.sys_password));
    }


    public <T> T getSetting(String key, T defVal) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return this.getSetting(key, defVal, false);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(String key, T defVal, boolean encrypted) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        if(defVal instanceof String) {
            if(encrypted) {
                String returnValue = this.sharedPreferences.getString(key, (String) defVal);
                if(returnValue.equals(defVal)) {
                    return (T) returnValue;
                } else {
                    return (T) this.crypto.decryptString(returnValue);
                }
            } else {
                return (T) this.sharedPreferences.getString(key, (String) defVal);
            }
        }
        if(defVal instanceof Float) {
            return (T) (Float) this.sharedPreferences.getFloat(key, (Float) defVal);
        }
        if(defVal instanceof Integer) {
            return (T) (Integer) this.sharedPreferences.getInt(key, (Integer) defVal);
        }
        if(defVal instanceof Boolean) {
            return (T) (Boolean) this.sharedPreferences.getBoolean(key, (Boolean) defVal);
        }
        if(defVal instanceof Long) {
            return (T) (Long) this.sharedPreferences.getLong(key, (Long) defVal);
        }

        return null;
    }

    public <T> void setSetting(String key, T object) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        this.setSetting(key, object, false);
    }

    public <T> void setSetting(String key, T object, boolean encrypted) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        if(object instanceof String) {
            if(encrypted) {
                editor.putString(key, this.crypto.encryptString(((String) object)));
            } else {
                editor.putString(key, (String) object);
            }
        }
        if(object instanceof Float) {
            editor.putFloat(key, (Float) object);
        }
        if(object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        }
        if(object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        }
        if(object instanceof Long) {
            editor.putLong(key, (Long) object);
        }
        editor.apply();
    }

    public boolean isNotifications() {
        return this.userPreferences.getBoolean("swtNotifications", false);
    }

    public boolean isDebugMode() {
        return this.userPreferences.getBoolean("swtDebugMode", false);
    }

    public String getEANDataKey() {
        return this.userPreferences.getString("txtEANDataKey", "");
    }

    public String getGoogleBooksKey() {
        return this.userPreferences.getString("txtGoogleBooksKey", "");
    }

    public String getMovieDBKey() {
        return this.userPreferences.getString("txtMovieDBKey", "");
    }
}
