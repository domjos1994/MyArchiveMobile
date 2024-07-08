package de.domjos.myarchivedbvalidator.exceptions;

import android.content.Context;

import java.util.Objects;

import de.domjos.myarchivedbvalidator.R;

public class LengthMinMaxException extends ValidationException {
    private final double min;
    private final double max;

    public LengthMinMaxException(Context context, double min, double max) {
        super(context);

        this.min = min;
        this.max = max;
    }

    @Override
    protected int getKey() {
        return R.string.validation_min_max;
    }

    @Override
    public String getMessage() {
        return String.format(Objects.requireNonNull(super.getMessage()), this.min, this.max);
    }
}
