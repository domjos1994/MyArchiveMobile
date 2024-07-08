package de.domjos.myarchivedatabase.model.media.album;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValue;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldWithValues;

public final class AlbumWithCustomFieldValues {
    @Embedded
    private Album album;

    @Relation(
            entity = CustomField.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CustomFieldValueAlbumCrossRef.class, parentColumn = "albumId", entityColumn = "customFieldValueId")
    )
    private List<CustomFieldWithValues> customFieldWithValues;

    public AlbumWithCustomFieldValues() {
        this.album = null;
        this.customFieldWithValues = new LinkedList<>();
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<CustomFieldWithValues> getCustomFieldWithValues() {
        return this.customFieldWithValues;
    }

    public void setCustomFieldWithValues(List<CustomFieldWithValues> customFieldWithValues) {
        this.customFieldWithValues = customFieldWithValues;
    }
}
