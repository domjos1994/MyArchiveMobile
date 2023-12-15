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
import de.domjos.myarchivedatabase.model.general.company.CompanyGameCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithGames;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonGameCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonWithGames;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagGameCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagWithGames;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.game.GameWithCompanies;
import de.domjos.myarchivedatabase.model.media.game.GameWithPersons;
import de.domjos.myarchivedatabase.model.media.game.GameWithTags;
import de.domjos.myarchivedatabase.repository.general.CompanyDAO;
import de.domjos.myarchivedatabase.repository.general.PersonDAO;
import de.domjos.myarchivedatabase.repository.general.TagDAO;

@RunWith(AndroidJUnit4.class)
public class GameDAOTest {
    private GameDAO gameDAO;
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
        this.gameDAO = this.appDatabase.gameDAO();
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

        // insert game
        Game game = new Game();
        game.setTitle(this.title);
        long id = this.gameDAO.insertGames(game)[0];

        // get game
        List<Game> games = this.gameDAO.getAllGames();
        Assert.assertEquals(games.size(), 1);
        game = this.gameDAO.getGame(this.title);
        Assert.assertEquals(game.getTitle(), this.title);
        Assert.assertEquals(id, game.getId());

        // delete game
        this.gameDAO.deleteGames(game);
        games = this.gameDAO.getAllGames();
        Assert.assertEquals(games.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert game
        Game game = new Game();
        game.setTitle(this.title);
        long id = this.gameDAO.insertGames(game)[0];

        // get game
        List<Game> games = this.gameDAO.getAllGames();
        Assert.assertEquals(games.size(), 1);
        game = this.gameDAO.getGame(this.title);
        Assert.assertEquals(game.getTitle(), this.title);
        Assert.assertEquals(id, game.getId());

        // update album
        String newTest = "Test2";
        game.setTitle(newTest);
        this.gameDAO.updateGames(game);
        game = this.gameDAO.getGame(game.getId());
        Assert.assertEquals(game.getTitle(), newTest);

        // delete game
        this.gameDAO.deleteGames(game);
        games = this.gameDAO.getAllGames();
        Assert.assertEquals(games.size(), 0);
    }

    @Test
    public void testInsertAndAddPerson() {
        // insert game
        Game game = new Game();
        game.setTitle(this.title);
        game.setId(this.gameDAO.insertGames(game)[0]);

        // insert person
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setId(this.personDAO.insertPersons(person)[0]);

        // create crossRef
        PersonGameCrossRef personGameCrossRef = new PersonGameCrossRef();
        personGameCrossRef.setGameId(game.getId());
        personGameCrossRef.setPersonId(person.getId());
        this.gameDAO.insertGameWithPerson(personGameCrossRef);

        // has crossRef
        GameWithPersons gameWithPersons = this.gameDAO.getGameWithPersons(game.getId());
        Assert.assertEquals(gameWithPersons.getPersons().size(), 1);
        PersonWithGames personWithGames = this.personDAO.getPersonWithGames(person.getId());
        Assert.assertEquals(personWithGames.getGames().size(), 1);

        // delete crossRef
        this.gameDAO.deleteGameWithPerson(personGameCrossRef);
        List<GameWithPersons> gamesWithPersons = this.gameDAO.getAllGamesWithPersons();
        Assert.assertEquals(gamesWithPersons.size(), 1);
        Assert.assertEquals(gamesWithPersons.get(0).getPersons().size(), 0);
        List<PersonWithGames> personsWithGames = this.personDAO.getAllPersonsWithGames();
        Assert.assertEquals(personsWithGames.size(), 1);
        Assert.assertEquals(personsWithGames.get(0).getGames().size(), 0);

        // delete data
        this.personDAO.deletePersons(person);
        this.gameDAO.deleteGames(game);
    }

    @Test
    public void testInsertAndAddCompany() {
        // insert game
        Game game = new Game();
        game.setTitle(this.title);
        game.setId(this.gameDAO.insertGames(game)[0]);

        // insert company
        Company company = new Company();
        company.setTitle(this.title);
        company.setId(this.companyDAO.insertCompanies(company)[0]);

        // create crossRef
        CompanyGameCrossRef companyGameCrossRef = new CompanyGameCrossRef();
        companyGameCrossRef.setGameId(game.getId());
        companyGameCrossRef.setCompanyId(company.getId());
        this.gameDAO.insertGameWithCompany(companyGameCrossRef);

        // has crossRef
        GameWithCompanies gameWithCompanies = this.gameDAO.getGameWithCompanies(game.getId());
        Assert.assertEquals(gameWithCompanies.getCompanies().size(), 1);
        CompanyWithGames companyWithGames = this.companyDAO.getCompanyWithGames(company.getId());
        Assert.assertEquals(companyWithGames.getGames().size(), 1);

        // delete crossRef
        this.gameDAO.deleteGameWithCompany(companyGameCrossRef);
        List<GameWithCompanies> gamesWithCompanies = this.gameDAO.getAllGamesWithCompanies();
        Assert.assertEquals(gamesWithCompanies.size(), 1);
        Assert.assertEquals(gamesWithCompanies.get(0).getCompanies().size(), 0);
        List<CompanyWithGames> companiesWithGames = this.companyDAO.getAllCompaniesWithGames();
        Assert.assertEquals(companiesWithGames.size(), 1);
        Assert.assertEquals(companiesWithGames.get(0).getGames().size(), 0);

        // delete data
        this.companyDAO.deleteCompanies(company);
        this.gameDAO.deleteGames(game);
    }

    @Test
    public void testInsertAndAddTag() {
        // insert game
        Game game = new Game();
        game.setTitle(this.title);
        game.setId(this.gameDAO.insertGames(game)[0]);

        // insert tag
        Tag tag = new Tag();
        tag.setTitle(this.title);
        tag.setId(this.tagDAO.insertTags(tag)[0]);

        // create crossRef
        TagGameCrossRef tagGameCrossRef = new TagGameCrossRef();
        tagGameCrossRef.setGameId(game.getId());
        tagGameCrossRef.setTagId(tag.getId());
        this.gameDAO.insertGameWithTag(tagGameCrossRef);

        // has crossRef
        GameWithTags gameWithTags = this.gameDAO.getGameWithTags(game.getId());
        Assert.assertEquals(gameWithTags.getTags().size(), 1);
        TagWithGames tagWithGames = this.tagDAO.getTagWithGames(tag.getId());
        Assert.assertEquals(tagWithGames.getGames().size(), 1);

        // delete crossRef
        this.gameDAO.deleteGameWithTag(tagGameCrossRef);
        List<GameWithTags> albumsWithCompanies = this.gameDAO.getAllGamesWithTags();
        Assert.assertEquals(albumsWithCompanies.size(), 1);
        Assert.assertEquals(albumsWithCompanies.get(0).getTags().size(), 0);
        List<TagWithGames> companiesWithGames = this.tagDAO.getAllTagsWithGames();
        Assert.assertEquals(companiesWithGames.size(), 1);
        Assert.assertEquals(companiesWithGames.get(0).getGames().size(), 0);

        // delete data
        this.tagDAO.deleteTags(tag);
        this.gameDAO.deleteGames(game);
    }
}
