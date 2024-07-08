package de.domjos.myarchivedbvalidator.validation;

public class LengthValidator extends Validator {
    private final int min;
    private final int max;
    private final boolean exactly;

    public LengthValidator(String value, int max) {
        super(value);

        this.min = 0;
        this.max = max;
        this.exactly = false;
    }

    public LengthValidator(String value, int max, boolean exactly) {
        super(value);

        this.min = 0;
        this.max = max;
        this.exactly = exactly;
    }

    public LengthValidator(String value, int min, int max) {
        super(value);

        this.min = min;
        this.max = max;
        this.exactly = false;
    }

    public LengthValidator(Number value, int min, int max) {
        super(value);

        this.min = min;
        this.max = max;
        this.exactly = false;
    }

    @Override
    public boolean validate() {
        if(super.object instanceof String) {
            String item = ((String) super.object);
            if(!this.exactly) {
                return item.length() >= this.min && item.length() <= this.max;
            } else {
                return item.length() == this.max;
            }
        } else {
            if(super.object instanceof Number number) {
                return number.doubleValue() >= this.min && number.doubleValue() <= this.max;
            }
        }

        return false;
    }
}
