package de.domjos.myarchivedbvalidator.database;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedbvalidator.Database;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private Database database;

    @Before
    public void initDatabase() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.database = new Database(context);
    }

    @After
    public void closeDatabase() {
        this.database.close();
    }

    @Test
    public void testAlbumInsert() {
        Category category = new Category();
        category.setTitle("category");

        Album album = new Album();
        album.setTitle("album");
        album.setCategoryItem(category);
        this.database.insertAlbum(album);

        List<Album> albums = this.database.getAlbums();
        Assert.assertEquals(this.database.getMessages(), "", this.database.getMessages());
        Assert.assertEquals(1, albums.size());

        Assert.assertNotEquals(0, albums.get(0).getId());
        Assert.assertNotEquals(0, albums.get(0).getCategory());

        this.database.deleteAlbum(album);
        albums = this.database.getAlbums();
        Assert.assertEquals(0, albums.size());
    }

    @Test
    public void testSongInsert() {
        Category category = new Category();
        category.setTitle("category");

        Song song = new Song();
        song.setTitle("songs");
        song.setCategoryItem(category);
        this.database.insertSong(song, 0);

        List<Song> songs = this.database.getSongs();
        Assert.assertEquals(this.database.getMessages(), "", this.database.getMessages());
        Assert.assertEquals(1, songs.size());

        Assert.assertNotEquals(0, songs.get(0).getId());
        Assert.assertNotEquals(0, songs.get(0).getCategory());

        this.database.deleteSong(song);
        songs = this.database.getSongs();
        Assert.assertEquals(0, songs.size());
    }

    @Test
    public void testBookInsert() {
        Category category = new Category();
        category.setTitle("category");

        Book book = new Book();
        book.setTitle("book");
        book.setCategoryItem(category);
        this.database.insertBook(book);

        List<Book> books = this.database.getBooks();
        Assert.assertEquals(this.database.getMessages(), "", this.database.getMessages());
        Assert.assertEquals(1, books.size());

        Assert.assertNotEquals(0, books.get(0).getId());
        Assert.assertNotEquals(0, books.get(0).getCategory());

        this.database.deleteBook(book);
        books = this.database.getBooks();
        Assert.assertEquals(0, books.size());
    }

    @Test
    public void testGameInsert() {
        Category category = new Category();
        category.setTitle("category");

        Game game = new Game();
        game.setTitle("game");
        game.setCategoryItem(category);
        this.database.insertGame(game);

        List<Game> games = this.database.getGames();
        Assert.assertEquals(this.database.getMessages(), "", this.database.getMessages());
        Assert.assertEquals(1, games.size());

        Assert.assertNotEquals(0, games.get(0).getId());
        Assert.assertNotEquals(0, games.get(0).getCategory());

        this.database.deleteGame(game);
        games = this.database.getGames();
        Assert.assertEquals(0, games.size());
    }

    @Test
    public void testMovieInsert() {
        Category category = new Category();
        category.setTitle("category");

        Movie movie = new Movie();
        movie.setTitle("game");
        movie.setCategoryItem(category);
        this.database.insertMovie(movie);

        List<Movie> movies = this.database.getMovies();
        Assert.assertEquals(this.database.getMessages(), "", this.database.getMessages());
        Assert.assertEquals(1, movies.size());

        Assert.assertNotEquals(0, movies.get(0).getId());
        Assert.assertNotEquals(0, movies.get(0).getCategory());

        this.database.deleteMovie(movie);
        movies = this.database.getMovies();
        Assert.assertEquals(0, movies.size());
    }

    @Test
    public void testTagInsert() {
        Tag tag = new Tag();
        tag.setTitle("tag");
        this.database.insertTags(0, "", tag);

        List<Tag> tags = this.database.getTags(0, "");
        Assert.assertEquals(this.database.getMessages(), "", this.database.getMessages());
        Assert.assertEquals(1, tags.size());

        Assert.assertNotEquals(0, tags.size());

        this.database.deleteTags("", 0);
        tags = this.database.getTags(0, "");
        Assert.assertEquals(0, tags.size());
    }
}
