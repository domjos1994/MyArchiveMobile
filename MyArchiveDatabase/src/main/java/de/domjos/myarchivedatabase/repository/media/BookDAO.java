package de.domjos.myarchivedatabase.repository.media;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.CompanyBookCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonBookCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagBookCrossRef;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.book.BookWithCompanies;
import de.domjos.myarchivedatabase.model.media.book.BookWithPersons;
import de.domjos.myarchivedatabase.model.media.book.BookWithTags;

@Dao
public interface BookDAO {
    @Query("SELECT * FROM books")
    List<Book> getAllBooks();

    @Query("SELECT * FROM books LIMIT :limit OFFSET :offset")
    List<Book> getAllBooks(int limit, int offset);

    @Query("SELECT * FROM books WHERE id=:id")
    Book getBook(long id);

    @Query("SELECT * FROM books WHERE title=:title")
    Book getBook(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertBooks(Book... books);

    @Update
    void updateBooks(Book... books);

    @Delete
    void deleteBooks(Book... books);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookWithPerson(PersonBookCrossRef... personBookCrossRefs);

    @Delete
    void deleteBookWithPerson(PersonBookCrossRef... personBookCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookWithCompany(CompanyBookCrossRef... companyBookCrossRefs);

    @Delete
    void deleteBookWithCompany(CompanyBookCrossRef... companyBookCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookWithTag(TagBookCrossRef... tagBookCrossRefs);

    @Delete
    void deleteBookWithTag(TagBookCrossRef... tagBookCrossRefs);

    @Transaction
    @Query("SELECT * FROM books")
    List<BookWithCompanies> getAllBooksWithCompanies();

    @Transaction
    @Query("SELECT * FROM books WHERE id=:id")
    BookWithCompanies getBookWithCompanies(long id);

    @Transaction
    @Query("SELECT * FROM books")
    List<BookWithPersons> getAllBooksWithPersons();

    @Transaction
    @Query("SELECT * FROM books WHERE id=:id")
    BookWithPersons getBookWithPersons(long id);

    @Transaction
    @Query("SELECT * FROM books")
    List<BookWithTags> getAllBooksWithTags();

    @Transaction
    @Query("SELECT * FROM books WHERE id=:id")
    BookWithTags getBookWithTags(long id);
}
