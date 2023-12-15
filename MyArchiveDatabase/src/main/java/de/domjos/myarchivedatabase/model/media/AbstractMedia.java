package de.domjos.myarchivedatabase.model.media;

import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;

import java.util.Date;

import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;

public class AbstractMedia extends BaseDescriptionObject {
    @ColumnInfo(name = "originalTitle")
    private String originalTitle;

    @ColumnInfo(name = "releaseDate")
    private Date releaseDate;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "category")
    private long category;

    @ColumnInfo(name = "cover")
    private Drawable cover;

    @ColumnInfo(name = "rating_web")
    private double ratingWeb;

    @ColumnInfo(name = "rating_own")
    private double ratingOwn;

    @ColumnInfo(name = "rating_note")
    private String note;

    public AbstractMedia() {
        super();
        this.originalTitle = "";
        this.releaseDate = null;
        this.code = "";
        this.price = 0.0;
        this.category = 0L;
        this.cover = null;
        this.ratingWeb = 0.0;
        this.ratingOwn = 0.0;
        this.note = "";
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public Drawable getCover() {
        return cover;
    }

    public void setCover(Drawable cover) {
        this.cover = cover;
    }

    public double getRatingWeb() {
        return this.ratingWeb;
    }

    public void setRatingWeb(double ratingWeb) {
        this.ratingWeb = ratingWeb;
    }

    public double getRatingOwn() {
        return this.ratingOwn;
    }

    public void setRatingOwn(double ratingOwn) {
        this.ratingOwn = ratingOwn;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
