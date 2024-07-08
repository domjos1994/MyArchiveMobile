package de.domjos.myarchivedatabase.repository.media;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.CompanyMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagMovieCrossRef;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithCompanies;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithPersons;
import de.domjos.myarchivedatabase.model.media.movie.MovieWithTags;

@Dao
public interface MovieDAO {
    @Query("SELECT * FROM movies")
    List<Movie> getAllMovies();

    @Query("SELECT * FROM movies LIMIT :limit OFFSET :offset")
    List<Movie> getAllMovies(int limit, int offset);

    @Query("SELECT * FROM movies WHERE id=:id")
    Movie getMovie(long id);

    @Query("SELECT * FROM movies WHERE title=:title")
    Movie getMovie(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertMovies(Movie... movies);

    @Update
    void updateMovies(Movie... movies);

    @Delete
    void deleteMovies(Movie... movies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieWithPerson(PersonMovieCrossRef... personMovieCrossRefs);

    @Delete
    void deleteMovieWithPerson(PersonMovieCrossRef... personMovieCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieWithCompany(CompanyMovieCrossRef... companyMovieCrossRefs);

    @Delete
    void deleteMovieWithCompany(CompanyMovieCrossRef... companyMovieCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieWithTag(TagMovieCrossRef... tagMovieCrossRefs);

    @Delete
    void deleteMovieWithTag(TagMovieCrossRef... tagMovieCrossRefs);

    @Transaction
    @Query("SELECT * FROM movies")
    List<MovieWithCompanies> getAllMoviesWithCompanies();

    @Transaction
    @Query("SELECT * FROM movies WHERE id=:id")
    MovieWithCompanies getMovieWithCompanies(long id);

    @Transaction
    @Query("SELECT * FROM movies")
    List<MovieWithPersons> getAllMoviesWithPersons();

    @Transaction
    @Query("SELECT * FROM movies WHERE id=:id")
    MovieWithPersons getMovieWithPersons(long id);

    @Transaction
    @Query("SELECT * FROM movies")
    List<MovieWithTags> getAllMoviesWithTags();

    @Transaction
    @Query("SELECT * FROM movies WHERE id=:id")
    MovieWithTags getMovieWithTags(long id);
}
