package de.domjos.myarchivedatabase.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagWithAlbums;
import de.domjos.myarchivedatabase.model.general.tag.TagWithBooks;
import de.domjos.myarchivedatabase.model.general.tag.TagWithGames;
import de.domjos.myarchivedatabase.model.general.tag.TagWithMovies;
import de.domjos.myarchivedatabase.model.general.tag.TagWithSongs;

@Dao
public interface TagDAO {
    @Query("SELECT * FROM tags")
    List<Tag> getAllTags();

    @Query("SELECT * FROM tags WHERE id=:id")
    Tag getTag(long id);

    @Query("SELECT * FROM tags WHERE title=:title")
    Tag getTag(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertTags(Tag... tags);

    @Update
    void updateTags(Tag... tags);

    @Delete
    void deleteTags(Tag... tags);

    @Transaction
    @Query("SELECT * FROM tags")
    List<TagWithAlbums> getAllTagsWithAlbums();

    @Transaction
    @Query("SELECT * FROM tags WHERE id=:id")
    TagWithAlbums getTagWithAlbums(long id);

    @Transaction
    @Query("SELECT * FROM tags")
    List<TagWithSongs> getAllTagsWithSongs();

    @Transaction
    @Query("SELECT * FROM tags WHERE id=:id")
    TagWithSongs getTagWithSongs(long id);

    @Transaction
    @Query("SELECT * FROM tags")
    List<TagWithMovies> getAllTagsWithMovies();

    @Transaction
    @Query("SELECT * FROM tags WHERE id=:id")
    TagWithMovies getTagWithMovies(long id);

    @Transaction
    @Query("SELECT * FROM tags")
    List<TagWithBooks> getAllTagsWithBooks();

    @Transaction
    @Query("SELECT * FROM tags WHERE id=:id")
    TagWithBooks getTagWithBooks(long id);

    @Transaction
    @Query("SELECT * FROM tags")
    List<TagWithGames> getAllTagsWithGames();

    @Transaction
    @Query("SELECT * FROM tags WHERE id=:id")
    TagWithGames getTagWithGames(long id);
}
