package de.domjos.myarchivedatabase.model.general.person;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

import de.domjos.myarchivedatabase.model.base.BaseObject;

@Entity(tableName = "persons")
public final class Person extends BaseObject {
    @ColumnInfo(name = "firstName")
    private String firstName;

    @ColumnInfo(name = "lastName")
    private String lastName;

    @ColumnInfo(name = "birthDate")
    private Date birthDate;

    @ColumnInfo(name = "image")
    private Drawable image;

    @ColumnInfo(name = "description")
    private String description;

    public Person() {
        super();

        this.firstName = "";
        this.lastName = "";
        this.birthDate = null;
        this.image = null;
        this.description = "";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
