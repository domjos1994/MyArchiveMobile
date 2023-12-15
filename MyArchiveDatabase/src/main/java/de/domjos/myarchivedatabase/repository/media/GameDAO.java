package de.domjos.myarchivedatabase.repository.media;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.myarchivedatabase.model.general.company.CompanyGameCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonGameCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagGameCrossRef;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.game.GameWithCompanies;
import de.domjos.myarchivedatabase.model.media.game.GameWithPersons;
import de.domjos.myarchivedatabase.model.media.game.GameWithTags;

@Dao
public interface GameDAO {
    @Query("SELECT * FROM games")
    List<Game> getAllGames();

    @Query("SELECT * FROM games WHERE id=:id")
    Game getGame(long id);

    @Query("SELECT * FROM games WHERE title=:title")
    Game getGame(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertGames(Game... games);

    @Update
    void updateGames(Game... games);

    @Delete
    void deleteGames(Game... games);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGameWithPerson(PersonGameCrossRef... personGameCrossRefs);

    @Delete
    void deleteGameWithPerson(PersonGameCrossRef... personGameCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGameWithCompany(CompanyGameCrossRef... companyGameCrossRefs);

    @Delete
    void deleteGameWithCompany(CompanyGameCrossRef... companyGameCrossRefs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGameWithTag(TagGameCrossRef... tagGameCrossRefs);

    @Delete
    void deleteGameWithTag(TagGameCrossRef... tagGameCrossRefs);

    @Transaction
    @Query("SELECT * FROM games")
    List<GameWithCompanies> getAllGamesWithCompanies();

    @Transaction
    @Query("SELECT * FROM games WHERE id=:id")
    GameWithCompanies getGameWithCompanies(long id);

    @Transaction
    @Query("SELECT * FROM games")
    List<GameWithPersons> getAllGamesWithPersons();

    @Transaction
    @Query("SELECT * FROM games WHERE id=:id")
    GameWithPersons getGameWithPersons(long id);

    @Transaction
    @Query("SELECT * FROM games")
    List<GameWithTags> getAllGamesWithTags();

    @Transaction
    @Query("SELECT * FROM games WHERE id=:id")
    GameWithTags getGameWithTags(long id);
}
