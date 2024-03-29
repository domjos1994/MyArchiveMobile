package de.domjos.myarchivedatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.domjos.myarchivedatabase.converter.BitmapConverter;
import de.domjos.myarchivedatabase.converter.DateConverter;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.fileTree.FileTree;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFile;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileTagCrossRef;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeTagCrossRef;
import de.domjos.myarchivedatabase.model.filter.Filter;
import de.domjos.myarchivedatabase.model.filter.FilterCategoryCrossRef;
import de.domjos.myarchivedatabase.model.filter.FilterTagCrossRef;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyBookCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyGameCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanySongCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValue;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueBookCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueFilterCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueGameCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonBookCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonGameCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonSongCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagBookCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagGameCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagSongCrossRef;
import de.domjos.myarchivedatabase.model.library.Library;
import de.domjos.myarchivedatabase.model.library.LibraryAlbumCrossRef;
import de.domjos.myarchivedatabase.model.library.LibraryBookCrossRef;
import de.domjos.myarchivedatabase.model.library.LibraryGameCrossRef;
import de.domjos.myarchivedatabase.model.library.LibraryMovieCrossRef;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedatabase.model.media.song.SongAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagAlbumCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaList;
import de.domjos.myarchivedatabase.model.mediaList.MediaListAlbumCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListBookCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListGameCrossRef;
import de.domjos.myarchivedatabase.model.mediaList.MediaListMovieCrossRef;
import de.domjos.myarchivedatabase.repository.FilterDAO;
import de.domjos.myarchivedatabase.repository.LibraryDAO;
import de.domjos.myarchivedatabase.repository.MediaListDAO;
import de.domjos.myarchivedatabase.repository.ViewsDAO;
import de.domjos.myarchivedatabase.repository.fileTree.FileTreeDAO;
import de.domjos.myarchivedatabase.repository.fileTree.FileTreeFileDAO;
import de.domjos.myarchivedatabase.repository.general.CategoryDAO;
import de.domjos.myarchivedatabase.repository.general.CompanyDAO;
import de.domjos.myarchivedatabase.repository.general.CustomFieldDAO;
import de.domjos.myarchivedatabase.repository.general.CustomFieldValueDAO;
import de.domjos.myarchivedatabase.repository.general.PersonDAO;
import de.domjos.myarchivedatabase.repository.general.TagDAO;
import de.domjos.myarchivedatabase.repository.media.AlbumDAO;
import de.domjos.myarchivedatabase.repository.media.BookDAO;
import de.domjos.myarchivedatabase.repository.media.GameDAO;
import de.domjos.myarchivedatabase.repository.media.MovieDAO;
import de.domjos.myarchivedatabase.repository.media.SongDAO;
import de.domjos.myarchivedatabase.views.CustomFieldWithMediaAndValue;
import de.domjos.myarchivedatabase.views.Media;

@Database(entities = {
        Category.class, Tag.class, Person.class, Company.class, CustomField.class, CustomFieldValue.class,
        Album.class, Song.class, Movie.class, Book.class, Game.class,
        Library.class, MediaList.class, Filter.class,
        CompanyAlbumCrossRef.class, CompanySongCrossRef.class, CompanyMovieCrossRef.class,
        CompanyBookCrossRef.class, CompanyGameCrossRef.class,
        PersonAlbumCrossRef.class, PersonSongCrossRef.class, PersonMovieCrossRef.class,
        PersonBookCrossRef.class, PersonGameCrossRef.class,
        TagAlbumCrossRef.class, TagSongCrossRef.class, TagMovieCrossRef.class, TagBookCrossRef.class,
        TagGameCrossRef.class,
        CustomFieldValueBookCrossRef.class, CustomFieldValueAlbumCrossRef.class, CustomFieldValueMovieCrossRef.class,
        CustomFieldValueGameCrossRef.class, CustomFieldValueFilterCrossRef.class,
        SongAlbumCrossRef.class,
        LibraryAlbumCrossRef.class, LibraryBookCrossRef.class, LibraryGameCrossRef.class, LibraryMovieCrossRef.class,
        MediaListAlbumCrossRef.class, MediaListBookCrossRef.class, MediaListGameCrossRef.class, MediaListMovieCrossRef.class,
        FilterCategoryCrossRef.class, FilterTagCrossRef.class,
        FileTree.class, FileTreeFile.class, FileTreeTagCrossRef.class, FileTreeFileTagCrossRef.class
}, views = {
        CustomFieldWithMediaAndValue.class, Media.class
}, version = 14, exportSchema = false)
@TypeConverters({DateConverter.class, BitmapConverter.class, DrawableConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoryDAO categoryDAO();
    public abstract TagDAO tagDAO();
    public abstract PersonDAO personDAO();
    public abstract CompanyDAO companyDAO();
    public abstract AlbumDAO albumDAO();
    public abstract SongDAO songDAO();
    public abstract MovieDAO movieDAO();
    public abstract BookDAO bookDAO();
    public abstract GameDAO gameDAO();
    public abstract LibraryDAO libraryDAO();
    public abstract MediaListDAO mediaListDAO();
    public abstract CustomFieldDAO customFieldDAO();
    public abstract CustomFieldValueDAO customFieldValueDAO();
    public abstract FilterDAO filterDAO();
    public abstract FileTreeDAO fileTreeDAO();
    public abstract FileTreeFileDAO fileTreeFileDAO();
    public abstract ViewsDAO viewsDAO();
}

