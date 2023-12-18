package de.domjos.myarchivedbvalidator.model;

import android.graphics.drawable.Drawable;

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

public abstract class MediaObject extends BaseDescriptionObject {
    private String originalTitle;
    private Date releaseDate;
    private String code;
    private double price;
    private Category category;
    private Drawable cover;
    private double ratingWeb;
    private double ratingOwn;
    private String note;
    private List<Tag> tags;
    private List<Person> persons;
    private List<Company> companies;
    private Map<CustomField, String> customFields;

    public MediaObject() {
        this.originalTitle = "";
        this.releaseDate = new Date();
        this.code = "";
        this.price = 0.0;
        this.category = null;
        this.cover = null;
        this.ratingWeb = 0.0;
        this.ratingOwn = 0.0;
        this.note = "";
        this.tags = new LinkedList<>();
        this.persons = new LinkedList<>();
        this.companies = new LinkedList<>();
        this.customFields = new LinkedHashMap<>();
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Drawable getCover() {
        return this.cover;
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
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public Map<CustomField, String> getCustomFields() {
        return this.customFields;
    }

    public void setCustomFields(Map<CustomField, String> customFields) {
        this.customFields = customFields;
    }
}
