package de.domjos.myarchivedatabase.model.general.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class CategoryWithAlbums {
    @Embedded
    private Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "category"
    )
    private List<Album> albums;

    public CategoryWithAlbums() {
        this.category = null;
        this.albums = new LinkedList<>();
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
}
