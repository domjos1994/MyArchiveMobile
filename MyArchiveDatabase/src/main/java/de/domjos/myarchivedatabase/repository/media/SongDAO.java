package de.domjos.myarchivedatabase.repository.media;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.CompanySongCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonSongCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagSongCrossRef;
import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedatabase.model.media.song.SongWithAlbums;
import de.domjos.myarchivedatabase.model.media.song.SongWithCompanies;
import de.domjos.myarchivedatabase.model.media.song.SongWithPersons;
import de.domjos.myarchivedatabase.model.media.song.SongWithTags;

@Dao
public interface SongDAO {
    @Query("SELECT * FROM songs")
    List<Song> getAllSongs();

    @Query("SELECT * FROM songs WHERE id=:id")
    Song getSong(long id);

    @Query("SELECT * FROM songs WHERE title=:title")
    Song getSong(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertSongs(Song... songs);

    @Update
    void updateSongs(Song... songs);

    @Delete
    void deleteSongs(Song... songs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongWithPerson(PersonSongCrossRef... personSongCrossRefs);

    @Delete
    void deleteSongWithPerson(PersonSongCrossRef... personSongCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongWithCompany(CompanySongCrossRef... companySongCrossRefs);

    @Delete
    void deleteSongWithCompany(CompanySongCrossRef... companySongCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongWithTag(TagSongCrossRef... tagSongCrossRefs);

    @Delete
    void deleteSongWithTag(TagSongCrossRef... tagSongCrossRefs);

    @Transaction
    @Query("SELECT * FROM songs")
    List<SongWithCompanies> getAllSongsWithCompanies();

    @Transaction
    @Query("SELECT * FROM songs WHERE id=:id")
    SongWithCompanies getSongWithCompanies(long id);

    @Transaction
    @Query("SELECT * FROM songs")
    List<SongWithPersons> getAllSongsWithPersons();

    @Transaction
    @Query("SELECT * FROM songs WHERE id=:id")
    SongWithPersons getSongWithPersons(long id);

    @Transaction
    @Query("SELECT * FROM songs")
    List<SongWithAlbums> getAllSongsWithAlbums();

    @Transaction
    @Query("SELECT * FROM songs WHERE id=:id")
    SongWithAlbums getSongWithAlbums(long id);

    @Transaction
    @Query("SELECT * FROM songs")
    List<SongWithTags> getAllSongsWithTags();

    @Transaction
    @Query("SELECT * FROM songs WHERE id=:id")
    SongWithTags getSongWithTags(long id);
}
