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
import de.domjos.myarchivedatabase.model.general.company.CompanyAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyWithAlbums;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonWithAlbums;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagWithAlbums;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithCompanies;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithPersons;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithSongs;
import de.domjos.myarchivedatabase.model.media.album.AlbumWithTags;
import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedatabase.model.media.song.SongAlbumCrossRef;
import de.domjos.myarchivedatabase.model.media.song.SongWithAlbums;
import de.domjos.myarchivedatabase.repository.general.CompanyDAO;
import de.domjos.myarchivedatabase.repository.general.PersonDAO;
import de.domjos.myarchivedatabase.repository.general.TagDAO;

@RunWith(AndroidJUnit4.class)

public class AlbumDAOTest {
    private AlbumDAO albumDAO;
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
        this.albumDAO = this.appDatabase.albumDAO();
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

        // insert album
        Album album = new Album();
        album.setTitle(this.title);
        long id = this.albumDAO.insertAlbums(album)[0];

        // get album
        List<Album> albums = this.albumDAO.getAllAlbums();
        Assert.assertEquals(albums.size(), 1);
        album = this.albumDAO.getAlbum(this.title);
        Assert.assertEquals(album.getTitle(), this.title);
        Assert.assertEquals(id, album.getId());

        // delete album
        this.albumDAO.deleteAlbums(album);
        albums = this.albumDAO.getAllAlbums();
        Assert.assertEquals(albums.size(), 0);
    }

    @Test
    public void testInsertUpdateAndDelete() {

        // insert album
        Album album = new Album();
        album.setTitle(this.title);
        long id = this.albumDAO.insertAlbums(album)[0];

        // get album
        List<Album> albums = this.albumDAO.getAllAlbums();
        Assert.assertEquals(albums.size(), 1);
        album = this.albumDAO.getAlbum(this.title);
        Assert.assertEquals(album.getTitle(), this.title);
        Assert.assertEquals(id, album.getId());

        // update album
        String newTest = "Test2";
        album.setTitle(newTest);
        this.albumDAO.updateAlbums(album);
        album = this.albumDAO.getAlbum(album.getId());
        Assert.assertEquals(album.getTitle(), newTest);

        // delete album
        this.albumDAO.deleteAlbums(album);
        albums = this.albumDAO.getAllAlbums();
        Assert.assertEquals(albums.size(), 0);
    }

    @Test
    public void testInsertAndAddSong() {
        // insert album
        Album album = new Album();
        album.setTitle(this.title);
        album.setId(this.albumDAO.insertAlbums(album)[0]);

        // insert song
        Song song = new Song();
        song.setTitle(this.title);
        song.setId(this.songDAO.insertSongs(song)[0]);

        // create crossRef
        SongAlbumCrossRef songAlbumCrossRef = new SongAlbumCrossRef();
        songAlbumCrossRef.setAlbumId(album.getId());
        songAlbumCrossRef.setSongId(song.getId());
        this.albumDAO.insertAlbumWithSong(songAlbumCrossRef);

        // has crossRef
        AlbumWithSongs albumWithSongs = this.albumDAO.getAlbumWithSongs(album.getId());
        Assert.assertEquals(albumWithSongs.getSongs().size(), 1);
        SongWithAlbums songWithAlbums = this.songDAO.getSongWithAlbums(song.getId());
        Assert.assertEquals(songWithAlbums.getAlbums().size(), 1);

        // delete crossRef
        this.albumDAO.deleteAlbumWithSong(songAlbumCrossRef);
        List<AlbumWithSongs> albumsWithSongs = this.albumDAO.getAllAlbumsWithSongs();
        Assert.assertEquals(albumsWithSongs.size(), 1);
        Assert.assertEquals(albumsWithSongs.get(0).getSongs().size(), 0);
        List<SongWithAlbums> songsWithAlbums = this.songDAO.getAllSongsWithAlbums();
        Assert.assertEquals(songsWithAlbums.size(), 1);
        Assert.assertEquals(songsWithAlbums.get(0).getAlbums().size(), 0);

        // delete data
        this.songDAO.deleteSongs(song);
        this.albumDAO.deleteAlbums(album);
    }

    @Test
    public void testInsertAndAddPerson() {
        // insert album
        Album album = new Album();
        album.setTitle(this.title);
        album.setId(this.albumDAO.insertAlbums(album)[0]);

        // insert person
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setId(this.personDAO.insertPersons(person)[0]);

        // create crossRef
        PersonAlbumCrossRef personAlbumCrossRef = new PersonAlbumCrossRef();
        personAlbumCrossRef.setAlbumId(album.getId());
        personAlbumCrossRef.setPersonId(person.getId());
        this.albumDAO.insertAlbumWithPerson(personAlbumCrossRef);

        // has crossRef
        AlbumWithPersons albumWithPersons = this.albumDAO.getAlbumWithPersons(album.getId());
        Assert.assertEquals(albumWithPersons.getPersons().size(), 1);
        PersonWithAlbums personWithAlbums = this.personDAO.getPersonWithAlbums(person.getId());
        Assert.assertEquals(personWithAlbums.getAlbums().size(), 1);

        // delete crossRef
        this.albumDAO.deleteAlbumWithPerson(personAlbumCrossRef);
        List<AlbumWithPersons> albumsWithPersons = this.albumDAO.getAllAlbumsWithPersons();
        Assert.assertEquals(albumsWithPersons.size(), 1);
        Assert.assertEquals(albumsWithPersons.get(0).getPersons().size(), 0);
        List<PersonWithAlbums> personsWithAlbums = this.personDAO.getAllPersonsWithAlbums();
        Assert.assertEquals(personsWithAlbums.size(), 1);
        Assert.assertEquals(personsWithAlbums.get(0).getAlbums().size(), 0);

        // delete data
        this.personDAO.deletePersons(person);
        this.albumDAO.deleteAlbums(album);
    }

    @Test
    public void testInsertAndAddCompany() {
        // insert album
        Album album = new Album();
        album.setTitle(this.title);
        album.setId(this.albumDAO.insertAlbums(album)[0]);

        // insert company
        Company company = new Company();
        company.setTitle(this.title);
        company.setId(this.companyDAO.insertCompanies(company)[0]);

        // create crossRef
        CompanyAlbumCrossRef companyAlbumCrossRef = new CompanyAlbumCrossRef();
        companyAlbumCrossRef.setAlbumId(album.getId());
        companyAlbumCrossRef.setCompanyId(company.getId());
        this.albumDAO.insertAlbumWithCompany(companyAlbumCrossRef);

        // has crossRef
        AlbumWithCompanies albumWithCompanies = this.albumDAO.getAlbumWithCompanies(album.getId());
        Assert.assertEquals(albumWithCompanies.getCompanies().size(), 1);
        CompanyWithAlbums companyWithAlbums = this.companyDAO.getCompanyWithAlbums(company.getId());
        Assert.assertEquals(companyWithAlbums.getAlbums().size(), 1);

        // delete crossRef
        this.albumDAO.deleteAlbumWithCompany(companyAlbumCrossRef);
        List<AlbumWithCompanies> albumsWithCompanies = this.albumDAO.getAllAlbumsWithCompanies();
        Assert.assertEquals(albumsWithCompanies.size(), 1);
        Assert.assertEquals(albumsWithCompanies.get(0).getCompanies().size(), 0);
        List<CompanyWithAlbums> companiesWithAlbums = this.companyDAO.getAllCompaniesWithAlbums();
        Assert.assertEquals(companiesWithAlbums.size(), 1);
        Assert.assertEquals(companiesWithAlbums.get(0).getAlbums().size(), 0);

        // delete data
        this.companyDAO.deleteCompanies(company);
        this.albumDAO.deleteAlbums(album);
    }

    @Test
    public void testInsertAndAddTag() {
        // insert album
        Album album = new Album();
        album.setTitle(this.title);
        album.setId(this.albumDAO.insertAlbums(album)[0]);

        // insert tag
        Tag tag = new Tag();
        tag.setTitle(this.title);
        tag.setId(this.tagDAO.insertTags(tag)[0]);

        // create crossRef
        TagAlbumCrossRef tagAlbumCrossRef = new TagAlbumCrossRef();
        tagAlbumCrossRef.setAlbumId(album.getId());
        tagAlbumCrossRef.setTagId(tag.getId());
        this.albumDAO.insertAlbumWithTag(tagAlbumCrossRef);

        // has crossRef
        AlbumWithTags albumWithTags = this.albumDAO.getAlbumWithTags(album.getId());
        Assert.assertEquals(albumWithTags.getTags().size(), 1);
        TagWithAlbums tagWithAlbums = this.tagDAO.getTagWithAlbums(tag.getId());
        Assert.assertEquals(tagWithAlbums.getAlbums().size(), 1);

        // delete crossRef
        this.albumDAO.deleteAlbumWithTag(tagAlbumCrossRef);
        List<AlbumWithTags> albumsWithCompanies = this.albumDAO.getAllAlbumsWithTags();
        Assert.assertEquals(albumsWithCompanies.size(), 1);
        Assert.assertEquals(albumsWithCompanies.get(0).getTags().size(), 0);
        List<TagWithAlbums> companiesWithAlbums = this.tagDAO.getAllTagsWithAlbums();
        Assert.assertEquals(companiesWithAlbums.size(), 1);
        Assert.assertEquals(companiesWithAlbums.get(0).getAlbums().size(), 0);

        // delete data
        this.tagDAO.deleteTags(tag);
        this.albumDAO.deleteAlbums(album);
    }
}
