package de.domjos.myarchivedatabase.repository.fileTree;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.fileTree.FileTreeFile;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileTagCrossRef;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileWithParent;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileWithTags;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeWithTags;

@Dao
public interface FileTreeFileDAO {

    @Query("SELECT * FROM file_tree_file")
    List<FileTreeFile> getAllFileTreeFileElements();

    @Query("SELECT * FROM file_tree_file where id=:id")
    FileTreeFile getFileTreeFileElementById(long id);

    @Transaction
    @Query("SELECT * FROM file_tree_file")
    List<FileTreeFileWithParent> getChildTreeFileElementsWithParents();

    @Transaction
    @Query("SELECT * FROM file_tree_file")
    List<FileTreeFileWithTags> getChildTreeFileElementsWithTags();

    @Transaction
    @Query("SELECT * FROM file_tree_file WHERE id=:id")
    FileTreeFileWithTags getChildTreeFileElementWithTags(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertFileTreeFileElements(FileTreeFile... fileTreeFiles);

    @Update
    void updateFileTreeFileElements(FileTreeFile... fileTreeFiles);

    @Delete
    void deleteFileTreeFileElements(FileTreeFile... fileTreeFiles);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFileTreeFileTagsElements(FileTreeFileTagCrossRef... fileTreeFileTagCrossRefs);

    @Delete
    void deleteFileTreeFileTagsElements(FileTreeFileTagCrossRef... fileTreeFileTagCrossRefs);
}
