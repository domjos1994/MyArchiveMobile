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
import de.domjos.myarchivedatabase.model.general.company.CompanyMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithMovies;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonWithMovies;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagWithMovies;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithCompanies;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithPersons;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithTags;
import de.domjos.myarchivedatabase.repository.general.CompanyDAO;
import de.domjos.myarchivedatabase.repository.general.PersonDAO;
import de.domjos.myarchivedatabase.repository.general.TagDAO;

@RunWith(AndroidJUnit4.class)
public class MovieDAOTest {
    private MovieDAO movieDAO;
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
        this.movieDAO = this.appDatabase.movieDAO();
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

        // insert movie
        Movie movie = new Movie();
        movie.setTitle(this.title);
        long id = this.movieDAO.insertMovies(movie)[0];

        // get movie
        List<Movie> movies = this.movieDAO.getAllMovies();
        Assert.assertEquals(movies.size(), 1);
        movie = this.movieDAO.getMovie(this.title);
        Assert.assertEquals(movie.getTitle(), this.title);
        Assert.assertEquals(id, movie.getId());

        // delete movie
        this.movieDAO.deleteMovies(movie);
        movies = this.movieDAO.getAllMovies();
        Assert.assertEquals(movies.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert movie
        Movie movie = new Movie();
        movie.setTitle(this.title);
        long id = this.movieDAO.insertMovies(movie)[0];

        // get movie
        List<Movie> movies = this.movieDAO.getAllMovies();
        Assert.assertEquals(movies.size(), 1);
        movie = this.movieDAO.getMovie(this.title);
        Assert.assertEquals(movie.getTitle(), this.title);
        Assert.assertEquals(id, movie.getId());

        // update album
        String newTest = "Test2";
        movie.setTitle(newTest);
        this.movieDAO.updateMovies(movie);
        movie = this.movieDAO.getMovie(movie.getId());
        Assert.assertEquals(movie.getTitle(), newTest);

        // delete movie
        this.movieDAO.deleteMovies(movie);
        movies = this.movieDAO.getAllMovies();
        Assert.assertEquals(movies.size(), 0);
    }

    @Test
    public void testInsertAndAddPerson() {
        // insert movie
        Movie movie = new Movie();
        movie.setTitle(this.title);
        movie.setId(this.movieDAO.insertMovies(movie)[0]);

        // insert person
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setId(this.personDAO.insertPersons(person)[0]);

        // create crossRef
        PersonMovieCrossRef personMovieCrossRef = new PersonMovieCrossRef();
        personMovieCrossRef.setMovieId(movie.getId());
        personMovieCrossRef.setPersonId(person.getId());
        this.movieDAO.insertMovieWithPerson(personMovieCrossRef);

        // has crossRef
        MovieWithPersons movieWithPersons = this.movieDAO.getMovieWithPersons(movie.getId());
        Assert.assertEquals(movieWithPersons.getPersons().size(), 1);
        PersonWithMovies personWithMovies = this.personDAO.getPersonWithMovies(person.getId());
        Assert.assertEquals(personWithMovies.getMovies().size(), 1);

        // delete crossRef
        this.movieDAO.deleteMovieWithPerson(personMovieCrossRef);
        List<MovieWithPersons> moviesWithPersons = this.movieDAO.getAllMoviesWithPersons();
        Assert.assertEquals(moviesWithPersons.size(), 1);
        Assert.assertEquals(moviesWithPersons.get(0).getPersons().size(), 0);
        List<PersonWithMovies> personsWithMovies = this.personDAO.getAllPersonsWithMovies();
        Assert.assertEquals(personsWithMovies.size(), 1);
        Assert.assertEquals(personsWithMovies.get(0).getMovies().size(), 0);

        // delete data
        this.personDAO.deletePersons(person);
        this.movieDAO.deleteMovies(movie);
    }

    @Test
    public void testInsertAndAddCompany() {
        // insert movie
        Movie movie = new Movie();
        movie.setTitle(this.title);
        movie.setId(this.movieDAO.insertMovies(movie)[0]);

        // insert company
        Company company = new Company();
        company.setTitle(this.title);
        company.setId(this.companyDAO.insertCompanies(company)[0]);

        // create crossRef
        CompanyMovieCrossRef companyMovieCrossRef = new CompanyMovieCrossRef();
        companyMovieCrossRef.setMovieId(movie.getId());
        companyMovieCrossRef.setCompanyId(company.getId());
        this.movieDAO.insertMovieWithCompany(companyMovieCrossRef);

        // has crossRef
        MovieWithCompanies movieWithCompanies = this.movieDAO.getMovieWithCompanies(movie.getId());
        Assert.assertEquals(movieWithCompanies.getCompanies().size(), 1);
        CompanyWithMovies companyWithMovies = this.companyDAO.getCompanyWithMovies(company.getId());
        Assert.assertEquals(companyWithMovies.getMovies().size(), 1);

        // delete crossRef
        this.movieDAO.deleteMovieWithCompany(companyMovieCrossRef);
        List<MovieWithCompanies> moviesWithCompanies = this.movieDAO.getAllMoviesWithCompanies();
        Assert.assertEquals(moviesWithCompanies.size(), 1);
        Assert.assertEquals(moviesWithCompanies.get(0).getCompanies().size(), 0);
        List<CompanyWithMovies> companiesWithMovies = this.companyDAO.getAllCompaniesWithMovies();
        Assert.assertEquals(companiesWithMovies.size(), 1);
        Assert.assertEquals(companiesWithMovies.get(0).getMovies().size(), 0);

        // delete data
        this.companyDAO.deleteCompanies(company);
        this.movieDAO.deleteMovies(movie);
    }

    @Test
    public void testInsertAndAddTag() {
        // insert movie
        Movie movie = new Movie();
        movie.setTitle(this.title);
        movie.setId(this.movieDAO.insertMovies(movie)[0]);

        // insert tag
        Tag tag = new Tag();
        tag.setTitle(this.title);
        tag.setId(this.tagDAO.insertTags(tag)[0]);

        // create crossRef
        TagMovieCrossRef tagMovieCrossRef = new TagMovieCrossRef();
        tagMovieCrossRef.setMovieId(movie.getId());
        tagMovieCrossRef.setTagId(tag.getId());
        this.movieDAO.insertMovieWithTag(tagMovieCrossRef);

        // has crossRef
        MovieWithTags movieWithTags = this.movieDAO.getMovieWithTags(movie.getId());
        Assert.assertEquals(movieWithTags.getTags().size(), 1);
        TagWithMovies tagWithMovies = this.tagDAO.getTagWithMovies(tag.getId());
        Assert.assertEquals(tagWithMovies.getMovies().size(), 1);

        // delete crossRef
        this.movieDAO.deleteMovieWithTag(tagMovieCrossRef);
        List<MovieWithTags> albumsWithCompanies = this.movieDAO.getAllMoviesWithTags();
        Assert.assertEquals(albumsWithCompanies.size(), 1);
        Assert.assertEquals(albumsWithCompanies.get(0).getTags().size(), 0);
        List<TagWithMovies> companiesWithMovies = this.tagDAO.getAllTagsWithMovies();
        Assert.assertEquals(companiesWithMovies.size(), 1);
        Assert.assertEquals(companiesWithMovies.get(0).getMovies().size(), 0);

        // delete data
        this.tagDAO.deleteTags(tag);
        this.movieDAO.deleteMovies(movie);
    }
}
