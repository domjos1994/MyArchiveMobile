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
import de.domjos.myarchivedatabase.model.fileTree.FileTree;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFile;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithAlbums;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithBooks;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithFileTree;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithFileTreeFile;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithGames;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithMovies;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithSongs;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedatabase.repository.fileTree.FileTreeDAO;
import de.domjos.myarchivedatabase.repository.fileTree.FileTreeFileDAO;
import de.domjos.myarchivedatabase.repository.media.AlbumDAO;
import de.domjos.myarchivedatabase.repository.media.BookDAO;
import de.domjos.myarchivedatabase.repository.media.GameDAO;
import de.domjos.myarchivedatabase.repository.media.MovieDAO;
import de.domjos.myarchivedatabase.repository.media.SongDAO;

@RunWith(AndroidJUnit4.class)

public class CategoryDAOTest {
    private CategoryDAO categoryDAO;
    private AlbumDAO albumDAO;
    private SongDAO songDAO;
    private MovieDAO movieDAO;
    private BookDAO bookDAO;
    private GameDAO gameDAO;
    private FileTreeDAO fileTreeDAO;
    private FileTreeFileDAO fileTreeFileDAO;
    private AppDatabase appDatabase;
    private final String title = "Test-Category";

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.categoryDAO = this.appDatabase.categoryDAO();
        this.albumDAO = this.appDatabase.albumDAO();
        this.songDAO = this.appDatabase.songDAO();
        this.movieDAO = this.appDatabase.movieDAO();
        this.bookDAO = this.appDatabase.bookDAO();
        this.gameDAO = this.appDatabase.gameDAO();
        this.fileTreeDAO = this.appDatabase.fileTreeDAO();
        this.fileTreeFileDAO = this.appDatabase.fileTreeFileDAO();
    }

    @After
    public void closeDb() {
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {

        // insert category
        Category category = new Category();
        category.setTitle(this.title);
        long id = this.categoryDAO.insertCategories(category)[0];

        // get category
        List<Category> categories = this.categoryDAO.getAllCategories();
        Assert.assertEquals(categories.size(), 1);
        category = this.categoryDAO.getCategory(this.title);
        Assert.assertEquals(category.getTitle(), this.title);
        Assert.assertEquals(id, category.getId());

        // delete category
        this.categoryDAO.deleteCategories(category);
        categories = this.categoryDAO.getAllCategories();
        Assert.assertEquals(categories.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert category
        Category category = new Category();
        category.setTitle(this.title);
        this.categoryDAO.insertCategories(category);

        // get category
        List<Category> categories = this.categoryDAO.getAllCategories();
        Assert.assertEquals(categories.size(), 1);
        category = this.categoryDAO.getCategory(this.title);
        Assert.assertEquals(category.getTitle(), this.title);

        // update category
        String newTest = "Test2";
        category.setTitle(newTest);
        this.categoryDAO.updateCategories(category);
        category = this.categoryDAO.getCategory(category.getId());
        Assert.assertEquals(category.getTitle(), newTest);

        // delete category
        this.categoryDAO.deleteCategories(category);
        categories = this.categoryDAO.getAllCategories();
        Assert.assertEquals(categories.size(), 0);
    }

    @Test
    public void testInsertAndAddCategoryWithAlbum() {
        // create category
        Category category = new Category();
        category.setTitle(this.title);
        category.setId(this.categoryDAO.insertCategories(category)[0]);

        // create Album
        Album album = new Album();
        album.setTitle(this.title);
        album.setCategory(category.getId());
        album.setId(this.albumDAO.insertAlbums(album)[0]);

        // call category with albums
        CategoryWithAlbums categoryWithAlbums = this.categoryDAO.getCategoryWithAlbums(category.getId());
        Assert.assertNotNull(categoryWithAlbums);

        this.categoryDAO.deleteCategories(category);
        List<CategoryWithAlbums> categoriesWithAlbums = this.categoryDAO.getAllCategoriesWithAlbums();
        Assert.assertEquals(categoriesWithAlbums.size(), 0);
        this.albumDAO.deleteAlbums(album);
    }

    @Test
    public void testInsertAndAddCategoryWithSong() {
        // create category
        Category category = new Category();
        category.setTitle(this.title);
        category.setId(this.categoryDAO.insertCategories(category)[0]);

        // create Album
        Song song = new Song();
        song.setTitle(this.title);
        song.setCategory(category.getId());
        song.setId(this.songDAO.insertSongs(song)[0]);

        // call category with albums
        CategoryWithSongs categoryWithSongs = this.categoryDAO.getCategoryWithSongs(category.getId());
        Assert.assertNotNull(categoryWithSongs);

        this.categoryDAO.deleteCategories(category);
        List<CategoryWithSongs> categoriesWithSongs = this.categoryDAO.getAllCategoriesWithSongs();
        Assert.assertEquals(categoriesWithSongs.size(), 0);
        this.songDAO.deleteSongs(song);
    }

    @Test
    public void testInsertAndAddCategoryWithMovie() {
        // create category
        Category category = new Category();
        category.setTitle(this.title);
        category.setId(this.categoryDAO.insertCategories(category)[0]);

        // create Movie
        Movie movie = new Movie();
        movie.setTitle(this.title);
        movie.setCategory(category.getId());
        movie.setId(this.movieDAO.insertMovies(movie)[0]);

        // call category with Movies
        CategoryWithMovies categoryWithMovies = this.categoryDAO.getCategoryWithMovies(category.getId());
        Assert.assertNotNull(categoryWithMovies);

        this.categoryDAO.deleteCategories(category);
        List<CategoryWithMovies> categoriesWithMovies = this.categoryDAO.getAllCategoriesWithMovies();
        Assert.assertEquals(categoriesWithMovies.size(), 0);
        this.movieDAO.deleteMovies(movie);
    }

    @Test
    public void testInsertAndAddCategoryWithBook() {
        // create category
        Category category = new Category();
        category.setTitle(this.title);
        category.setId(this.categoryDAO.insertCategories(category)[0]);

        // create Movie
        Book book = new Book();
        book.setTitle(this.title);
        book.setCategory(category.getId());
        book.setId(this.bookDAO.insertBooks(book)[0]);

        // call category with Movies
        CategoryWithBooks categoryWithBooks = this.categoryDAO.getCategoryWithBooks(category.getId());
        Assert.assertNotNull(categoryWithBooks);

        this.categoryDAO.deleteCategories(category);
        List<CategoryWithBooks> categoriesWithBooks = this.categoryDAO.getAllCategoriesWithBooks();
        Assert.assertEquals(categoriesWithBooks.size(), 0);
        this.bookDAO.deleteBooks(book);
    }

    @Test
    public void testInsertAndAddCategoryWithGame() {
        // create category
        Category category = new Category();
        category.setTitle(this.title);
        category.setId(this.categoryDAO.insertCategories(category)[0]);

        // create Game
        Game game = new Game();
        game.setTitle(this.title);
        game.setCategory(category.getId());
        game.setId(this.gameDAO.insertGames(game)[0]);

        // call category with Games
        CategoryWithGames categoryWithGames = this.categoryDAO.getCategoryWithGames(category.getId());
        Assert.assertNotNull(categoryWithGames);

        this.categoryDAO.deleteCategories(category);
        List<CategoryWithGames> categoriesWithGames = this.categoryDAO.getAllCategoriesWithGames();
        Assert.assertEquals(categoriesWithGames.size(), 0);
        this.gameDAO.deleteGames(game);
    }

    @Test
    public void testInsertAndAddCategoryWithFileTree() {
        // create category
        Category category = new Category();
        category.setTitle(this.title);
        category.setId(this.categoryDAO.insertCategories(category)[0]);

        // create Game
        FileTree fileTree = new FileTree();
        fileTree.setTitle(this.title);
        fileTree.setCategory(category.getId());
        fileTree.setId(this.fileTreeDAO.insertFileTreeElements(fileTree)[0]);

        // call category with Games
        CategoryWithFileTree categoryWithGames = this.categoryDAO.getCategoryWithFileTree(category.getId());
        Assert.assertNotNull(categoryWithGames);

        this.categoryDAO.deleteCategories(category);
        List<CategoryWithFileTree> categoriesWithFileTrees = this.categoryDAO.getAllCategoriesWithFileTreeElements();
        Assert.assertEquals(categoriesWithFileTrees.size(), 0);
        this.fileTreeDAO.deleteFileTreeElements(fileTree);
    }

    @Test
    public void testInsertAndAddCategoryWithFileTreeFile() {
        // create category
        Category category = new Category();
        category.setTitle(this.title);
        category.setId(this.categoryDAO.insertCategories(category)[0]);

        // create Game
        FileTreeFile fileTreeFile = new FileTreeFile();
        fileTreeFile.setTitle(this.title);
        fileTreeFile.setCategory(category.getId());
        fileTreeFile.setId(this.fileTreeFileDAO.insertFileTreeFileElements(fileTreeFile)[0]);

        // call category with Games
        CategoryWithFileTree categoryWithGames = this.categoryDAO.getCategoryWithFileTree(category.getId());
        Assert.assertNotNull(categoryWithGames);

        this.categoryDAO.deleteCategories(category);
        List<CategoryWithFileTreeFile> categoriesWithFileTreeFiles = this.categoryDAO.getAllCategoriesWithFileTreeFileElements();
        Assert.assertEquals(categoriesWithFileTreeFiles.size(), 0);
        this.fileTreeFileDAO.deleteFileTreeFileElements(fileTreeFile);
    }
}
