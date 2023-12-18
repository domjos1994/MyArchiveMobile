package de.domjos.myarchivedbvalidator.validation;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LengthValidationTest {

    @Test
    public void testStringLengthValidation() {
        LengthValidator lengthValidator = new LengthValidator("Test", 4);
        Assert.assertTrue(lengthValidator.validate());
        lengthValidator = new LengthValidator("Test", 3);
        Assert.assertFalse(lengthValidator.validate());
    }

    @Test
    public void testStringLengthExactlyValidation() {
        LengthValidator lengthValidator = new LengthValidator("Test", 4, true);
        Assert.assertTrue(lengthValidator.validate());
        lengthValidator = new LengthValidator("Test", 5, true);
        Assert.assertFalse(lengthValidator.validate());
    }

    @Test
    public void testNumberLengthValidation() {
        LengthValidator lengthValidator = new LengthValidator(42, 0, 100);
        Assert.assertTrue(lengthValidator.validate());
        lengthValidator = new LengthValidator(42, 45, 100);
        Assert.assertFalse(lengthValidator.validate());
        lengthValidator = new LengthValidator(42, 2, 40);
        Assert.assertFalse(lengthValidator.validate());
    }
}
