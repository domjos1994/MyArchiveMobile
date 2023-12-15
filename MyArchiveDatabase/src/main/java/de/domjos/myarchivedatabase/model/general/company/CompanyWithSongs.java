package de.domjos.myarchivedatabase.model.general.company;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;
import de.domjos.myarchivedatabase.model.media.song.Song;

public final class CompanyWithSongs {
    @Embedded
    private Company company;

    @Relation(
            entity = Song.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CompanySongCrossRef.class, parentColumn = "companyId", entityColumn = "songId")
    )
    private List<Song> songs;

    public CompanyWithSongs() {
        this.company = null;
        this.songs = new LinkedList<>();
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
