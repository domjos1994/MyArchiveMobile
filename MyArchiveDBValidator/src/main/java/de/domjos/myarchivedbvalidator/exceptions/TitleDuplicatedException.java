package de.domjos.myarchivedbvalidator.exceptions;

import android.content.Context;

public class TitleDuplicatedException extends DuplicatedException{

    public TitleDuplicatedException(Context context, Object object) {
        super(context, "title", object);
    }
}
