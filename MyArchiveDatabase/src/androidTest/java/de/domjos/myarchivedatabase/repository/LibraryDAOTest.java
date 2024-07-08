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
public class LibraryDAOTest {
    private LibraryDAO libraryDAO;
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
    private Library library;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.libraryDAO = this.appDatabase.libraryDAO();
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

        this.library = new Library();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2023);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        this.library.setDeadline(cal.getTime());
        this.library.setPerson(this.person.getId());
        this.library.setId(this.libraryDAO.insertLibrary(this.library)[0]);
    }

    @After
    public void closeDb() {
        this.libraryDAO.deleteLibrary(this.library);
        this.personDAO.deletePersons(this.person);
        this.albumDAO.deleteAlbums(this.album);
        this.bookDAO.deleteBooks(this.book);
        this.gameDAO.deleteGames(this.game);
        this.movieDAO.deleteMovies(this.movie);
        this.appDatabase.close();
    }

    @Test
    public void testLibraryCreation() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2023);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);


        // create library
        Library library = new Library();
        library.setDeadline(cal.getTime());
        library.setPerson(this.person.getId());
        library.setId(this.libraryDAO.insertLibrary(library)[0]);

        // get library
        Library tmp = this.libraryDAO.getLibraryObject(library.getId());
        Assert.assertEquals(tmp.getDeadline(), library.getDeadline());

        // update library
        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        tmp.setDeadline(cal.getTime());
        this.libraryDAO.updateLibrary(tmp);
        List<Library> libraries = this.libraryDAO.getAllDeadLineNotReached(new Date().getTime());
        Assert.assertEquals(libraries.size(), 2);
        Assert.assertEquals(libraries.get(1).getDeadline(), tmp.getDeadline());

        // delete object
        this.libraryDAO.deleteLibrary(tmp);

        List<Library> allLibraries = this.libraryDAO.getAllLibraryObjects();
        Assert.assertEquals(allLibraries.size(), 1);
    }

    @Test
    public void testLibraryCreationWithAlbums() {
        // insert cross ref
        LibraryAlbumCrossRef libraryAlbumCrossRef = new LibraryAlbumCrossRef();
        libraryAlbumCrossRef.setAlbumId(this.album.getId());
        libraryAlbumCrossRef.setLibraryId(this.library.getId());
        this.libraryDAO.insertLibraryWithAlbums(libraryAlbumCrossRef);

        // compare lists
        List<LibraryWithAlbums> libraryWithAlbums = this.libraryDAO.getAllLibraryAlbumObjects();
        List<LibraryWithAlbums> libraryWithAlbumsDeadline = this.libraryDAO.getAllDeadLineNotReachedAlbum(new Date().getTime());
        Assert.assertEquals(libraryWithAlbums.size(), libraryWithAlbumsDeadline.size());

        // library with persons
        List<LibraryPersonWithAlbums> libraryPersonWithAlbums = this.libraryDAO.getAllLibraryAlbumPersonObjects();
        List<LibraryPersonWithAlbums> libraryPersonWithAlbumsDeadline = this.libraryDAO.getAllDeadLineNotReachedAlbumPerson(new Date().getTime());
        LibraryPersonWithAlbums albums = this.libraryDAO.getLibraryAlbumPersonObject(this.library.getId());
        Assert.assertEquals(libraryPersonWithAlbums.size(), libraryPersonWithAlbumsDeadline.size());
        Assert.assertEquals(albums.getPerson().getId(), libraryPersonWithAlbumsDeadline.get(0).getPerson().getId());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        libraryWithAlbumsDeadline = this.libraryDAO.getAllDeadLineNotReachedAlbum(cal.getTime().getTime());
        Assert.assertNotEquals(libraryWithAlbums.size(), libraryWithAlbumsDeadline.size());

        LibraryWithAlbums tmp = this.libraryDAO.getLibraryAlbumObject(this.library.getId());
        Assert.assertNotEquals(0L, tmp.getAlbums().size());
        this.libraryDAO.deleteLibraryWithAlbums(libraryAlbumCrossRef);
        Assert.assertEquals(0, this.libraryDAO.getAllLibraryAlbumObjects().get(0).getAlbums().size());
    }

    @Test
    public void testLibraryCreationWithBooks() {
        // insert cross ref
        LibraryBookCrossRef libraryBookCrossRef = new LibraryBookCrossRef();
        libraryBookCrossRef.setBookId(this.book.getId());
        libraryBookCrossRef.setLibraryId(this.library.getId());
        this.libraryDAO.insertLibraryWithBooks(libraryBookCrossRef);

        // compare lists
        List<LibraryWithBooks> libraryWithBooks = this.libraryDAO.getAllLibraryBookObjects();
        List<LibraryWithBooks> libraryWithBooksDeadline = this.libraryDAO.getAllDeadLineNotReachedBook(new Date().getTime());
        Assert.assertEquals(libraryWithBooks.size(), libraryWithBooksDeadline.size());

        // library with persons
        List<LibraryPersonWithBooks> libraryPersonWithBooks = this.libraryDAO.getAllLibraryBookPersonObjects();
        List<LibraryPersonWithBooks> libraryPersonWithBooksDeadline = this.libraryDAO.getAllDeadLineNotReachedBookPerson(new Date().getTime());
        LibraryPersonWithBooks books = this.libraryDAO.getLibraryBookPersonObject(this.library.getId());
        Assert.assertEquals(libraryPersonWithBooks.size(), libraryPersonWithBooksDeadline.size());
        Assert.assertEquals(books.getPerson().getId(), libraryPersonWithBooksDeadline.get(0).getPerson().getId());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        libraryWithBooksDeadline = this.libraryDAO.getAllDeadLineNotReachedBook(cal.getTime().getTime());
        Assert.assertNotEquals(libraryWithBooks.size(), libraryWithBooksDeadline.size());

        LibraryWithBooks tmp = this.libraryDAO.getLibraryBookObject(this.library.getId());
        Assert.assertNotEquals(0L, tmp.getBooks().size());
        this.libraryDAO.deleteLibraryWithBooks(libraryBookCrossRef);
        Assert.assertEquals(0, this.libraryDAO.getAllLibraryBookObjects().get(0).getBooks().size());
    }

    @Test
    public void testLibraryCreationWithGames() {
        // insert cross ref
        LibraryGameCrossRef libraryGameCrossRef = new LibraryGameCrossRef();
        libraryGameCrossRef.setGameId(this.game.getId());
        libraryGameCrossRef.setLibraryId(this.library.getId());
        this.libraryDAO.insertLibraryWithGames(libraryGameCrossRef);

        // compare lists
        List<LibraryWithGames> libraryWithGames = this.libraryDAO.getAllLibraryGameObjects();
        List<LibraryWithGames> libraryWithGamesDeadline = this.libraryDAO.getAllDeadLineNotReachedGame(new Date().getTime());
        Assert.assertEquals(libraryWithGames.size(), libraryWithGamesDeadline.size());

        // library with persons
        List<LibraryPersonWithGames> libraryPersonWithGames = this.libraryDAO.getAllLibraryGamePersonObjects();
        List<LibraryPersonWithGames> libraryPersonWithGamesDeadline = this.libraryDAO.getAllDeadLineNotReachedGamePerson(new Date().getTime());
        LibraryPersonWithGames games = this.libraryDAO.getLibraryGamePersonObject(this.library.getId());
        Assert.assertEquals(libraryPersonWithGames.size(), libraryPersonWithGamesDeadline.size());
        Assert.assertEquals(games.getPerson().getId(), libraryPersonWithGamesDeadline.get(0).getPerson().getId());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        libraryWithGamesDeadline = this.libraryDAO.getAllDeadLineNotReachedGame(cal.getTime().getTime());
        Assert.assertNotEquals(libraryWithGames.size(), libraryWithGamesDeadline.size());

        LibraryWithGames tmp = this.libraryDAO.getLibraryGameObject(this.library.getId());
        Assert.assertNotEquals(0L, tmp.getGames().size());
        this.libraryDAO.deleteLibraryWithGames(libraryGameCrossRef);
        Assert.assertEquals(0, this.libraryDAO.getAllLibraryGameObjects().get(0).getGames().size());
    }

    @Test
    public void testLibraryCreationWithMovies() {
        // insert cross ref
        LibraryMovieCrossRef libraryMovieCrossRef = new LibraryMovieCrossRef();
        libraryMovieCrossRef.setMovieId(this.movie.getId());
        libraryMovieCrossRef.setLibraryId(this.library.getId());
        this.libraryDAO.insertLibraryWithMovies(libraryMovieCrossRef);

        // compare lists
        List<LibraryWithMovies> libraryWithMovies = this.libraryDAO.getAllLibraryMovieObjects();
        List<LibraryWithMovies> libraryWithMoviesDeadline = this.libraryDAO.getAllDeadLineNotReachedMovie(new Date().getTime());
        Assert.assertEquals(libraryWithMovies.size(), libraryWithMoviesDeadline.size());

        // library with persons
        List<LibraryPersonWithMovies> libraryPersonWithMovies = this.libraryDAO.getAllLibraryMoviePersonObjects();
        List<LibraryPersonWithMovies> libraryPersonWithMoviesDeadline = this.libraryDAO.getAllDeadLineNotReachedMoviePerson(new Date().getTime());
        LibraryPersonWithMovies movies = this.libraryDAO.getLibraryMoviePersonObject(this.library.getId());
        Assert.assertEquals(libraryPersonWithMovies.size(), libraryPersonWithMoviesDeadline.size());
        Assert.assertEquals(movies.getPerson().getId(), libraryPersonWithMoviesDeadline.get(0).getPerson().getId());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2024);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        libraryWithMoviesDeadline = this.libraryDAO.getAllDeadLineNotReachedMovie(cal.getTime().getTime());
        Assert.assertNotEquals(libraryWithMovies.size(), libraryWithMoviesDeadline.size());

        LibraryWithMovies tmp = this.libraryDAO.getLibraryMovieObject(this.library.getId());
        Assert.assertNotEquals(0L, tmp.getMovies().size());
        this.libraryDAO.deleteLibraryWithMovies(libraryMovieCrossRef);
        Assert.assertEquals(0, this.libraryDAO.getAllLibraryMovieObjects().get(0).getMovies().size());
    }
}
