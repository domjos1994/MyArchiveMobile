package de.domjos.myarchivedatabase.model.general.company;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;

@Entity(tableName = "companies")
public final class Company extends BaseDescriptionObject {
    @ColumnInfo(name = "foundation")
    private Date foundation;

    @ColumnInfo(name = "cover")
    private Drawable cover;

    public Company() {
        super();

        this.foundation = null;
        this.cover = null;
    }

    public Date getFoundation() {
        return this.foundation;
    }

    public void setFoundation(Date foundation) {
        this.foundation = foundation;
    }

    public Drawable getCover() {
        return this.cover;
    }

    public void setCover(Drawable cover) {
        this.cover = cover;
    }
}
