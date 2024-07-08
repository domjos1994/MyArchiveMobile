package de.domjos.myarchivedatabase.repository.media;

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
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyBookCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithBooks;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonBookCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonWithBooks;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagBookCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagWithBooks;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.book.BookWithCompanies;
import de.domjos.myarchivedatabase.model.media.book.BookWithPersons;
import de.domjos.myarchivedatabase.model.media.book.BookWithTags;
import de.domjos.myarchivedatabase.repository.general.CompanyDAO;
import de.domjos.myarchivedatabase.repository.general.PersonDAO;
import de.domjos.myarchivedatabase.repository.general.TagDAO;

@RunWith(AndroidJUnit4.class)
public class BookDAOTest {
    private BookDAO bookDAO;
    private PersonDAO personDAO;
    private CompanyDAO companyDAO;
    private TagDAO tagDAO;
    private AppDatabase appDatabase;
    private final String title = "Test";

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.bookDAO = this.appDatabase.bookDAO();
        this.personDAO = this.appDatabase.personDAO();
        this.companyDAO = this.appDatabase.companyDAO();
        this.tagDAO = this.appDatabase.tagDAO();
    }

    @After
    public void closeDb() {
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {

        // insert book
        Book book = new Book();
        book.setTitle(this.title);
        long id = this.bookDAO.insertBooks(book)[0];

        // get book
        List<Book> books = this.bookDAO.getAllBooks();
        Assert.assertEquals(books.size(), 1);
        book = this.bookDAO.getBook(this.title);
        Assert.assertEquals(book.getTitle(), this.title);
        Assert.assertEquals(id, book.getId());

        // delete book
        this.bookDAO.deleteBooks(book);
        books = this.bookDAO.getAllBooks();
        Assert.assertEquals(books.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert book
        Book book = new Book();
        book.setTitle(this.title);
        long id = this.bookDAO.insertBooks(book)[0];

        // get book
        List<Book> books = this.bookDAO.getAllBooks();
        Assert.assertEquals(books.size(), 1);
        book = this.bookDAO.getBook(this.title);
        Assert.assertEquals(book.getTitle(), this.title);
        Assert.assertEquals(id, book.getId());

        // update album
        String newTest = "Test2";
        book.setTitle(newTest);
        this.bookDAO.updateBooks(book);
        book = this.bookDAO.getBook(book.getId());
        Assert.assertEquals(book.getTitle(), newTest);

        // delete book
        this.bookDAO.deleteBooks(book);
        books = this.bookDAO.getAllBooks();
        Assert.assertEquals(books.size(), 0);
    }

    @Test
    public void testInsertAndAddPerson() {
        // insert book
        Book book = new Book();
        book.setTitle(this.title);
        book.setId(this.bookDAO.insertBooks(book)[0]);

        // insert person
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setId(this.personDAO.insertPersons(person)[0]);

        // create crossRef
        PersonBookCrossRef personBookCrossRef = new PersonBookCrossRef();
        personBookCrossRef.setBookId(book.getId());
        personBookCrossRef.setPersonId(person.getId());
        this.bookDAO.insertBookWithPerson(personBookCrossRef);

        // has crossRef
        BookWithPersons bookWithPersons = this.bookDAO.getBookWithPersons(book.getId());
        Assert.assertEquals(bookWithPersons.getPersons().size(), 1);
        PersonWithBooks personWithBooks = this.personDAO.getPersonWithBooks(person.getId());
        Assert.assertEquals(personWithBooks.getBooks().size(), 1);

        // delete crossRef
        this.bookDAO.deleteBookWithPerson(personBookCrossRef);
        List<BookWithPersons> booksWithPersons = this.bookDAO.getAllBooksWithPersons();
        Assert.assertEquals(booksWithPersons.size(), 1);
        Assert.assertEquals(booksWithPersons.get(0).getPersons().size(), 0);
        List<PersonWithBooks> personsWithBooks = this.personDAO.getAllPersonsWithBooks();
        Assert.assertEquals(personsWithBooks.size(), 1);
        Assert.assertEquals(personsWithBooks.get(0).getBooks().size(), 0);

        // delete data
        this.personDAO.deletePersons(person);
        this.bookDAO.deleteBooks(book);
    }

    @Test
    public void testInsertAndAddCompany() {
        // insert book
        Book book = new Book();
        book.setTitle(this.title);
        book.setId(this.bookDAO.insertBooks(book)[0]);

        // insert company
        Company company = new Company();
        company.setTitle(this.title);
        company.setId(this.companyDAO.insertCompanies(company)[0]);

        // create crossRef
        CompanyBookCrossRef companyBookCrossRef = new CompanyBookCrossRef();
        companyBookCrossRef.setBookId(book.getId());
        companyBookCrossRef.setCompanyId(company.getId());
        this.bookDAO.insertBookWithCompany(companyBookCrossRef);

        // has crossRef
        BookWithCompanies bookWithCompanies = this.bookDAO.getBookWithCompanies(book.getId());
        Assert.assertEquals(bookWithCompanies.getCompanies().size(), 1);
        CompanyWithBooks companyWithBooks = this.companyDAO.getCompanyWithBooks(company.getId());
        Assert.assertEquals(companyWithBooks.getBooks().size(), 1);

        // delete crossRef
        this.bookDAO.deleteBookWithCompany(companyBookCrossRef);
        List<BookWithCompanies> booksWithCompanies = this.bookDAO.getAllBooksWithCompanies();
        Assert.assertEquals(booksWithCompanies.size(), 1);
        Assert.assertEquals(booksWithCompanies.get(0).getCompanies().size(), 0);
        List<CompanyWithBooks> companiesWithBooks = this.companyDAO.getAllCompaniesWithBooks();
        Assert.assertEquals(companiesWithBooks.size(), 1);
        Assert.assertEquals(companiesWithBooks.get(0).getBooks().size(), 0);

        // delete data
        this.companyDAO.deleteCompanies(company);
        this.bookDAO.deleteBooks(book);
    }

    @Test
    public void testInsertAndAddTag() {
        // insert book
        Book book = new Book();
        book.setTitle(this.title);
        book.setId(this.bookDAO.insertBooks(book)[0]);

        // insert tag
        Tag tag = new Tag();
        tag.setTitle(this.title);
        tag.setId(this.tagDAO.insertTags(tag)[0]);

        // create crossRef
        TagBookCrossRef tagBookCrossRef = new TagBookCrossRef();
        tagBookCrossRef.setBookId(book.getId());
        tagBookCrossRef.setTagId(tag.getId());
        this.bookDAO.insertBookWithTag(tagBookCrossRef);

        // has crossRef
        BookWithTags bookWithTags = this.bookDAO.getBookWithTags(book.getId());
        Assert.assertEquals(bookWithTags.getTags().size(), 1);
        TagWithBooks tagWithBooks = this.tagDAO.getTagWithBooks(tag.getId());
        Assert.assertEquals(tagWithBooks.getBooks().size(), 1);

        // delete crossRef
        this.bookDAO.deleteBookWithTag(tagBookCrossRef);
        List<BookWithTags> albumsWithCompanies = this.bookDAO.getAllBooksWithTags();
        Assert.assertEquals(albumsWithCompanies.size(), 1);
        Assert.assertEquals(albumsWithCompanies.get(0).getTags().size(), 0);
        List<TagWithBooks> companiesWithBooks = this.tagDAO.getAllTagsWithBooks();
        Assert.assertEquals(companiesWithBooks.size(), 1);
        Assert.assertEquals(companiesWithBooks.get(0).getBooks().size(), 0);

        // delete data
        this.tagDAO.deleteTags(tag);
        this.bookDAO.deleteBooks(book);
    }
}
