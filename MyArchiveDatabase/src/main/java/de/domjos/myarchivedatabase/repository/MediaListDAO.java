package de.domjos.myarchivedatabase.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.mediaList.MediaList;
import de.domjos.myarchivedatabase.model.mediaList.MediaListAlbumCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListBookCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListGameCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListMovieCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithAlbums;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithBooks;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithGames;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithMovies;

@Dao
public interface MediaListDAO {
    @Query("SELECT * FROM mediaList")
    List<MediaList> getAllMediaListObjects();

    @Query("SELECT * FROM mediaList WHERE id=:id")
    MediaList getMediaListObject(long id);

    @Query("SELECT * FROM mediaList WHERE deadline >= :date")
    List<MediaList> getAllDeadLineNotReached(long date);

    @Query("SELECT * FROM mediaList")
    List<MediaListWithAlbums> getAllMediaListAlbumObjects();

    @Query("SELECT * FROM mediaList WHERE id=:id")
    MediaListWithAlbums getMediaListAlbumObject(long id);

    @Query("SELECT * FROM mediaList WHERE deadline >= :date")
    List<MediaListWithAlbums> getAllDeadLineNotReachedAlbum(long date);

    @Query("SELECT * FROM mediaList")
    List<MediaListWithBooks> getAllMediaListBookObjects();

    @Query("SELECT * FROM mediaList WHERE id=:id")
    MediaListWithBooks getMediaListBookObject(long id);

    @Query("SELECT * FROM mediaList WHERE deadline >= :date")
    List<MediaListWithBooks> getAllDeadLineNotReachedBook(long date);

    @Query("SELECT * FROM mediaList")
    List<MediaListWithGames> getAllMediaListGameObjects();

    @Query("SELECT * FROM mediaList WHERE id=:id")
    MediaListWithGames getMediaListGameObject(long id);

    @Query("SELECT * FROM mediaList WHERE deadline >= :date")
    List<MediaListWithGames> getAllDeadLineNotReachedGame(long date);

    @Query("SELECT * FROM mediaList")
    List<MediaListWithMovies> getAllMediaListMovieObjects();

    @Query("SELECT * FROM mediaList WHERE id=:id")
    MediaListWithMovies getMediaListMovieObject(long id);

    @Query("SELECT * FROM mediaList WHERE deadline >= :date")
    List<MediaListWithMovies> getAllDeadLineNotReachedMovie(long date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertMediaList(MediaList... mediaList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMediaListWithAlbums(MediaListAlbumCrossRef... mediaListAlbumCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMediaListWithBooks(MediaListBookCrossRef... mediaListBookCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMediaListWithGames(MediaListGameCrossRef... mediaListBookCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMediaListWithMovies(MediaListMovieCrossRef... mediaListBookCrossRefs);

    @Update
    void updateMediaList(MediaList... mediaList);

    @Delete
    void deleteMediaList(MediaList... mediaList);

    @Delete
    void deleteMediaListWithAlbums(MediaListAlbumCrossRef... mediaListAlbumCrossRefs);

    @Delete
    void deleteMediaListWithBooks(MediaListBookCrossRef... mediaListBookCrossRefs);

    @Delete
    void deleteMediaListWithGames(MediaListGameCrossRef... mediaListGameCrossRefs);

    @Delete
    void deleteMediaListWithMovies(MediaListMovieCrossRef... mediaListMovieCrossRefs);
}
