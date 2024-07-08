package de.domjos.myarchivedatabase.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonWithAlbums;
import de.domjos.myarchivedatabase.model.general.person.PersonWithBooks;
import de.domjos.myarchivedatabase.model.general.person.PersonWithGames;
import de.domjos.myarchivedatabase.model.general.person.PersonWithMovies;
import de.domjos.myarchivedatabase.model.general.person.PersonWithSongs;

@Dao
public interface PersonDAO {
    @Query("SELECT * FROM persons")
    List<Person> getAllPersons();

    @Query("SELECT * FROM persons WHERE id=:id")
    Person getPerson(long id);

    @Query("SELECT * FROM persons WHERE firstName=:firstName and lastName=:lastName")
    Person getPerson(String firstName, String lastName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertPersons(Person... persons);

    @Update
    void updatePersons(Person... persons);

    @Delete
    void deletePersons(Person... persons);

    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithAlbums> getAllPersonsWithAlbums();

    @Transaction
    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithAlbums getPersonWithAlbums(long id);

    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithSongs> getAllPersonsWithSongs();

    @Transaction
    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithSongs getPersonWithSongs(long id);

    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithMovies> getAllPersonsWithMovies();

    @Transaction
    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithMovies getPersonWithMovies(long id);

    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithBooks> getAllPersonsWithBooks();

    @Transaction
    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithBooks getPersonWithBooks(long id);

    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithGames> getAllPersonsWithGames();

    @Transaction
    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithGames getPersonWithGames(long id);
}
