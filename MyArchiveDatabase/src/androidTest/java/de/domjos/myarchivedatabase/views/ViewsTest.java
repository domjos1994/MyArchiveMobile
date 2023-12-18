package de.domjos.myarchivedatabase.views;

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
import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValue;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueBookCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueGameCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueMovieCrossRef;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;

@RunWith(AndroidJUnit4.class)
public class ViewsTest {
    private AppDatabase appDatabase;

    private Album album;
    private Book book;
    private Game game;
    private Movie movie;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();

        this.album = new Album();
        this.album.setTitle("Album1");
        this.album.setId(this.appDatabase.albumDAO().insertAlbums(this.album)[0]);

        this.book = new Book();
        this.book.setTitle("Book1");
        this.book.setId(this.appDatabase.bookDAO().insertBooks(this.book)[0]);

        this.game = new Game();
        this.game.setTitle("Game1");
        this.game.setId(this.appDatabase.gameDAO().insertGames(this.game)[0]);

        this.movie = new Movie();
        this.movie.setTitle("Movie1");
        this.movie.setId(this.appDatabase.movieDAO().insertMovies(this.movie)[0]);
    }

    @After
    public void closeDb() {
        this.appDatabase.movieDAO().deleteMovies(this.movie);
        this.appDatabase.gameDAO().deleteGames(this.game);
        this.appDatabase.bookDAO().deleteBooks(this.book);
        this.appDatabase.albumDAO().deleteAlbums(this.album);
        this.appDatabase.close();
    }

    @Test
    public void testCustomFieldWithMediaAndValue() {
        CustomField customField1 = new CustomField();
        customField1.setTitle("Field1");
        customField1.setId(this.appDatabase.customFieldDAO().insertCustomFields(customField1)[0]);

        CustomField customField2 = new CustomField();
        customField2.setTitle("Field2");
        customField2.setId(this.appDatabase.customFieldDAO().insertCustomFields(customField2)[0]);

        CustomField customField3 = new CustomField();
        customField3.setTitle("Field3");
        customField3.setId(this.appDatabase.customFieldDAO().insertCustomFields(customField3)[0]);

        CustomField customField4 = new CustomField();
        customField4.setTitle("Field4");
        customField4.setId(this.appDatabase.customFieldDAO().insertCustomFields(customField4)[0]);


        CustomFieldValue value1 = new CustomFieldValue();
        value1.setValue("Value1");
        value1.setCustomField(customField1.getId());
        value1.setId(this.appDatabase.customFieldValueDAO().insertCustomFieldValues(value1)[0]);

        CustomFieldValue value2 = new CustomFieldValue();
        value2.setValue("Value2");
        value2.setCustomField(customField2.getId());
        value2.setId(this.appDatabase.customFieldValueDAO().insertCustomFieldValues(value2)[0]);

        CustomFieldValue value3 = new CustomFieldValue();
        value3.setValue("Value3");
        value3.setCustomField(customField3.getId());
        value3.setId(this.appDatabase.customFieldValueDAO().insertCustomFieldValues(value3)[0]);

        CustomFieldValue value4 = new CustomFieldValue();
        value4.setValue("Value4");
        value4.setCustomField(customField4.getId());
        value4.setId(this.appDatabase.customFieldValueDAO().insertCustomFieldValues(value4)[0]);

        CustomFieldValueAlbumCrossRef customFieldValueAlbumCrossRef = new CustomFieldValueAlbumCrossRef();
        customFieldValueAlbumCrossRef.setAlbumId(this.album.getId());
        customFieldValueAlbumCrossRef.setCustomFieldValueId(value1.getId());
        this.appDatabase.customFieldValueDAO().insertCustomFieldAlbums(customFieldValueAlbumCrossRef);

        CustomFieldValueBookCrossRef customFieldValueBookCrossRef = new CustomFieldValueBookCrossRef();
        customFieldValueBookCrossRef.setBookId(this.book.getId());
        customFieldValueBookCrossRef.setCustomFieldValueId(value2.getId());
        this.appDatabase.customFieldValueDAO().insertCustomFieldBooks(customFieldValueBookCrossRef);

        CustomFieldValueGameCrossRef customFieldValueGameCrossRef = new CustomFieldValueGameCrossRef();
        customFieldValueGameCrossRef.setGameId(this.game.getId());
        customFieldValueGameCrossRef.setCustomFieldValueId(value3.getId());
        this.appDatabase.customFieldValueDAO().insertCustomFieldGames(customFieldValueGameCrossRef);

        CustomFieldValueMovieCrossRef customFieldValueMovieCrossRef = new CustomFieldValueMovieCrossRef();
        customFieldValueMovieCrossRef.setMovieId(this.movie.getId());
        customFieldValueMovieCrossRef.setCustomFieldValueId(value4.getId());
        this.appDatabase.customFieldValueDAO().insertCustomFieldMovies(customFieldValueMovieCrossRef);

        List<CustomFieldWithMediaAndValue> customFieldWithMediaAndValues = this.appDatabase.viewsDAO().getAllCustomFieldWithMediaAndValue();
        Assert.assertEquals(4, customFieldWithMediaAndValues.size());
        for(CustomFieldWithMediaAndValue customFieldWithMediaAndValue : customFieldWithMediaAndValues) {
            switch (customFieldWithMediaAndValue.getMediaType()) {
                case "Album" -> {
                    Assert.assertEquals(this.album.getId(), customFieldWithMediaAndValue.getMediaId());
                    Assert.assertEquals(value1.getValue(), customFieldWithMediaAndValue.getCustomFieldValue());
                }
                case "Book" -> {
                    Assert.assertEquals(this.book.getId(), customFieldWithMediaAndValue.getMediaId());
                    Assert.assertEquals(value2.getValue(), customFieldWithMediaAndValue.getCustomFieldValue());
                }
                case "Game" -> {
                    Assert.assertEquals(this.game.getId(), customFieldWithMediaAndValue.getMediaId());
                    Assert.assertEquals(value3.getValue(), customFieldWithMediaAndValue.getCustomFieldValue());
                }
                case "Movie" -> {
                    Assert.assertEquals(this.movie.getId(), customFieldWithMediaAndValue.getMediaId());
                    Assert.assertEquals(value4.getValue(), customFieldWithMediaAndValue.getCustomFieldValue());
                }
            }
        }

        this.appDatabase.customFieldValueDAO().deleteCustomFieldMovies(customFieldValueMovieCrossRef);
        this.appDatabase.customFieldValueDAO().deleteCustomFieldGames(customFieldValueGameCrossRef);
        this.appDatabase.customFieldValueDAO().deleteCustomFieldBooks(customFieldValueBookCrossRef);
        this.appDatabase.customFieldValueDAO().deleteCustomFieldAlbums(customFieldValueAlbumCrossRef);
        this.appDatabase.customFieldValueDAO().deleteCustomFieldValues(value4);
        this.appDatabase.customFieldValueDAO().deleteCustomFieldValues(value3);
        this.appDatabase.customFieldValueDAO().deleteCustomFieldValues(value2);
        this.appDatabase.customFieldValueDAO().deleteCustomFieldValues(value1);
        this.appDatabase.customFieldDAO().deleteCustomFields(customField4);
        this.appDatabase.customFieldDAO().deleteCustomFields(customField3);
        this.appDatabase.customFieldDAO().deleteCustomFields(customField2);
        this.appDatabase.customFieldDAO().deleteCustomFields(customField1);
    }

    @Test
    public void testMedia() {
        List<Media> mediaList = this.appDatabase.viewsDAO().getAllMedia();
        Assert.assertEquals(4, mediaList.size());

        for(Media media : mediaList) {
            switch (media.getType()) {
                case "Album" -> Assert.assertEquals(this.album.getId(), media.getId());
                case "Book" -> Assert.assertEquals(this.book.getId(), media.getId());
                case "Movie" -> Assert.assertEquals(this.movie.getId(), media.getId());
                case "Game" -> Assert.assertEquals(this.game.getId(), media.getId());
            }
        }
    }
}
