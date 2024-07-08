package de.domjos.myarchivedatabase.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.domjos.myarchivedatabase.model.filter.FilterWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValue;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueBookCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueFilterCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueGameCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldWithValues;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.media.book.BookWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.media.game.GameWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithCustomFieldValues;

@Dao
public interface CustomFieldValueDAO {
    @Query("SELECT * FROM customFieldValues")
    List<CustomFieldValue> getAllCustomFieldValues();

    @Query("SELECT * FROM customFieldValues WHERE customField=:id")
    List<CustomFieldValue> getCustomFieldValues(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertCustomFieldValues(CustomFieldValue... customFieldValues);

    @Delete
    void deleteCustomFieldValues(CustomFieldValue... customFieldValues);

    @Query("SELECT * FROM customFieldValues")
    List<CustomFieldWithValues> getAllCustomFieldsWithValues();

    @Query("SELECT * FROM customFieldValues WHERE customField=:id")
    List<CustomFieldWithValues> getCustomFieldsWithValues(long id);

    @Query("SELECT * FROM customFieldValues WHERE customField=:id")
    List<AlbumWithCustomFieldValues> getAlbumWithCustomFieldValues(long id);

    @Query("SELECT * FROM customFieldValues WHERE customField=:id")
    List<BookWithCustomFieldValues> getBookWithCustomFieldValues(long id);

    @Query("SELECT * FROM customFieldValues WHERE customField=:id")
    List<GameWithCustomFieldValues> getGameWithCustomFieldValues(long id);

    @Query("SELECT * FROM customFieldValues WHERE customField=:id")
    List<MovieWithCustomFieldValues> getMovieWithCustomFieldValues(long id);

    @Query("SELECT * FROM customFieldValues WHERE customField=:id")
    List<FilterWithCustomFieldValues> getFilterWithCustomFieldValues(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomFieldBooks(CustomFieldValueBookCrossRef... customFieldValueBookCrossRefs);

    @Delete
    void deleteCustomFieldBooks(CustomFieldValueBookCrossRef... customFieldValueBookCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomFieldAlbums(CustomFieldValueAlbumCrossRef... customFieldValueAlbumCrossRefs);

    @Delete
    void deleteCustomFieldAlbums(CustomFieldValueAlbumCrossRef... customFieldValueAlbumCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomFieldGames(CustomFieldValueGameCrossRef... customFieldValueGameCrossRefs);

    @Delete
    void deleteCustomFieldGames(CustomFieldValueGameCrossRef... customFieldValueGameCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomFieldMovies(CustomFieldValueMovieCrossRef... customFieldValueMovieCrossRefs);

    @Delete
    void deleteCustomFieldMovies(CustomFieldValueMovieCrossRef... customFieldValueMovieCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomFieldFilters(CustomFieldValueFilterCrossRef... customFieldValueFilterCrossRefs);

    @Delete
    void deleteCustomFieldFilters(CustomFieldValueFilterCrossRef... customFieldValueFilterCrossRefs);
}
