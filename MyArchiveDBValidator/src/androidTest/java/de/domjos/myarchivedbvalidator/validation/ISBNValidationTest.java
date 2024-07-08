package de.domjos.myarchivedbvalidator.validation;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ISBNValidationTest {

    @Test
    public void testISBN10Validation() {
        ISBNValidator isbnValidator = new ISBNValidator("0306406152");
        Assert.assertTrue(isbnValidator.validate());

        isbnValidator = new ISBNValidator("9999999998");
        Assert.assertFalse(isbnValidator.validate());
    }

    @Test
    public void testISBN13Validation() {
        ISBNValidator isbnValidator = new ISBNValidator("9780306406157");
        Assert.assertTrue(isbnValidator.validate());

        isbnValidator = new ISBNValidator("9999999999998");
        Assert.assertFalse(isbnValidator.validate());
    }
}
