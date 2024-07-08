package de.domjos.myarchivedatabase.model.library;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

import de.domjos.myarchivedatabase.model.base.BaseObject;

@Entity(tableName = "library")
public final class Library extends BaseObject {
    @ColumnInfo(name = "person")
    private long person;
    @ColumnInfo(name = "numberOfDays")
    private int numberOfDays;

    @ColumnInfo(name = "numberOfWeeks")
    private int numberOfWeeks;

    @ColumnInfo(name = "deadline")
    private Date deadline;

    @ColumnInfo(name = "returnedAt")
    private Date returnedAt;

    public Library() {
        super();

        this.person = 0;
        this.numberOfDays = 0;
        this.numberOfWeeks = 0;
        this.deadline = null;
        this.returnedAt = null;
    }

    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public int getNumberOfWeeks() {
        return this.numberOfWeeks;
    }

    public void setNumberOfWeeks(int numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public Date getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getReturnedAt() {
        return this.returnedAt;
    }

    public void setReturnedAt(Date returnedAt) {
        this.returnedAt = returnedAt;
    }

    public long getPerson() {
        return this.person;
    }

    public void setPerson(long person) {
        this.person = person;
    }
}
