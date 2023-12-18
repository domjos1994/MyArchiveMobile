package de.domjos.myarchivedbvalidator.validation;

public abstract class Validator {
    protected Object object;

    public Validator(Object object) {
        this.object = object;
    }

    public abstract boolean validate();
}
