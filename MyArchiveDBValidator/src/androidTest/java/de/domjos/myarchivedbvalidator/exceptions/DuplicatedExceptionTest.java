package de.domjos.myarchivedbvalidator.exceptions;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedbvalidator.R;

@RunWith(AndroidJUnit4.class)
public final class DuplicatedExceptionTest {
    private Context context;

    @Before
    public void getContext() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testDuplicatedExceptionWithKnownField() {
        try {
            Category category = new Category();
            category.setTitle("Test");

            throw new DuplicatedException(this.context, "title", category);
        } catch (Exception ex) {
            String msg = String.format(this.context.getString(R.string.validation_duplicated), "Title", "Test");
            Assert.assertEquals(DuplicatedException.class, ex.getClass());
            Assert.assertEquals(msg, ex.getMessage());
        }
    }

    @Test
    public void testDuplicatedExceptionWithUnknownField() {
        try {
            Category category = new Category();
            category.setTitle("Test");

            throw new DuplicatedException(this.context, "titled", category);
        } catch (Exception ex) {
            String msg = String.format(this.context.getString(R.string.validation_duplicated).replace("%2s ", ""), "Titled");
            Assert.assertEquals(DuplicatedException.class, ex.getClass());
            Assert.assertEquals(msg, ex.getMessage());
        }
    }

    @Test
    public void testTitleDuplicatedException() {
        Category category = new Category();
        category.setTitle("Test");

        try {
            throw new TitleDuplicatedException(this.context, category);
        } catch (Exception ex) {
            Exception tmp = new DuplicatedException(this.context, "title", category);
            Assert.assertEquals(tmp.getMessage(), ex.getMessage());
        }
    }
}
