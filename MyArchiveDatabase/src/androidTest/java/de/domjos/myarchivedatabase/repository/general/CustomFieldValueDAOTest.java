package de.domjos.myarchivedatabase.repository.general;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.filter.Filter;
import de.domjos.myarchivedatabase.model.filter.FilterWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValue;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueBookCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueFilterCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueGameCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldWithValues;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.book.BookWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.game.GameWithCustomFieldValues;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithCustomFieldValues;
import de.domjos.myarchivedatabase.repository.FilterDAO;
import de.domjos.myarchivedatabase.repository.media.AlbumDAO;
import de.domjos.myarchivedatabase.repository.media.BookDAO;
import de.domjos.myarchivedatabase.repository.media.GameDAO;
import de.domjos.myarchivedatabase.repository.media.MovieDAO;

@RunWith(AndroidJUnit4.class)

public class CustomFieldValueDAOTest {
    private CustomFieldValueDAO customFieldValueDAO;
    private CustomFieldDAO customFieldDAO;
    private BookDAO bookDAO;
    private MovieDAO movieDAO;
    private AlbumDAO albumDAO;
    private GameDAO gameDAO;
    private FilterDAO filterDAO;
    private AppDatabase appDatabase;
    private final String test = "Test";
    private CustomField customField;
    private CustomFieldValue customFieldValue;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.customFieldDAO = this.appDatabase.customFieldDAO();
        this.customFieldValueDAO = this.appDatabase.customFieldValueDAO();
        this.bookDAO = this.appDatabase.bookDAO();
        this.movieDAO = this.appDatabase.movieDAO();
        this.albumDAO = this.appDatabase.albumDAO();
        this.gameDAO = this.appDatabase.gameDAO();
        this.filterDAO = this.appDatabase.filterDAO();

        this.customField = new CustomField();
        this.customField.setTitle(this.test);
        this.customField.setId(this.customFieldDAO.insertCustomFields(this.customField)[0]);

