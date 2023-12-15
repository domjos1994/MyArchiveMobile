package de.domjos.myarchivedatabase.repository.general;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

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
import de.domjos.myarchivedatabase.R;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.general.person.Person;

@RunWith(AndroidJUnit4.class)

public class PersonDAOTest {
    private Context context;
    private PersonDAO personDAO;
    private AppDatabase appDatabase;
    private final String firstName = "John";
    private final String lastName = "Doe";

    @Before
    public void createDB() {
        this.context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(this.context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.personDAO = this.appDatabase.personDAO();
    }

    @After
    public void closeDb() {
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {

        // insert person
        Person person = new Person();
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        long id = this.personDAO.insertPersons(person)[0];

        // get person
        List<Person> persons = this.personDAO.getAllPersons();
        Assert.assertEquals(persons.size(), 1);
        person = this.personDAO.getPerson(this.firstName, this.lastName);
        Assert.assertEquals(person.getFirstName(), this.firstName);
        Assert.assertEquals(person.getLastName(), this.lastName);
        Assert.assertEquals(id, person.getId());

        // delete person
        this.personDAO.deletePersons(person);
        persons = this.personDAO.getAllPersons();
        Assert.assertEquals(persons.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert person
        Person person = new Person();
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        long id = this.personDAO.insertPersons(person)[0];

        // get person
        List<Person> persons = this.personDAO.getAllPersons();
        Assert.assertEquals(persons.size(), 1);
        person = this.personDAO.getPerson(this.firstName, this.lastName);
        Assert.assertEquals(person.getFirstName(), this.firstName);
        Assert.assertEquals(person.getLastName(), this.lastName);
        Assert.assertEquals(id, person.getId());

        // update person
        String newTest = "Test2";
        person.setFirstName(newTest);
        this.personDAO.updatePersons(person);
        person = this.personDAO.getPerson(person.getId());
        Assert.assertEquals(person.getFirstName(), newTest);

        // delete person
        this.personDAO.deletePersons(person);
        persons = this.personDAO.getAllPersons();
        Assert.assertEquals(persons.size(), 0);
    }

    @Test
    public void testBitmapConverter() {
        Drawable drawable = this.context.getDrawable(R.drawable.ic_android_black_24dp);

        Person person = new Person();
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        person.setImage(drawable);
        long id = this.personDAO.insertPersons(person)[0];

        Person tmp = this.personDAO.getPerson(id);
        Assert.assertNotNull(drawable);
        Assert.assertEquals(drawable.getIntrinsicHeight(), tmp.getImage().getIntrinsicHeight());
        Assert.assertEquals(drawable.getIntrinsicWidth(), tmp.getImage().getIntrinsicWidth());
    }
}
