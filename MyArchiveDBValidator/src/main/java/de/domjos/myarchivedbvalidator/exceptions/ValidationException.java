package de.domjos.myarchivedbvalidator.exceptions;

import android.content.Context;

import java.lang.reflect.Field;

public abstract class ValidationException extends Exception {
    protected final Context context;
    private String key;

    public ValidationException(Context context) {
        super();

        this.context = context;
    }

    protected abstract int getKey();

    @Override
    public String getMessage() {
        return this.context.getString(this.getKey());
    }

    protected Field getField(String key, Class<?> cls) {
        for(Field field : cls.getDeclaredFields()) {
            if(field.getName().equals(key)) {
                field.setAccessible(true);
                return field;
            }
        }
        if(cls.getSuperclass() != null) {
            return this.getField(key, cls.getSuperclass());
        }
        return null;
    }
}