        // insert customField
        this.customFieldValue = new CustomFieldValue();
        this.customFieldValue.setCustomField(this.customField.getId());
        this.customFieldValue.setValue("42");
        this.customFieldValue.setId(this.customFieldValueDAO.insertCustomFieldValues(customFieldValue)[0]);
    }

    @After
    public void closeDb() {
        this.customFieldValueDAO.deleteCustomFieldValues(this.customFieldValue);
        this.customFieldDAO.deleteCustomFields(this.customField);
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {

        // insert customField
        CustomFieldValue customFieldValue = new CustomFieldValue();
        customFieldValue.setCustomField(this.customField.getId());
        customFieldValue.setValue("42");
        customFieldValue.setId(this.customFieldValueDAO.insertCustomFieldValues(customFieldValue)[0]);

        // get customField
        List<CustomFieldValue> customFieldValues = this.customFieldValueDAO.getCustomFieldValues(customField.getId());
        Assert.assertEquals(customFieldValues.size(), 2);

        // delete customField
        this.customFieldValueDAO.deleteCustomFieldValues(customFieldValue);
        customFieldValues = this.customFieldValueDAO.getAllCustomFieldValues();
        Assert.assertEquals(customFieldValues.size(), 1);
    }

    @Test
    public void testCustomFieldValueBookRef() {

        // create Book
        Book book = new Book();
        book.setTitle(this.test);
        book.setId(this.bookDAO.insertBooks(book)[0]);

        // create Ref
        CustomFieldValueBookCrossRef bookCrossRef = new CustomFieldValueBookCrossRef();
        bookCrossRef.setCustomFieldValueId(this.customFieldValue.getId());
        bookCrossRef.setBookId(book.getId());
        this.customFieldValueDAO.insertCustomFieldBooks(bookCrossRef);

        // get data
        List<CustomFieldWithValues> customFieldWithValues = this.customFieldValueDAO.getCustomFieldsWithValues(this.customFieldValue.getCustomField());
        List<BookWithCustomFieldValues> bookWithCustomFieldValues = this.customFieldValueDAO.getBookWithCustomFieldValues(this.customFieldValue.getCustomField());
        List<MovieWithCustomFieldValues> movieWithCustomFieldValues = this.customFieldValueDAO.getMovieWithCustomFieldValues(this.customFieldValue.getCustomField());
        Assert.assertEquals(bookWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());
        Assert.assertNotEquals(movieWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());

        this.customFieldValueDAO.deleteCustomFieldBooks(bookCrossRef);
        this.customFieldValueDAO.deleteCustomFieldValues(this.customFieldValue);
        customFieldWithValues = this.customFieldValueDAO.getAllCustomFieldsWithValues();
        Assert.assertEquals(0, customFieldWithValues.size());
    }

    @Test
    public void testCustomFieldValueAlbumRef() {

        // create Book
        Album album = new Album();
        album.setTitle(this.test);
        album.setId(this.albumDAO.insertAlbums(album)[0]);

        // create Ref
        CustomFieldValueAlbumCrossRef albumCrossRef = new CustomFieldValueAlbumCrossRef();
        albumCrossRef.setCustomFieldValueId(this.customFieldValue.getId());
        albumCrossRef.setAlbumId(album.getId());
        this.customFieldValueDAO.insertCustomFieldAlbums(albumCrossRef);

        // get data
        List<CustomFieldWithValues> customFieldWithValues = this.customFieldValueDAO.getCustomFieldsWithValues(this.customFieldValue.getCustomField());
        List<AlbumWithCustomFieldValues> albumWithCustomFieldValues = this.customFieldValueDAO.getAlbumWithCustomFieldValues(this.customFieldValue.getCustomField());
        List<BookWithCustomFieldValues> bookWithCustomFieldValues = this.customFieldValueDAO.getBookWithCustomFieldValues(this.customFieldValue.getCustomField());
        Assert.assertEquals(albumWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());
        Assert.assertNotEquals(bookWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());

        this.customFieldValueDAO.deleteCustomFieldAlbums(albumCrossRef);
        this.customFieldValueDAO.deleteCustomFieldValues(this.customFieldValue);
        customFieldWithValues = this.customFieldValueDAO.getAllCustomFieldsWithValues();
        Assert.assertEquals(0, customFieldWithValues.size());
    }

    @Test
    public void testCustomFieldValueGameRef() {

        // create Book
        Game game = new Game();
        game.setTitle(this.test);
        game.setId(this.gameDAO.insertGames(game)[0]);

        // create Ref
        CustomFieldValueGameCrossRef gameCrossRef = new CustomFieldValueGameCrossRef();
        gameCrossRef.setCustomFieldValueId(this.customFieldValue.getId());
        gameCrossRef.setGameId(game.getId());
        this.customFieldValueDAO.insertCustomFieldGames(gameCrossRef);

        // get data
        List<CustomFieldWithValues> customFieldWithValues = this.customFieldValueDAO.getCustomFieldsWithValues(this.customFieldValue.getCustomField());
        List<GameWithCustomFieldValues> gameWithCustomFieldValues = this.customFieldValueDAO.getGameWithCustomFieldValues(this.customFieldValue.getCustomField());
        List<AlbumWithCustomFieldValues> albumWithCustomFieldValues = this.customFieldValueDAO.getAlbumWithCustomFieldValues(this.customFieldValue.getCustomField());
        Assert.assertEquals(gameWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());
        Assert.assertNotEquals(albumWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());

        this.customFieldValueDAO.deleteCustomFieldGames(gameCrossRef);
        this.customFieldValueDAO.deleteCustomFieldValues(this.customFieldValue);
        customFieldWithValues = this.customFieldValueDAO.getAllCustomFieldsWithValues();
        Assert.assertEquals(0, customFieldWithValues.size());
    }

    @Test
    public void testCustomFieldValueMovieRef() {

        // create Book
        Movie movie = new Movie();
        movie.setTitle(this.test);
        movie.setId(this.movieDAO.insertMovies(movie)[0]);

        // create Ref
        CustomFieldValueMovieCrossRef movieCrossRef = new CustomFieldValueMovieCrossRef();
        movieCrossRef.setCustomFieldValueId(this.customFieldValue.getId());
        movieCrossRef.setMovieId(movie.getId());
        this.customFieldValueDAO.insertCustomFieldMovies(movieCrossRef);

        // get data
        List<CustomFieldWithValues> customFieldWithValues = this.customFieldValueDAO.getCustomFieldsWithValues(this.customFieldValue.getCustomField());
        List<MovieWithCustomFieldValues> movieWithCustomFieldValues = this.customFieldValueDAO.getMovieWithCustomFieldValues(this.customFieldValue.getCustomField());
        List<GameWithCustomFieldValues> gameWithCustomFieldValues = this.customFieldValueDAO.getGameWithCustomFieldValues(this.customFieldValue.getCustomField());
        Assert.assertEquals(movieWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());
        Assert.assertNotEquals(gameWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());

        this.customFieldValueDAO.deleteCustomFieldMovies(movieCrossRef);
        this.customFieldValueDAO.deleteCustomFieldValues(this.customFieldValue);
        customFieldWithValues = this.customFieldValueDAO.getAllCustomFieldsWithValues();
        Assert.assertEquals(0, customFieldWithValues.size());
    }

    @Test
    public void testCustomFieldValueFilterRef() {

        // create Book
        Filter filter = new Filter();
        filter.setTitle(this.test);
        filter.setId(this.filterDAO.insertFilters(filter)[0]);

        // create Ref
        CustomFieldValueFilterCrossRef filterCrossRef = new CustomFieldValueFilterCrossRef();
        filterCrossRef.setCustomFieldValueId(this.customFieldValue.getId());
        filterCrossRef.setFilterId(filter.getId());
        this.customFieldValueDAO.insertCustomFieldFilters(filterCrossRef);

        // get data
        List<CustomFieldWithValues> customFieldWithValues = this.customFieldValueDAO.getCustomFieldsWithValues(this.customFieldValue.getCustomField());
        List<FilterWithCustomFieldValues> filterWithCustomFieldValues = this.customFieldValueDAO.getFilterWithCustomFieldValues(this.customFieldValue.getCustomField());
        List<MovieWithCustomFieldValues> movieWithCustomFieldValues = this.customFieldValueDAO.getMovieWithCustomFieldValues(this.customFieldValue.getCustomField());
        Assert.assertEquals(filterWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());
        Assert.assertNotEquals(movieWithCustomFieldValues.get(0).getCustomFieldWithValues().size(), customFieldWithValues.get(0).getCustomFieldValues().size());

        this.customFieldValueDAO.deleteCustomFieldFilters(filterCrossRef);
        this.customFieldValueDAO.deleteCustomFieldValues(this.customFieldValue);
        customFieldWithValues = this.customFieldValueDAO.getAllCustomFieldsWithValues();
        Assert.assertEquals(0, customFieldWithValues.size());
    }
}
