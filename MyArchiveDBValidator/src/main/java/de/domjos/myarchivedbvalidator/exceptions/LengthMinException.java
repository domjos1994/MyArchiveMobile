package de.domjos.myarchivedbvalidator.exceptions;

import android.content.Context;

import java.util.Objects;

import de.domjos.myarchivedbvalidator.R;

public class LengthMinException extends ValidationException {
    private final double min;

    public LengthMinException(Context context, double min) {
        super(context);

        this.min = min;
    }

    @Override
    protected int getKey() {
        return R.string.validation_min;
    }

    @Override
    public String getMessage() {
        return String.format(Objects.requireNonNull(super.getMessage()), this.min);
    }
}
