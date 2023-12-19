package de.domjos.myarchivedatabase.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.filter.Filter;
import de.domjos.myarchivedatabase.model.filter.FilterWithTags;

@Dao
public interface FilterDAO {

    @Query("SELECT * FROM filters")
    List<Filter> getAllFilters();

    @Query("SELECT * FROM filters WHERE id=:id")
    Filter getFilterById(long id);

    @Query("SELECT * FROM filters WHERE title=:title")
    Filter getFilterByTitle(String title);

    @Query("SELECT * FROM filters WHERE id=:id")
    FilterWithTags getFilterWithTags(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertFilters(Filter... filters);

    @Update
    void updateFilters(Filter... filters);

    @Delete
    void deleteFilters(Filter... filters);
}
