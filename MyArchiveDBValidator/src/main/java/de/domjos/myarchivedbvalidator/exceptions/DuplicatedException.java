package de.domjos.myarchivedbvalidator.exceptions;

import android.content.Context;

import java.lang.reflect.Field;

import de.domjos.myarchivedbvalidator.R;

public class DuplicatedException extends ValidationException {
    private final String field;
    private final Object object;

    public DuplicatedException(Context context, String field, Object object) {
        super(context);

        this.field = field;
        this.object = object;
    }

    @Override
    protected int getKey() {
        return R.string.validation_duplicated;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();

        if(msg != null) {
            String key = this.field.substring(0, 1).toUpperCase() + this.field.substring(1);
            try {
                Class<?> cls = this.object.getClass();
                Field field = super.getField(this.field, cls);

                if(field == null) {
                    throw new Exception();
                }

                String value = (String) field.get(object);
                return String.format(msg, key, value);
            } catch (Exception ex) {
                return String.format(msg.replace("%2s ", ""), key);
            }
        }
        return "";
    }
}
