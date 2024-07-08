package de.domjos.myarchivedatabase.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.library.Library;
import de.domjos.myarchivedatabase.model.library.LibraryAlbumCrossRef;
import de.domjos.myarchivedatabase.model.library.LibraryBookCrossRef;
import de.domjos.myarchivedatabase.model.library.LibraryGameCrossRef;
import de.domjos.myarchivedatabase.model.library.LibraryMovieCrossRef;
import de.domjos.myarchivedatabase.model.library.LibraryPersonWithAlbums;
import de.domjos.myarchivedatabase.model.library.LibraryPersonWithBooks;
import de.domjos.myarchivedatabase.model.library.LibraryPersonWithGames;
import de.domjos.myarchivedatabase.model.library.LibraryPersonWithMovies;
import de.domjos.myarchivedatabase.model.library.LibraryWithAlbums;
import de.domjos.myarchivedatabase.model.library.LibraryWithBooks;
import de.domjos.myarchivedatabase.model.library.LibraryWithGames;
import de.domjos.myarchivedatabase.model.library.LibraryWithMovies;

@Dao
public interface LibraryDAO {
    @Query("SELECT * FROM library")
    List<Library> getAllLibraryObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    Library getLibraryObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<Library> getAllDeadLineNotReached(long date);

    @Query("SELECT * FROM library")
    List<LibraryWithAlbums> getAllLibraryAlbumObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryWithAlbums getLibraryAlbumObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryWithAlbums> getAllDeadLineNotReachedAlbum(long date);

    @Query("SELECT * FROM library")
    List<LibraryWithBooks> getAllLibraryBookObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryWithBooks getLibraryBookObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryWithBooks> getAllDeadLineNotReachedBook(long date);

    @Query("SELECT * FROM library")
    List<LibraryWithGames> getAllLibraryGameObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryWithGames getLibraryGameObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryWithGames> getAllDeadLineNotReachedGame(long date);

    @Query("SELECT * FROM library")
    List<LibraryWithMovies> getAllLibraryMovieObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryWithMovies getLibraryMovieObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryWithMovies> getAllDeadLineNotReachedMovie(long date);

    @Query("SELECT * FROM library")
    List<LibraryPersonWithAlbums> getAllLibraryAlbumPersonObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryPersonWithAlbums getLibraryAlbumPersonObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryPersonWithAlbums> getAllDeadLineNotReachedAlbumPerson(long date);

    @Query("SELECT * FROM library")
    List<LibraryPersonWithBooks> getAllLibraryBookPersonObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryPersonWithBooks getLibraryBookPersonObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryPersonWithBooks> getAllDeadLineNotReachedBookPerson(long date);

    @Query("SELECT * FROM library")
    List<LibraryPersonWithGames> getAllLibraryGamePersonObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryPersonWithGames getLibraryGamePersonObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryPersonWithGames> getAllDeadLineNotReachedGamePerson(long date);

    @Query("SELECT * FROM library")
    List<LibraryPersonWithMovies> getAllLibraryMoviePersonObjects();

    @Query("SELECT * FROM library WHERE id=:id")
    LibraryPersonWithMovies getLibraryMoviePersonObject(long id);

    @Query("SELECT * FROM library WHERE deadline >= :date")
    List<LibraryPersonWithMovies> getAllDeadLineNotReachedMoviePerson(long date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertLibrary(Library... library);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLibraryWithAlbums(LibraryAlbumCrossRef... libraryAlbumCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLibraryWithBooks(LibraryBookCrossRef... libraryBookCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLibraryWithGames(LibraryGameCrossRef... libraryBookCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLibraryWithMovies(LibraryMovieCrossRef... libraryBookCrossRefs);

    @Update
    void updateLibrary(Library... library);

    @Delete
    void deleteLibrary(Library... library);

    @Delete
    void deleteLibraryWithAlbums(LibraryAlbumCrossRef... libraryAlbumCrossRefs);

    @Delete
    void deleteLibraryWithBooks(LibraryBookCrossRef... libraryBookCrossRefs);

    @Delete
    void deleteLibraryWithGames(LibraryGameCrossRef... libraryGameCrossRefs);

    @Delete
    void deleteLibraryWithMovies(LibraryMovieCrossRef... libraryMovieCrossRefs);
}
