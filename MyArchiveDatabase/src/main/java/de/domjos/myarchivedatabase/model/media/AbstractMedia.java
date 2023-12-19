package de.domjos.myarchivedatabase.model.media;

import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.tag.Tag;

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

    @Ignore
    private Category categoryItem;

    @Ignore
    private List<Tag> tags;

    @Ignore
    private List<Person> persons;

    @Ignore
    private List<Company> companies;

    @Ignore
    private Map<CustomField, String> customFields;

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

        this.categoryItem = null;
        this.tags = new LinkedList<>();
        this.persons = new LinkedList<>();
        this.companies = new LinkedList<>();
        this.customFields = new LinkedHashMap<>();
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

    public Category getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(Category categoryItem) {
        this.categoryItem = categoryItem;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public Map<CustomField, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<CustomField, String> customFields) {
        this.customFields = customFields;
    }
}
