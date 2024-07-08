package de.domjos.myarchivedatabase.model.media.movie;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldWithValues;

public final class MovieWithCustomFieldValues {
    @Embedded
    private Movie movie;

    @Relation(
            entity = CustomField.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CustomFieldValueMovieCrossRef.class, parentColumn = "movieId", entityColumn = "customFieldValueId")
    )
    private List<CustomFieldWithValues> customFieldWithValues;

    public MovieWithCustomFieldValues() {
        this.movie = null;
        this.customFieldWithValues = new LinkedList<>();
    }

    public Movie getMovie() {
        return this.movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<CustomFieldWithValues> getCustomFieldWithValues() {
        return this.customFieldWithValues;
    }

    public void setCustomFieldWithValues(List<CustomFieldWithValues> customFieldWithValues) {
        this.customFieldWithValues = customFieldWithValues;
    }
}
