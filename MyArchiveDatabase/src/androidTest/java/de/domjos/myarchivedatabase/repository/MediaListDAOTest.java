package de.domjos.myarchivedatabase.repository;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.mediaList.MediaList;
import de.domjos.myarchivedatabase.model.mediaList.MediaListAlbumCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListBookCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListGameCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListMovieCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithAlbums;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithBooks;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithGames;
import de.domjos.myarchivedatabase.model.mediaList.MediaListWithMovies;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.repository.general.PersonDAO;
import de.domjos.myarchivedatabase.repository.media.AlbumDAO;
import de.domjos.myarchivedatabase.repository.media.BookDAO;
import de.domjos.myarchivedatabase.repository.media.GameDAO;
import de.domjos.myarchivedatabase.repository.media.MovieDAO;

@RunWith(AndroidJUnit4.class)
public class MediaListDAOTest {
    private MediaListDAO mediaListDAO;
    private AlbumDAO albumDAO;
    private BookDAO bookDAO;
    private GameDAO gameDAO;
    private MovieDAO movieDAO;
    private PersonDAO personDAO;
    private AppDatabase appDatabase;

    private Person person;
    private Album album;
    private Book book;
    private Game game;
    private Movie movie;
    private MediaList mediaList;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.mediaListDAO = this.appDatabase.mediaListDAO();
        this.albumDAO = this.appDatabase.albumDAO();
        this.bookDAO = this.appDatabase.bookDAO();
        this.gameDAO = this.appDatabase.gameDAO();
        this.movieDAO = this.appDatabase.movieDAO();
        this.personDAO = this.appDatabase.personDAO();

        this.person = new Person();
        this.person.setFirstName("John");
        this.person.setLastName("Doe");
        this.person.setId(this.personDAO.insertPersons(this.person)[0]);

        this.album = new Album();
        this.album.setTitle("Test");
        this.album.setId(this.albumDAO.insertAlbums(this.album)[0]);

        this.book = new Book();
        this.book.setTitle("Test");
        this.book.setId(this.bookDAO.insertBooks(this.book)[0]);

        this.game = new Game();
        this.game.setTitle("Test");
        this.game.setId(this.gameDAO.insertGames(this.game)[0]);

        this.movie = new Movie();
        this.movie.setTitle("Test");
        this.movie.setId(this.movieDAO.insertMovies(this.movie)[0]);

