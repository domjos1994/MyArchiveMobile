package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.album.Album;

public final class CompanyWithAlbums {
    @Embedded
    private Company company;

    @Relation(
            entity = Album.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanyAlbumCrossRef.class, parentColumn = "companyId", entityColumn = "albumId")
    )
    private List<Album> albums;

    public CompanyWithAlbums() {
        this.company = null;
        this.albums = new LinkedList<>();
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
}
