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
import de.domjos.myarchivedatabase.model.general.company.CompanySongCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithSongs;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonSongCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonWithSongs;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagSongCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagWithSongs;
import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedatabase.model.media.song.SongWithCompanies;
import de.domjos.myarchivedatabase.model.media.song.SongWithPersons;
import de.domjos.myarchivedatabase.model.media.song.SongWithTags;
import de.domjos.myarchivedatabase.repository.general.CompanyDAO;
import de.domjos.myarchivedatabase.repository.general.PersonDAO;
import de.domjos.myarchivedatabase.repository.general.TagDAO;

@RunWith(AndroidJUnit4.class)

public class SongDAOTest {
    private SongDAO songDAO;
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
        this.songDAO = this.appDatabase.songDAO();
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

        // insert song
        Song song = new Song();
        song.setTitle(this.title);
        long id = this.songDAO.insertSongs(song)[0];

        // get song
        List<Song> songs = this.songDAO.getAllSongs();
        Assert.assertEquals(songs.size(), 1);
        song = this.songDAO.getSong(this.title);
        Assert.assertEquals(song.getTitle(), this.title);
        Assert.assertEquals(id, song.getId());

        // delete song
        this.songDAO.deleteSongs(song);
        songs = this.songDAO.getAllSongs();
        Assert.assertEquals(songs.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert song
        Song song = new Song();
        song.setTitle(this.title);
        long id = this.songDAO.insertSongs(song)[0];

        // get song
        List<Song> songs = this.songDAO.getAllSongs();
        Assert.assertEquals(songs.size(), 1);
        song = this.songDAO.getSong(this.title);
        Assert.assertEquals(song.getTitle(), this.title);
        Assert.assertEquals(id, song.getId());

        // update album
        String newTest = "Test2";
        song.setTitle(newTest);
        this.songDAO.updateSongs(song);
        song = this.songDAO.getSong(song.getId());
        Assert.assertEquals(song.getTitle(), newTest);

        // delete song
        this.songDAO.deleteSongs(song);
        songs = this.songDAO.getAllSongs();
        Assert.assertEquals(songs.size(), 0);
    }

    @Test
    public void testInsertAndAddPerson() {
        // insert album
        Song song = new Song();
        song.setTitle(this.title);
        song.setId(this.songDAO.insertSongs(song)[0]);

        // insert person
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setId(this.personDAO.insertPersons(person)[0]);

        // create crossRef
        PersonSongCrossRef songAlbumCrossRef = new PersonSongCrossRef();
        songAlbumCrossRef.setSongId(song.getId());
        songAlbumCrossRef.setPersonId(person.getId());
        this.songDAO.insertSongWithPerson(songAlbumCrossRef);

        // has crossRef
        SongWithPersons songWithPersons = this.songDAO.getSongWithPersons(song.getId());
        Assert.assertEquals(songWithPersons.getPersons().size(), 1);
        PersonWithSongs personWithSongs = this.personDAO.getPersonWithSongs(person.getId());
        Assert.assertEquals(personWithSongs.getSongs().size(), 1);

        // delete crossRef
        this.songDAO.deleteSongWithPerson(songAlbumCrossRef);
        List<SongWithPersons> songsWithPersons = this.songDAO.getAllSongsWithPersons();
        Assert.assertEquals(songsWithPersons.size(), 1);
        Assert.assertEquals(songsWithPersons.get(0).getPersons().size(), 0);
        List<PersonWithSongs> personsWithSongs = this.personDAO.getAllPersonsWithSongs();
        Assert.assertEquals(personsWithSongs.size(), 1);
        Assert.assertEquals(personsWithSongs.get(0).getSongs().size(), 0);

        // delete data
        this.personDAO.deletePersons(person);
        this.songDAO.deleteSongs(song);
    }

    @Test
    public void testInsertAndAddCompany() {
        // insert song
        Song song = new Song();
        song.setTitle(this.title);
        song.setId(this.songDAO.insertSongs(song)[0]);

        // insert company
        Company company = new Company();
        company.setTitle(this.title);
        company.setId(this.companyDAO.insertCompanies(company)[0]);

        // create crossRef
        CompanySongCrossRef companySongCrossRef = new CompanySongCrossRef();
        companySongCrossRef.setSongId(song.getId());
        companySongCrossRef.setCompanyId(company.getId());
        this.songDAO.insertSongWithCompany(companySongCrossRef);

        // has crossRef
        SongWithCompanies songWithCompanies = this.songDAO.getSongWithCompanies(song.getId());
        Assert.assertEquals(songWithCompanies.getCompanies().size(), 1);
        CompanyWithSongs companyWithAlbums = this.companyDAO.getCompanyWithSongs(company.getId());
        Assert.assertEquals(companyWithAlbums.getSongs().size(), 1);

        // delete crossRef
        this.songDAO.deleteSongWithCompany(companySongCrossRef);
        List<SongWithCompanies> songsWithCompanies = this.songDAO.getAllSongsWithCompanies();
        Assert.assertEquals(songsWithCompanies.size(), 1);
        Assert.assertEquals(songsWithCompanies.get(0).getCompanies().size(), 0);
        List<CompanyWithSongs> companiesWithSongs = this.companyDAO.getAllCompaniesWithSongs();
        Assert.assertEquals(companiesWithSongs.size(), 1);
        Assert.assertEquals(companiesWithSongs.get(0).getSongs().size(), 0);

        // delete data
        this.companyDAO.deleteCompanies(company);
        this.songDAO.deleteSongs(song);
    }

    @Test
    public void testInsertAndAddTag() {
        // insert song
        Song song = new Song();
        song.setTitle(this.title);
        song.setId(this.songDAO.insertSongs(song)[0]);

        // insert tag
        Tag tag = new Tag();
        tag.setTitle(this.title);
        tag.setId(this.tagDAO.insertTags(tag)[0]);

        // create crossRef
        TagSongCrossRef tagAlbumCrossRef = new TagSongCrossRef();
        tagAlbumCrossRef.setSongId(song.getId());
        tagAlbumCrossRef.setTagId(tag.getId());
        this.songDAO.insertSongWithTag(tagAlbumCrossRef);

        // has crossRef
        SongWithTags albumWithTags = this.songDAO.getSongWithTags(song.getId());
        Assert.assertEquals(albumWithTags.getTags().size(), 1);
        TagWithSongs tagWithSongs = this.tagDAO.getTagWithSongs(tag.getId());
        Assert.assertEquals(tagWithSongs.getSongs().size(), 1);

        // delete crossRef
        this.songDAO.deleteSongWithTag(tagAlbumCrossRef);
        List<SongWithTags> songsWithCompanies = this.songDAO.getAllSongsWithTags();
        Assert.assertEquals(songsWithCompanies.size(), 1);
        Assert.assertEquals(songsWithCompanies.get(0).getTags().size(), 0);
        List<TagWithSongs> tagsWithSongs = this.tagDAO.getAllTagsWithSongs();
        Assert.assertEquals(tagsWithSongs.size(), 1);
        Assert.assertEquals(tagsWithSongs.get(0).getSongs().size(), 0);

        // delete data
        this.tagDAO.deleteTags(tag);
        this.songDAO.deleteSongs(song);
    }
}
