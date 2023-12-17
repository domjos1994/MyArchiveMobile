package de.domjos.myarchivedatabase.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.customField.CustomField;

@Dao
public interface CustomFieldDAO {
    @Query("SELECT * FROM customFields")
    List<CustomField> getAllCustomFields();

    @Query("SELECT * FROM customFields WHERE id=:id")
    CustomField getCustomField(long id);

    @Query("SELECT * FROM customFields WHERE title=:title")
    CustomField getCustomField(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertCustomFields(CustomField... customFields);

    @Update
    void updateCustomFields(CustomField... customFields);

    @Delete
    void deleteCustomFields(CustomField... customFields);
}
