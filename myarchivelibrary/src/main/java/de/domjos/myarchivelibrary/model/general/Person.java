package de.domjos.myarchivelibrary.model.general;

import java.util.Date;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseObject;

public final class Person extends BaseObject implements DatabaseObject {
    private String firstName;
    private String lastName;
    private Date birthDate;
    private byte[] image;
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
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTable() {
        return "persons";
    }
}
