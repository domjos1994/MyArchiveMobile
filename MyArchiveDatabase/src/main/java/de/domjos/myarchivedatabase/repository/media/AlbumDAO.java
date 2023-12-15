package de.domjos.myarchivedatabase.repository.media;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.CompanyAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagAlbumCrossRef;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithCompanies;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithPersons;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithSongs;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithTags;
import de.domjos.myarchivedatabase.model.media.song.SongAlbumCrossRef;

@Dao
public interface AlbumDAO {
    @Query("SELECT * FROM albums")
    List<Album> getAllAlbums();

    @Query("SELECT * FROM albums WHERE id=:id")
    Album getAlbum(long id);

    @Query("SELECT * FROM albums WHERE title=:title")
    Album getAlbum(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAlbums(Album... albums);

    @Update
    void updateAlbums(Album... albums);

    @Delete
    void deleteAlbums(Album... albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbumWithSong(SongAlbumCrossRef... songAlbumCrossRef);

    @Delete
    void deleteAlbumWithSong(SongAlbumCrossRef... songAlbumCrossRef);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbumWithPerson(PersonAlbumCrossRef... personAlbumCrossRefs);

    @Delete
    void deleteAlbumWithPerson(PersonAlbumCrossRef... personAlbumCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbumWithCompany(CompanyAlbumCrossRef... companyAlbumCrossRefs);

    @Delete
    void deleteAlbumWithCompany(CompanyAlbumCrossRef... companyAlbumCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbumWithTag(TagAlbumCrossRef... tagAlbumCrossRefs);

    @Delete
    void deleteAlbumWithTag(TagAlbumCrossRef... tagAlbumCrossRefs);

    @Transaction
    @Query("SELECT * FROM albums")
    List<AlbumWithCompanies> getAllAlbumsWithCompanies();

    @Transaction
    @Query("SELECT * FROM albums WHERE id=:id")
    AlbumWithCompanies getAlbumWithCompanies(long id);

    @Transaction
    @Query("SELECT * FROM albums")
    List<AlbumWithPersons> getAllAlbumsWithPersons();

    @Transaction
    @Query("SELECT * FROM albums WHERE id=:id")
    AlbumWithPersons getAlbumWithPersons(long id);

    @Transaction
    @Query("SELECT * FROM albums")
    List<AlbumWithSongs> getAllAlbumsWithSongs();

    @Transaction
    @Query("SELECT * FROM albums WHERE id=:id")
    AlbumWithSongs getAlbumWithSongs(long id);

    @Transaction
    @Query("SELECT * FROM albums")
    List<AlbumWithTags> getAllAlbumsWithTags();

    @Transaction
    @Query("SELECT * FROM albums WHERE id=:id")
    AlbumWithTags getAlbumWithTags(long id);
}
