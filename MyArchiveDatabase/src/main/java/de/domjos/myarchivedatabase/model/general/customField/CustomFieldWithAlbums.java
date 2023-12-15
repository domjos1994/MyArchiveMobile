package de.domjos.myarchivedatabase.model.general.customField;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class CustomFieldWithAlbums {
    @Embedded
    private CustomField customField;

    @Relation(
            entity = Album.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CustomFieldAlbumCrossRef.class, parentColumn = "customFieldId", entityColumn = "albumId")
    )
    private List<Album> albums;

    private String value;

    public CustomFieldWithAlbums() {
        this.customField = null;
        this.albums = new LinkedList<>();
        this.value = "";
    }

    public CustomField getCustomField() {
        return this.customField;
    }

    public void setCustomField(CustomField customField) {
        this.customField = customField;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
