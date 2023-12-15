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
import de.domjos.myarchivedatabase.model.general.tag.Tag;

@RunWith(AndroidJUnit4.class)

public class TagDAOTest {
    private TagDAO tagDAO;
    private AppDatabase appDatabase;
    private final String title = "Test-Category";

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.tagDAO = this.appDatabase.tagDAO();
    }

    @After
    public void closeDb() {
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {

        // insert category
        Tag tag = new Tag();
        tag.setTitle(this.title);
        long id = this.tagDAO.insertTags(tag)[0];

        // get category
        List<Tag> tags = this.tagDAO.getAllTags();
        Assert.assertEquals(tags.size(), 1);
        tag = this.tagDAO.getTag(this.title);
        Assert.assertEquals(tag.getTitle(), this.title);
        Assert.assertEquals(id, tag.getId());

        // delete category
        this.tagDAO.deleteTags(tag);
        tags = this.tagDAO.getAllTags();
        Assert.assertEquals(tags.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert category
        Tag tag = new Tag();
        tag.setTitle(this.title);
        this.tagDAO.insertTags(tag);

        // get category
        List<Tag> tags = this.tagDAO.getAllTags();
        Assert.assertEquals(tags.size(), 1);
        tag = this.tagDAO.getTag(this.title);
        Assert.assertEquals(tag.getTitle(), this.title);

        // update category
        String newTest = "Test2";
        tag.setTitle(newTest);
        this.tagDAO.updateTags(tag);
        tag = this.tagDAO.getTag(tag.getId());
        Assert.assertEquals(tag.getTitle(), newTest);

        // delete category
        this.tagDAO.deleteTags(tag);
        tags = this.tagDAO.getAllTags();
        Assert.assertEquals(tags.size(), 0);
    }

}
