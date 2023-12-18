package de.domjos.myarchivedbvalidator.validation;

public class ISBNValidator extends Validator {


    public ISBNValidator(String isbn) {
        super(isbn.replace("-", ""));
    }

    @Override
    public boolean validate() {
        boolean digits10;
        LengthValidator lengthValidator = new LengthValidator((String) super.object, 10, true);
        digits10 = lengthValidator.validate();
        if(!digits10) {
            lengthValidator = new LengthValidator((String) super.object, 13, true);
            if(!lengthValidator.validate()) {
                return false;
            }
        }

        int value = 0;
        String code = ((String) super.object).replace("-", "");
        if(digits10) {
            for(int i = 0; i<=code.length()-2; i++) {
                int digit = Integer.parseInt(code.substring(i, i+1).toLowerCase().replace("x", "10"));
                value = value + (digit * (10-i));
            }
            return ((11  - (value % 11)) % 11) == Integer.parseInt(code.substring(9, 10));
        } else {
            for(int i = 0; i<=code.length()-2; i++) {
                int digit = Integer.parseInt(code.substring(i, i+1).toLowerCase().replace("x", "10"));
                if(i % 2 == 0) {
                    value = value + digit;
                } else {
                    value = value + (3 * digit);
                }
            }
            return (10 - (value % 10)) == Integer.parseInt(code.substring(12, 13));
        }
    }
}
