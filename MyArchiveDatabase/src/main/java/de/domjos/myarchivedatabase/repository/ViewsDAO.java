package de.domjos.myarchivedatabase.repository;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import de.domjos.myarchivedatabase.views.CustomFieldWithMediaAndValue;
import de.domjos.myarchivedatabase.views.Media;

@Dao
public interface ViewsDAO {

    @Query("SELECT * FROM CustomFieldWithMediaAndValue")
    List<CustomFieldWithMediaAndValue> getAllCustomFieldWithMediaAndValue();

    @Query("SELECT * FROM Media")
    List<Media> getAllMedia();
}
