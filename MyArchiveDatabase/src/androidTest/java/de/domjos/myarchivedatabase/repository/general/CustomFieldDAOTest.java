package de.domjos.myarchivedatabase.repository.general;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.repository.media.AlbumDAO;
import de.domjos.myarchivedatabase.repository.media.BookDAO;
import de.domjos.myarchivedatabase.repository.media.MovieDAO;

@RunWith(AndroidJUnit4.class)

public class CustomFieldDAOTest {
    private CustomFieldDAO customFieldDAO;
    private BookDAO bookDAO;
    private MovieDAO movieDAO;
    private AlbumDAO albumDAO;
    private AppDatabase appDatabase;
    private final String test = "Test";
    private CustomField customField;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.customFieldDAO = this.appDatabase.customFieldDAO();

        this.customField = new CustomField();
        this.customField.setTitle(this.test + "2");
        this.customFieldDAO.insertCustomFields(this.customField);
    }

    @After
    public void closeDb() {
        this.customFieldDAO.deleteCustomFields(this.customField);
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {

        // insert customField
        CustomField customField = new CustomField();
        customField.setTitle(this.test);
        long id = this.customFieldDAO.insertCustomFields(customField)[0];

        // get customField
        List<CustomField> customFields = this.customFieldDAO.getAllCustomFields();
        Assert.assertEquals(customFields.size(), 2);
        customField = this.customFieldDAO.getCustomField(this.test);
        Assert.assertEquals(customField.getTitle(), this.test);
        Assert.assertEquals(id, customField.getId());

        // delete customField
        this.customFieldDAO.deleteCustomFields(customField);
        customFields = this.customFieldDAO.getAllCustomFields();
        Assert.assertEquals(customFields.size(), 1);
    }

    @Test
    public void testInsertAndDeleteCustomFieldAlbum() {

    }
}