        this.mediaList = new MediaList();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2023);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        this.mediaList.setDeadline(cal.getTime());
        this.mediaList.setId(this.mediaListDAO.insertMediaList(this.mediaList)[0]);
    }

    @After
    public void closeDb() {
        this.mediaListDAO.deleteMediaList(this.mediaList);
        this.personDAO.deletePersons(this.person);
        this.albumDAO.deleteAlbums(this.album);
        this.bookDAO.deleteBooks(this.book);
        this.gameDAO.deleteGames(this.game);
        this.movieDAO.deleteMovies(this.movie);
        this.appDatabase.close();
    }

    @Test
    public void testMediaListCreation() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2023);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);


        // create mediaList
        MediaList mediaList = new MediaList();
        mediaList.setDeadline(cal.getTime());
        mediaList.setId(this.mediaListDAO.insertMediaList(mediaList)[0]);

        // get mediaList
        MediaList tmp = this.mediaListDAO.getMediaListObject(mediaList.getId());
        Assert.assertEquals(tmp.getDeadline(), mediaList.getDeadline());

        // update mediaList
        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        tmp.setDeadline(cal.getTime());
        this.mediaListDAO.updateMediaList(tmp);
        List<MediaList> libraries = this.mediaListDAO.getAllDeadLineNotReached(new Date().getTime());
        Assert.assertEquals(libraries.size(), 2);
        Assert.assertEquals(libraries.get(1).getDeadline(), tmp.getDeadline());

        // delete object
        this.mediaListDAO.deleteMediaList(tmp);

        List<MediaList> allLibraries = this.mediaListDAO.getAllMediaListObjects();
        Assert.assertEquals(allLibraries.size(), 1);
    }

    @Test
    public void testMediaListCreationWithAlbums() {
        // insert cross ref
        MediaListAlbumCrossRef mediaListAlbumCrossRef = new MediaListAlbumCrossRef();
        mediaListAlbumCrossRef.setAlbumId(this.album.getId());
        mediaListAlbumCrossRef.setMediaListId(this.mediaList.getId());
        this.mediaListDAO.insertMediaListWithAlbums(mediaListAlbumCrossRef);

        // compare lists
        List<MediaListWithAlbums> mediaListWithAlbums = this.mediaListDAO.getAllMediaListAlbumObjects();
        List<MediaListWithAlbums> mediaListWithAlbumsDeadline = this.mediaListDAO.getAllDeadLineNotReachedAlbum(new Date().getTime());
        Assert.assertEquals(mediaListWithAlbums.size(), mediaListWithAlbumsDeadline.size());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        mediaListWithAlbumsDeadline = this.mediaListDAO.getAllDeadLineNotReachedAlbum(cal.getTime().getTime());
        Assert.assertNotEquals(mediaListWithAlbums.size(), mediaListWithAlbumsDeadline.size());

        MediaListWithAlbums tmp = this.mediaListDAO.getMediaListAlbumObject(this.mediaList.getId());
        Assert.assertNotEquals(0L, tmp.getAlbums().size());
        this.mediaListDAO.deleteMediaListWithAlbums(mediaListAlbumCrossRef);
        Assert.assertEquals(0, this.mediaListDAO.getAllMediaListAlbumObjects().get(0).getAlbums().size());
    }

    @Test
    public void testMediaListCreationWithBooks() {
        // insert cross ref
        MediaListBookCrossRef mediaListBookCrossRef = new MediaListBookCrossRef();
        mediaListBookCrossRef.setBookId(this.book.getId());
        mediaListBookCrossRef.setMediaListId(this.mediaList.getId());
        this.mediaListDAO.insertMediaListWithBooks(mediaListBookCrossRef);

        // compare lists
        List<MediaListWithBooks> mediaListWithBooks = this.mediaListDAO.getAllMediaListBookObjects();
        List<MediaListWithBooks> mediaListWithBooksDeadline = this.mediaListDAO.getAllDeadLineNotReachedBook(new Date().getTime());
        Assert.assertEquals(mediaListWithBooks.size(), mediaListWithBooksDeadline.size());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        mediaListWithBooksDeadline = this.mediaListDAO.getAllDeadLineNotReachedBook(cal.getTime().getTime());
        Assert.assertNotEquals(mediaListWithBooks.size(), mediaListWithBooksDeadline.size());

        MediaListWithBooks tmp = this.mediaListDAO.getMediaListBookObject(this.mediaList.getId());
        Assert.assertNotEquals(0L, tmp.getBooks().size());
        this.mediaListDAO.deleteMediaListWithBooks(mediaListBookCrossRef);
        Assert.assertEquals(0, this.mediaListDAO.getAllMediaListBookObjects().get(0).getBooks().size());
    }

    @Test
    public void testMediaListCreationWithGames() {
        // insert cross ref
        MediaListGameCrossRef mediaListGameCrossRef = new MediaListGameCrossRef();
        mediaListGameCrossRef.setGameId(this.game.getId());
        mediaListGameCrossRef.setMediaListId(this.mediaList.getId());
        this.mediaListDAO.insertMediaListWithGames(mediaListGameCrossRef);

        // compare lists
        List<MediaListWithGames> mediaListWithGames = this.mediaListDAO.getAllMediaListGameObjects();
        List<MediaListWithGames> mediaListWithGamesDeadline = this.mediaListDAO.getAllDeadLineNotReachedGame(new Date().getTime());
        Assert.assertEquals(mediaListWithGames.size(), mediaListWithGamesDeadline.size());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        mediaListWithGamesDeadline = this.mediaListDAO.getAllDeadLineNotReachedGame(cal.getTime().getTime());
        Assert.assertNotEquals(mediaListWithGames.size(), mediaListWithGamesDeadline.size());

        MediaListWithGames tmp = this.mediaListDAO.getMediaListGameObject(this.mediaList.getId());
        Assert.assertNotEquals(0L, tmp.getGames().size());
        this.mediaListDAO.deleteMediaListWithGames(mediaListGameCrossRef);
        Assert.assertEquals(0, this.mediaListDAO.getAllMediaListGameObjects().get(0).getGames().size());
    }

    @Test
    public void testMediaListCreationWithMovies() {
        // insert cross ref
        MediaListMovieCrossRef mediaListMovieCrossRef = new MediaListMovieCrossRef();
        mediaListMovieCrossRef.setMovieId(this.movie.getId());
        mediaListMovieCrossRef.setMediaListId(this.mediaList.getId());
        this.mediaListDAO.insertMediaListWithMovies(mediaListMovieCrossRef);

        // compare lists
        List<MediaListWithMovies> mediaListWithMovies = this.mediaListDAO.getAllMediaListMovieObjects();
        List<MediaListWithMovies> mediaListWithMoviesDeadline = this.mediaListDAO.getAllDeadLineNotReachedMovie(new Date().getTime());
        Assert.assertEquals(mediaListWithMovies.size(), mediaListWithMoviesDeadline.size());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        mediaListWithMoviesDeadline = this.mediaListDAO.getAllDeadLineNotReachedMovie(cal.getTime().getTime());
        Assert.assertNotEquals(mediaListWithMovies.size(), mediaListWithMoviesDeadline.size());

        MediaListWithMovies tmp = this.mediaListDAO.getMediaListMovieObject(this.mediaList.getId());
        Assert.assertNotEquals(0L, tmp.getMovies().size());
        this.mediaListDAO.deleteMediaListWithMovies(mediaListMovieCrossRef);
        Assert.assertEquals(0, this.mediaListDAO.getAllMediaListMovieObjects().get(0).getMovies().size());
    }
}
