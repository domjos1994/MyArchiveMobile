package de.domjos.myarchivedatabase.repository.fileTree;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.fileTree.FileTree;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeTagCrossRef;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeWithParent;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeWithTags;

@Dao
public interface FileTreeDAO {

    @Query("SELECT * FROM file_tree")
    List<FileTree> getAllFileTreeElements();

    @Query("SELECT * FROM file_tree where id=:id")
    FileTree getFileTreeElementById(long id);

    @Query("SELECT * FROM file_tree where parent=0")
    FileTree getRootFileTreeElement();

    @Transaction
    @Query("SELECT * FROM file_tree")
    List<FileTreeWithParent> getChildTreeElementsWithParents();

    @Transaction
    @Query("SELECT * FROM file_tree")
    List<FileTreeWithTags> getChildTreeElementsWithTags();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertFileTreeElements(FileTree... fileTrees);

    @Update
    void updateFileTreeElements(FileTree... fileTrees);

    @Delete
    void deleteFileTreeElements(FileTree... fileTrees);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFileTreeTagsElements(FileTreeTagCrossRef... fileTreeTagCrossRefs);

    @Delete
    void deleteFileTreeTagsElements(FileTreeTagCrossRef... fileTreeTagCrossRefs);
}
