package de.domjos.myarchivedatabase.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithAlbums;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithBooks;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithGames;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithMovies;
import de.domjos.myarchivedatabase.model.general.category.CategoryWithSongs;

@Dao
public interface CategoryDAO {
    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE id=:id")
    Category getCategory(long id);

    @Query("SELECT * FROM categories WHERE title=:title")
    Category getCategory(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertCategories(Category... categories);

    @Update
    void updateCategories(Category... categories);

    @Delete
    void deleteCategories(Category... categories);

    @Transaction
    @Query("SELECT * FROM categories")
    List<CategoryWithAlbums> getAllCategoriesWithAlbums();

    @Transaction
    @Query("SELECT * FROM categories WHERE id=:id")
    CategoryWithAlbums getCategoryWithAlbums(long id);

    @Transaction
    @Query("SELECT * FROM categories")
    List<CategoryWithSongs> getAllCategoriesWithSongs();

    @Transaction
    @Query("SELECT * FROM categories WHERE id=:id")
    CategoryWithSongs getCategoryWithSongs(long id);

    @Transaction
    @Query("SELECT * FROM categories")
    List<CategoryWithMovies> getAllCategoriesWithMovies();

    @Transaction
    @Query("SELECT * FROM categories WHERE id=:id")
    CategoryWithMovies getCategoryWithMovies(long id);

    @Transaction
    @Query("SELECT * FROM categories")
    List<CategoryWithBooks> getAllCategoriesWithBooks();

    @Transaction
    @Query("SELECT * FROM categories WHERE id=:id")
    CategoryWithBooks getCategoryWithBooks(long id);

    @Transaction
    @Query("SELECT * FROM categories")
    List<CategoryWithGames> getAllCategoriesWithGames();

    @Transaction
    @Query("SELECT * FROM categories WHERE id=:id")
    CategoryWithGames getCategoryWithGames(long id);
}
