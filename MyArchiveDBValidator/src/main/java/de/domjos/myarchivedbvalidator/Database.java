package de.domjos.myarchivedbvalidator;


import android.content.Context;

import androidx.room.Room;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileTagCrossRef;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeTagCrossRef;
import de.domjos.myarchivedatabase.model.filter.FilterTagCrossRef;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.company.CompanyAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyBookCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyGameCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanyMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.company.CompanySongCrossRef;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonBookCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonGameCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.person.PersonSongCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagAlbumCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagBookCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagGameCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagMovieCrossRef;
import de.domjos.myarchivedatabase.model.general.tag.TagSongCrossRef;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.media.song.Song;
import de.domjos.myarchivedatabase.model.media.song.SongAlbumCrossRef;
import de.domjos.myarchivedbvalidator.exceptions.DuplicatedException;
import de.domjos.myarchivedbvalidator.exceptions.LengthMinException;
import de.domjos.myarchivedbvalidator.exceptions.LengthMinMaxException;
import de.domjos.myarchivedbvalidator.validation.DuplicatedValidator;
import de.domjos.myarchivedbvalidator.validation.LengthValidator;

/**
 * Class to connect App with SQLite-Database
 * @author Dominic Joas
 * @version 1.0
 */
public final class Database {
    public final String MEDIA_ALBUM = "album";
    public final String MEDIA_SONG = "song";
    public final String MEDIA_BOOK = "book";
    public final String MEDIA_GAME = "game";
    public final String MEDIA_MOVIE = "movie";
    public final String FILTER = "filter";
    public final String FILE_TREE = "file_tree";
    public final String FILE_TREE_FILE = "file_tree_file";

    private final Context context;
    private final AppDatabase appDatabase;
    private StringBuilder messages = new StringBuilder();

    public Database(Context context) {
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();

        this.context = context;
    }

    public void close() {
        this.appDatabase.close();
    }

    public List<Album> getAlbums() {
        List<Album> albums = new LinkedList<>();
        try {
            this.messages = new StringBuilder();

            albums.addAll(this.appDatabase.albumDAO().getAllAlbums());

            albums.forEach(album -> {
                album.setCategoryItem(this.appDatabase.categoryDAO().getCategory(album.getId()));
                this.getPersons(album.getId(), MEDIA_ALBUM);
                this.getCompanies(album.getId(), MEDIA_ALBUM);
                this.getTags(album.getId(), MEDIA_ALBUM);
            });
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
        return albums;
    }

    public void insertAlbum(Album album) {
        try {
            this.messages = new StringBuilder();

            DuplicatedValidator duplicatedValidator = new DuplicatedValidator(album, this.appDatabase);
            if(!duplicatedValidator.validate()) {
                throw new DuplicatedException(this.context, album.getTitle(), album);
            }
            LengthValidator titleValidator = new LengthValidator(album.getTitle(), 2, 255);
            if(!titleValidator.validate()) {
                throw new LengthMinException(this.context, 2);
            }
            LengthValidator isbnValidator = new LengthValidator(album.getCode(), 13);
            if(!isbnValidator.validate()) {
                throw new LengthMinMaxException(this.context, 10, 13);
            }

            album.setCategory(this.insertCategory(album.getCategoryItem()));
            album.getCategoryItem().setId(album.getCategory());

            if(album.getId() != 0) {
                this.appDatabase.albumDAO().updateAlbums(album);
            } else {
                album.setId(this.appDatabase.albumDAO().insertAlbums(album)[0]);
            }

            this.insertTags(album.getId(), MEDIA_ALBUM, album.getTags().toArray(new Tag[0]));
            this.insertPersons(album.getId(), MEDIA_ALBUM, album.getPersons().toArray(new Person[0]));
            this.insertCompanies(album.getId(), MEDIA_ALBUM, album.getCompanies().toArray(new Company[0]));
            album.getSongs().forEach(song -> insertSong(song, album.getId()));
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
    }

    public void deleteAlbum(Album album) {
        if(album.getCategoryItem() != null) {
            this.appDatabase.categoryDAO().deleteCategories(album.getCategoryItem());
        }
        this.deletePersons(MEDIA_ALBUM, album.getId());
        this.deleteCompanies(MEDIA_ALBUM, album.getId());
        this.deleteTags(MEDIA_ALBUM, album.getId());

        this.appDatabase.albumDAO().deleteAlbums(album);
    }

    public List<Song> getSongs() {
        List<Song> songs = new LinkedList<>();
        try {
            this.messages = new StringBuilder();

            songs.addAll(this.appDatabase.songDAO().getAllSongs());

            songs.forEach(song -> {
                song.setCategoryItem(this.appDatabase.categoryDAO().getCategory(song.getId()));
                this.getPersons(song.getId(), MEDIA_SONG);
                this.getCompanies(song.getId(), MEDIA_SONG);
                this.getTags(song.getId(), MEDIA_SONG);
            });
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
        return songs;
    }

    public void insertSong(Song song, long id) {
        try {
            this.messages = new StringBuilder();

            DuplicatedValidator duplicatedValidator = new DuplicatedValidator(song, this.appDatabase);
            if(!duplicatedValidator.validate()) {
                throw new DuplicatedException(this.context, song.getTitle(), song);
            }
            LengthValidator titleValidator = new LengthValidator(song.getTitle(), 2, 255);
            if(!titleValidator.validate()) {
                throw new LengthMinException(this.context, 2);
            }
            LengthValidator isbnValidator = new LengthValidator(song.getCode(), 13);
            if(!isbnValidator.validate()) {
                throw new LengthMinMaxException(this.context, 10, 13);
            }

            song.setCategory(this.insertCategory(song.getCategoryItem()));
            song.getCategoryItem().setId(song.getCategory());

            if(song.getId() != 0) {
                this.appDatabase.songDAO().updateSongs(song);
            } else {
                song.setId(this.appDatabase.songDAO().insertSongs(song)[0]);
            }

            this.insertTags(song.getId(), MEDIA_SONG, song.getTags().toArray(new Tag[0]));
            this.insertPersons(song.getId(), MEDIA_SONG, song.getPersons().toArray(new Person[0]));
            this.insertCompanies(song.getId(), MEDIA_SONG, song.getCompanies().toArray(new Company[0]));

            if(id != 0) {
                SongAlbumCrossRef songAlbumCrossRef = new SongAlbumCrossRef();
                songAlbumCrossRef.setAlbumId(id);
                songAlbumCrossRef.setSongId(song.getId());
                this.appDatabase.albumDAO().insertAlbumWithSong(songAlbumCrossRef);
            }
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
    }

    public void deleteSong(Song song) {
        if(song.getCategoryItem() != null) {
            this.appDatabase.categoryDAO().deleteCategories(song.getCategoryItem());
        }
        this.deletePersons(MEDIA_SONG, song.getId());
        this.deleteCompanies(MEDIA_SONG, song.getId());
        this.deleteTags(MEDIA_SONG, song.getId());

        this.appDatabase.songDAO().deleteSongs(song);
    }

    public List<Book> getBooks() {
        List<Book> books = new LinkedList<>();
        try {
            this.messages = new StringBuilder();

            books.addAll(this.appDatabase.bookDAO().getAllBooks());

            books.forEach(album -> {
                album.setCategoryItem(this.appDatabase.categoryDAO().getCategory(album.getId()));
                this.getPersons(album.getId(), MEDIA_BOOK);
                this.getCompanies(album.getId(), MEDIA_BOOK);
                this.getTags(album.getId(), MEDIA_BOOK);
            });
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
        return books;
    }

    public void insertBook(Book book) {
        try {
            this.messages = new StringBuilder();

            DuplicatedValidator duplicatedValidator = new DuplicatedValidator(book, this.appDatabase);
            if(!duplicatedValidator.validate()) {
                throw new DuplicatedException(this.context, book.getTitle(), book);
            }
            LengthValidator titleValidator = new LengthValidator(book.getTitle(), 2, 255);
            if(!titleValidator.validate()) {
                throw new LengthMinException(this.context, 2);
            }
            LengthValidator isbnValidator = new LengthValidator(book.getCode(), 13);
            if(!isbnValidator.validate()) {
                throw new LengthMinMaxException(this.context, 10, 13);
            }

            book.setCategory(this.insertCategory(book.getCategoryItem()));
            book.getCategoryItem().setId(book.getCategory());

            if(book.getId() != 0) {
                this.appDatabase.bookDAO().updateBooks(book);
            } else {
                book.setId(this.appDatabase.bookDAO().insertBooks(book)[0]);
            }

            this.insertTags(book.getId(), MEDIA_BOOK, book.getTags().toArray(new Tag[0]));
            this.insertPersons(book.getId(), MEDIA_BOOK, book.getPersons().toArray(new Person[0]));
            this.insertCompanies(book.getId(), MEDIA_BOOK, book.getCompanies().toArray(new Company[0]));
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
    }

    public void deleteBook(Book book) {
        if(book.getCategoryItem() != null) {
            this.appDatabase.categoryDAO().deleteCategories(book.getCategoryItem());
        }
        this.deletePersons(MEDIA_BOOK, book.getId());
        this.deleteCompanies(MEDIA_BOOK, book.getId());
        this.deleteTags(MEDIA_BOOK, book.getId());

        this.appDatabase.bookDAO().deleteBooks(book);
    }

    public List<Game> getGames() {
        List<Game> games = new LinkedList<>();
        try {
            this.messages = new StringBuilder();

            games.addAll(this.appDatabase.gameDAO().getAllGames());

            games.forEach(game -> {
                game.setCategoryItem(this.appDatabase.categoryDAO().getCategory(game.getId()));
                this.getPersons(game.getId(), MEDIA_GAME);
                this.getCompanies(game.getId(), MEDIA_GAME);
                this.getTags(game.getId(), MEDIA_GAME);
            });
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
        return games;
    }

    public void insertGame(Game game) {
        try {
            this.messages = new StringBuilder();

            DuplicatedValidator duplicatedValidator = new DuplicatedValidator(game, this.appDatabase);
            if(!duplicatedValidator.validate()) {
                throw new DuplicatedException(this.context, game.getTitle(), game);
            }
            LengthValidator titleValidator = new LengthValidator(game.getTitle(), 2, 255);
            if(!titleValidator.validate()) {
                throw new LengthMinException(this.context, 2);
            }
            LengthValidator isbnValidator = new LengthValidator(game.getCode(), 13);
            if(!isbnValidator.validate()) {
                throw new LengthMinMaxException(this.context, 10, 13);
            }

            game.setCategory(this.insertCategory(game.getCategoryItem()));
            game.getCategoryItem().setId(game.getCategory());

            if(game.getId() != 0) {
                this.appDatabase.gameDAO().updateGames(game);
            } else {
                game.setId(this.appDatabase.gameDAO().insertGames(game)[0]);
            }

            this.insertTags(game.getId(), MEDIA_GAME, game.getTags().toArray(new Tag[0]));
            this.insertPersons(game.getId(), MEDIA_GAME, game.getPersons().toArray(new Person[0]));
            this.insertCompanies(game.getId(), MEDIA_GAME, game.getCompanies().toArray(new Company[0]));
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
    }

    public void deleteGame(Game game) {
        if(game.getCategoryItem() != null) {
            this.appDatabase.categoryDAO().deleteCategories(game.getCategoryItem());
        }
        this.deletePersons(MEDIA_GAME, game.getId());
        this.deleteCompanies(MEDIA_GAME, game.getId());
        this.deleteTags(MEDIA_GAME, game.getId());

        this.appDatabase.gameDAO().deleteGames(game);
    }

    public List<Movie> getMovies() {
        List<Movie> movies = new LinkedList<>();
        try {
            this.messages = new StringBuilder();

            movies.addAll(this.appDatabase.movieDAO().getAllMovies());

            movies.forEach(movie -> {
                movie.setCategoryItem(this.appDatabase.categoryDAO().getCategory(movie.getId()));
                this.getPersons(movie.getId(), MEDIA_MOVIE);
                this.getCompanies(movie.getId(), MEDIA_MOVIE);
                this.getTags(movie.getId(), MEDIA_MOVIE);
            });
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
        return movies;
    }

    public void insertMovie(Movie movie) {
        try {
            this.messages = new StringBuilder();

            DuplicatedValidator duplicatedValidator = new DuplicatedValidator(movie, this.appDatabase);
            if(!duplicatedValidator.validate()) {
                throw new DuplicatedException(this.context, movie.getTitle(), movie);
            }
            LengthValidator titleValidator = new LengthValidator(movie.getTitle(), 2, 255);
            if(!titleValidator.validate()) {
                throw new LengthMinException(this.context, 2);
            }
            LengthValidator isbnValidator = new LengthValidator(movie.getCode(), 13);
            if(!isbnValidator.validate()) {
                throw new LengthMinMaxException(this.context, 10, 13);
            }

            movie.setCategory(this.insertCategory(movie.getCategoryItem()));
            movie.getCategoryItem().setId(movie.getCategory());

            if(movie.getId() != 0) {
                this.appDatabase.movieDAO().updateMovies(movie);
            } else {
                movie.setId(this.appDatabase.movieDAO().insertMovies(movie)[0]);
            }

            this.insertTags(movie.getId(), MEDIA_MOVIE, movie.getTags().toArray(new Tag[0]));
            this.insertPersons(movie.getId(), MEDIA_MOVIE, movie.getPersons().toArray(new Person[0]));
            this.insertCompanies(movie.getId(), MEDIA_MOVIE, movie.getCompanies().toArray(new Company[0]));
        } catch (Exception ex) {
            this.messages.append(ex.getMessage()).append("\n");
        }
    }

    public void deleteMovie(Movie movie) {
        if(movie.getCategoryItem() != null) {
            this.appDatabase.categoryDAO().deleteCategories(movie.getCategoryItem());
        }
        this.deletePersons(MEDIA_MOVIE, movie.getId());
        this.deleteCompanies(MEDIA_MOVIE, movie.getId());
        this.deleteTags(MEDIA_MOVIE, movie.getId());

        this.appDatabase.movieDAO().deleteMovies(movie);
    }

    public String getMessages() {
        return this.messages.toString();
    }

    public long insertCategory(Category category) {
        if(category.getId() == 0) {
            Category tmp = this.appDatabase.categoryDAO().getCategory(category.getTitle());
            if (tmp != null) {
                category.setId(tmp.getId());
            }
            category.setId(this.appDatabase.categoryDAO().insertCategories(category)[0]);
        } else {
            this.appDatabase.categoryDAO().updateCategories(category);
        }
        return category.getId();
    }

    public void insertTags(long id, String type, Tag... tags) {
        if(id != 0) {
            switch (type.toLowerCase()) {
                case MEDIA_ALBUM ->
                        this.appDatabase.albumDAO().getAlbumWithTags(id).getTags().forEach(tag -> {
                            TagAlbumCrossRef tagAlbumCrossRef = new TagAlbumCrossRef();
                            tagAlbumCrossRef.setAlbumId(id);
                            tagAlbumCrossRef.setTagId(tag.getId());
                            this.appDatabase.albumDAO().deleteAlbumWithTag(tagAlbumCrossRef);
                        });
                case MEDIA_SONG ->
                        this.appDatabase.songDAO().getSongWithTags(id).getTags().forEach(tag -> {
                            TagSongCrossRef tagSongCrossRef = new TagSongCrossRef();
                            tagSongCrossRef.setSongId(id);
                            tagSongCrossRef.setTagId(tag.getId());
                            this.appDatabase.songDAO().deleteSongWithTag(tagSongCrossRef);
                        });
                case MEDIA_BOOK ->
                        this.appDatabase.bookDAO().getBookWithTags(id).getTags().forEach(tag -> {
                            TagBookCrossRef tagBookCrossRef = new TagBookCrossRef();
                            tagBookCrossRef.setBookId(id);
                            tagBookCrossRef.setTagId(tag.getId());
                            this.appDatabase.bookDAO().deleteBookWithTag(tagBookCrossRef);
                        });
                case MEDIA_GAME ->
                        this.appDatabase.gameDAO().getGameWithTags(id).getTags().forEach(tag -> {
                            TagGameCrossRef tagGameCrossRef = new TagGameCrossRef();
                            tagGameCrossRef.setGameId(id);
                            tagGameCrossRef.setTagId(tag.getId());
                            this.appDatabase.gameDAO().deleteGameWithTag(tagGameCrossRef);
                        });
                case MEDIA_MOVIE ->
                        this.appDatabase.movieDAO().getMovieWithTags(id).getTags().forEach(tag -> {
                            TagMovieCrossRef tagMovieCrossRef = new TagMovieCrossRef();
                            tagMovieCrossRef.setMovieId(id);
                            tagMovieCrossRef.setTagId(tag.getId());
                            this.appDatabase.movieDAO().deleteMovieWithTag(tagMovieCrossRef);
                        });
                case FILTER ->
                        this.appDatabase.filterDAO().getFilterWithTags(id).getTags().forEach(tag -> {
                            FilterTagCrossRef filterTagCrossRef = new FilterTagCrossRef();
                            filterTagCrossRef.setFilterId(id);
                            filterTagCrossRef.setTagId(tag.getId());
                            this.appDatabase.filterDAO().deleteFilterWithTags(filterTagCrossRef);
                        });
                case FILE_TREE ->
                        this.appDatabase.fileTreeDAO().getChildTreeElementWithTags(id).getTags().forEach(tag -> {
                            FileTreeTagCrossRef fileTreeTagCrossRef = new FileTreeTagCrossRef();
                            fileTreeTagCrossRef.setFileTreeId(id);
                            fileTreeTagCrossRef.setTagId(tag.getId());
                            this.appDatabase.fileTreeDAO().deleteFileTreeTagsElements(fileTreeTagCrossRef);
                        });
                case FILE_TREE_FILE ->
                        this.appDatabase.fileTreeFileDAO().getChildTreeFileElementWithTags(id).getTags().forEach(tag -> {
                            FileTreeFileTagCrossRef fileTreeFileTagCrossRef = new FileTreeFileTagCrossRef();
                            fileTreeFileTagCrossRef.setFileTreeFileId(id);
                            fileTreeFileTagCrossRef.setTagId(tag.getId());
                            this.appDatabase.fileTreeFileDAO().deleteFileTreeFileTagsElements(fileTreeFileTagCrossRef);
                        });
            }
        }

        for(Tag tag : tags) {
            try {
                LengthValidator lengthValidator = new LengthValidator(tag.getTitle(), 2, false);
                if(lengthValidator.validate()) {
                    throw new LengthMinException(this.context, 2);
                }

                Tag savedTag = this.appDatabase.tagDAO().getTag(tag.getTitle());
                if(savedTag != null) {
                    tag.setId(savedTag.getId());
                }

                if(tag.getId() != 0) {
                    this.appDatabase.tagDAO().updateTags(tag);
                } else {
                    tag.setId(this.appDatabase.tagDAO().insertTags(tag)[0]);
                }

                if(id != 0) {
                    switch (type.toLowerCase()) {
                        case MEDIA_ALBUM -> {
                            TagAlbumCrossRef tagAlbumCrossRef = new TagAlbumCrossRef();
                            tagAlbumCrossRef.setAlbumId(id);
                            tagAlbumCrossRef.setTagId(tag.getId());
                            this.appDatabase.albumDAO().insertAlbumWithTag(tagAlbumCrossRef);
                        }
                        case MEDIA_SONG -> {
                            TagSongCrossRef tagSongCrossRef = new TagSongCrossRef();
                            tagSongCrossRef.setSongId(id);
                            tagSongCrossRef.setTagId(tag.getId());
                            this.appDatabase.songDAO().insertSongWithTag(tagSongCrossRef);
                        }
                        case MEDIA_BOOK -> {
                            TagBookCrossRef tagBookCrossRef = new TagBookCrossRef();
                            tagBookCrossRef.setBookId(id);
                            tagBookCrossRef.setTagId(tag.getId());
                            this.appDatabase.bookDAO().insertBookWithTag(tagBookCrossRef);
                        }
                        case MEDIA_GAME -> {
                            TagGameCrossRef tagGameCrossRef = new TagGameCrossRef();
                            tagGameCrossRef.setGameId(id);
                            tagGameCrossRef.setTagId(tag.getId());
                            this.appDatabase.gameDAO().insertGameWithTag(tagGameCrossRef);
                        }
                        case MEDIA_MOVIE -> {
                            TagMovieCrossRef tagMovieCrossRef = new TagMovieCrossRef();
                            tagMovieCrossRef.setMovieId(id);
                            tagMovieCrossRef.setTagId(tag.getId());
                            this.appDatabase.movieDAO().insertMovieWithTag(tagMovieCrossRef);
                        }
                        case FILTER -> {
                            FilterTagCrossRef filterTagCrossRef = new FilterTagCrossRef();
                            filterTagCrossRef.setFilterId(id);
                            filterTagCrossRef.setTagId(tag.getId());
                            this.appDatabase.filterDAO().insertFilterWithTags(filterTagCrossRef);
                        }
                        case FILE_TREE -> {
                            FileTreeTagCrossRef fileTreeTagCrossRef = new FileTreeTagCrossRef();
                            fileTreeTagCrossRef.setFileTreeId(id);
                            fileTreeTagCrossRef.setTagId(tag.getId());
                            this.appDatabase.fileTreeDAO().insertFileTreeTagsElements(fileTreeTagCrossRef);
                        }
                        case FILE_TREE_FILE -> {
                            FileTreeFileTagCrossRef fileTreeTagCrossRef = new FileTreeFileTagCrossRef();
                            fileTreeTagCrossRef.setFileTreeFileId(id);
                            fileTreeTagCrossRef.setTagId(tag.getId());
                            this.appDatabase.fileTreeFileDAO().insertFileTreeFileTagsElements(fileTreeTagCrossRef);
                        }
                    }
                }
            } catch (Exception ex) {
                this.messages.append(ex.getMessage()).append("\n");
            }
        }
    }
    
    public List<Tag> getTags(long id, String type) {
        List<Tag> tags = new LinkedList<>();
        switch (type.toLowerCase()) {
            case MEDIA_ALBUM -> {
                if(id != 0) {
                    return this.appDatabase.albumDAO().getAlbumWithTags(id).getTags();
                } else {
                    this.appDatabase.albumDAO().getAllAlbumsWithTags().forEach(tagItem ->
                            tags.addAll(tagItem.getTags()));
                }
            }
            case MEDIA_SONG -> {
                if(id != 0) {
                    return this.appDatabase.songDAO().getSongWithTags(id).getTags();
                } else {
                    this.appDatabase.songDAO().getAllSongsWithTags().forEach(tagItem ->
                        tags.addAll(tagItem.getTags()));
                }
            }
            case MEDIA_BOOK -> {
                if(id != 0) {
                    return this.appDatabase.bookDAO().getBookWithTags(id).getTags();
                } else {
                    this.appDatabase.bookDAO().getAllBooksWithTags().forEach(tagItem ->
                        tags.addAll(tagItem.getTags()));
                }
            }
            case MEDIA_GAME -> {
                if(id != 0) {
                    return this.appDatabase.gameDAO().getGameWithTags(id).getTags();
                } else {
                    this.appDatabase.gameDAO().getAllGamesWithTags().forEach(tagItem ->
                        tags.addAll(tagItem.getTags()));
                }
            }
            case MEDIA_MOVIE -> {
                if(id != 0) {
                    return this.appDatabase.movieDAO().getMovieWithTags(id).getTags();
                } else {
                    this.appDatabase.movieDAO().getAllMoviesWithTags().forEach(tagItem ->
                        tags.addAll(tagItem.getTags()));
                }
            }
            case FILTER -> {
                if(id != 0) {
                    return this.appDatabase.filterDAO().getFilterWithTags(id).getTags();
                }
            }
            case FILE_TREE -> {
                if(id != 0) {
                    return this.appDatabase.fileTreeDAO().getChildTreeElementWithTags(id).getTags();
                } else {
                    this.appDatabase.fileTreeDAO().getChildTreeElementsWithTags().forEach(tagItem ->
                        tags.addAll(tagItem.getTags()));
                }
            }
            case FILE_TREE_FILE -> {
                if(id != 0) {
                    return this.appDatabase.fileTreeFileDAO().getChildTreeFileElementWithTags(id).getTags();
                } else {
                    this.appDatabase.fileTreeFileDAO().getChildTreeFileElementsWithTags().forEach(tagItem ->
                        tags.addAll(tagItem.getTags()));
                }
            }
            default -> {
                return this.appDatabase.tagDAO().getAllTags();
            }
        }
        return tags;
    }
    
    public void deleteTags(String type, long id, Tag... tags) {
        if(id != 0) {
            this.deleteTags(type, id);
        }

        this.appDatabase.tagDAO().deleteTags(tags);
    }
    
    public void deleteTags(String type, long id) {
        switch (type.toLowerCase()) {
            case MEDIA_ALBUM ->
                this.appDatabase.albumDAO().getAlbumWithTags(id).getTags().forEach(tag -> {
                    TagAlbumCrossRef tagAlbumCrossRef = new TagAlbumCrossRef();
                    tagAlbumCrossRef.setAlbumId(id);
                    tagAlbumCrossRef.setTagId(tag.getId());
                    this.appDatabase.albumDAO().deleteAlbumWithTag(tagAlbumCrossRef);
                });
            case MEDIA_SONG ->
                this.appDatabase.songDAO().getSongWithTags(id).getTags().forEach(tag -> {
                    TagSongCrossRef tagSongCrossRef = new TagSongCrossRef();
                    tagSongCrossRef.setSongId(id);
                    tagSongCrossRef.setTagId(tag.getId());
                    this.appDatabase.songDAO().deleteSongWithTag(tagSongCrossRef);
                });
            case MEDIA_BOOK ->
                this.appDatabase.bookDAO().getBookWithTags(id).getTags().forEach(tag -> {
                    TagBookCrossRef tagBookCrossRef = new TagBookCrossRef();
                    tagBookCrossRef.setBookId(id);
                    tagBookCrossRef.setTagId(tag.getId());
                    this.appDatabase.bookDAO().deleteBookWithTag(tagBookCrossRef);
                });
            case MEDIA_GAME ->
                this.appDatabase.gameDAO().getGameWithTags(id).getTags().forEach(tag -> {
                    TagGameCrossRef tagGameCrossRef = new TagGameCrossRef();
                    tagGameCrossRef.setGameId(id);
                    tagGameCrossRef.setTagId(tag.getId());
                    this.appDatabase.gameDAO().deleteGameWithTag(tagGameCrossRef);
                });
            case MEDIA_MOVIE ->
                this.appDatabase.movieDAO().getMovieWithTags(id).getTags().forEach(tag -> {
                    TagMovieCrossRef tagMovieCrossRef = new TagMovieCrossRef();
                    tagMovieCrossRef.setMovieId(id);
                    tagMovieCrossRef.setTagId(tag.getId());
                    this.appDatabase.movieDAO().deleteMovieWithTag(tagMovieCrossRef);
                });
            case FILTER ->
                this.appDatabase.filterDAO().getFilterWithTags(id).getTags().forEach(tag -> {
                    FilterTagCrossRef tagFilterCrossRef = new FilterTagCrossRef();
                    tagFilterCrossRef.setFilterId(id);
                    tagFilterCrossRef.setTagId(tag.getId());
                    this.appDatabase.filterDAO().deleteFilterWithTags(tagFilterCrossRef);
                });
            case FILE_TREE ->
                this.appDatabase.fileTreeFileDAO().getChildTreeFileElementWithTags(id).getTags().forEach(tag -> {
                    FileTreeTagCrossRef fileTreeTagCrossRef = new FileTreeTagCrossRef();
                    fileTreeTagCrossRef.setFileTreeId(id);
                    fileTreeTagCrossRef.setTagId(tag.getId());
                    this.appDatabase.fileTreeDAO().deleteFileTreeTagsElements(fileTreeTagCrossRef);
                });
            case FILE_TREE_FILE ->
                this.appDatabase.fileTreeFileDAO().getChildTreeFileElementWithTags(id).getTags().forEach(tag -> {
                    FileTreeFileTagCrossRef fileTreeFileTagCrossRef = new FileTreeFileTagCrossRef();
                    fileTreeFileTagCrossRef.setFileTreeFileId(id);
                    fileTreeFileTagCrossRef.setTagId(tag.getId());
                    this.appDatabase.fileTreeFileDAO().deleteFileTreeFileTagsElements(fileTreeFileTagCrossRef);
                });
        }
    }

    public void insertPersons(long id, String type, Person... persons) {
        if(id != 0) {
            switch (type.toLowerCase()) {
                case MEDIA_ALBUM ->
                        this.appDatabase.albumDAO().getAlbumWithPersons(id).getPersons().forEach(person -> {
                            PersonAlbumCrossRef personAlbumCrossRef = new PersonAlbumCrossRef();
                            personAlbumCrossRef.setAlbumId(id);
                            personAlbumCrossRef.setPersonId(person.getId());
                            this.appDatabase.albumDAO().deleteAlbumWithPerson(personAlbumCrossRef);
                        });
                case MEDIA_SONG ->
                        this.appDatabase.songDAO().getSongWithPersons(id).getPersons().forEach(tag -> {
                            PersonSongCrossRef personSongCrossRef = new PersonSongCrossRef();
                            personSongCrossRef.setSongId(id);
                            personSongCrossRef.setPersonId(tag.getId());
                            this.appDatabase.songDAO().deleteSongWithPerson(personSongCrossRef);
                        });
                case MEDIA_BOOK ->
                        this.appDatabase.bookDAO().getBookWithTags(id).getTags().forEach(tag -> {
                            PersonBookCrossRef personBookCrossRef = new PersonBookCrossRef();
                            personBookCrossRef.setBookId(id);
                            personBookCrossRef.setPersonId(tag.getId());
                            this.appDatabase.bookDAO().deleteBookWithPerson(personBookCrossRef);
                        });
                case MEDIA_GAME ->
                        this.appDatabase.gameDAO().getGameWithTags(id).getTags().forEach(tag -> {
                            TagGameCrossRef tagGameCrossRef = new TagGameCrossRef();
                            tagGameCrossRef.setGameId(id);
                            tagGameCrossRef.setTagId(tag.getId());
                            this.appDatabase.gameDAO().deleteGameWithTag(tagGameCrossRef);
                        });
                case MEDIA_MOVIE ->
                        this.appDatabase.movieDAO().getMovieWithTags(id).getTags().forEach(tag -> {
                            TagMovieCrossRef tagMovieCrossRef = new TagMovieCrossRef();
                            tagMovieCrossRef.setMovieId(id);
                            tagMovieCrossRef.setTagId(tag.getId());
                            this.appDatabase.movieDAO().deleteMovieWithTag(tagMovieCrossRef);
                        });
            }
        }

        for(Person person : persons) {
            try {
                LengthValidator personFirstNameValidator = new LengthValidator(person.getFirstName(), 2, false);
                if(personFirstNameValidator.validate()) {
                    throw new LengthMinException(this.context, 2);
                }
                LengthValidator personLastNameValidator = new LengthValidator(person.getLastName(), 2, false);
                if(personLastNameValidator.validate()) {
                    throw new LengthMinException(this.context, 2);
                }

                Person savedPerson = this.appDatabase.personDAO().getPerson(person.getFirstName(), person.getLastName());
                if(savedPerson != null) {
                    person.setId(savedPerson.getId());
                }

                if(person.getId() != 0) {
                    this.appDatabase.personDAO().updatePersons(person);
                } else {
                    person.setId(this.appDatabase.personDAO().insertPersons(person)[0]);
                }

                if(id != 0) {
                    switch (type.toLowerCase()) {
                        case MEDIA_ALBUM -> {
                            PersonAlbumCrossRef personAlbumCrossRef = new PersonAlbumCrossRef();
                            personAlbumCrossRef.setAlbumId(id);
                            personAlbumCrossRef.setPersonId(person.getId());
                            this.appDatabase.albumDAO().insertAlbumWithPerson(personAlbumCrossRef);
                        }
                        case MEDIA_SONG -> {
                            PersonSongCrossRef personSongCrossRef = new PersonSongCrossRef();
                            personSongCrossRef.setSongId(id);
                            personSongCrossRef.setPersonId(person.getId());
                            this.appDatabase.songDAO().insertSongWithPerson(personSongCrossRef);
                        }
                        case MEDIA_BOOK -> {
                            PersonBookCrossRef personBookCrossRef = new PersonBookCrossRef();
                            personBookCrossRef.setBookId(id);
                            personBookCrossRef.setPersonId(person.getId());
                            this.appDatabase.bookDAO().insertBookWithPerson(personBookCrossRef);
                        }
                        case MEDIA_GAME -> {
                            PersonGameCrossRef personGameCrossRef = new PersonGameCrossRef();
                            personGameCrossRef.setGameId(id);
                            personGameCrossRef.setPersonId(person.getId());
                            this.appDatabase.gameDAO().insertGameWithPerson(personGameCrossRef);
                        }
                        case MEDIA_MOVIE -> {
                            PersonMovieCrossRef personMovieCrossRef = new PersonMovieCrossRef();
                            personMovieCrossRef.setMovieId(id);
                            personMovieCrossRef.setPersonId(person.getId());
                            this.appDatabase.movieDAO().insertMovieWithPerson(personMovieCrossRef);
                        }
                    }
                }
            } catch (Exception ex) {
                this.messages.append(ex.getMessage()).append("\n");
            }
        }
    }

    public List<Person> getPersons(long id, String type) {
        List<Person> people = new LinkedList<>();
        switch (type.toLowerCase()) {
            case MEDIA_ALBUM -> {
                if(id != 0) {
                    return this.appDatabase.albumDAO().getAlbumWithPersons(id).getPersons();
                } else {
                    this.appDatabase.albumDAO().getAllAlbumsWithPersons().forEach(persons ->
                        people.addAll(persons.getPersons()));
                }
            }
            case MEDIA_SONG -> {
                if(id != 0) {
                    return this.appDatabase.songDAO().getSongWithPersons(id).getPersons();
                } else {
                    this.appDatabase.songDAO().getAllSongsWithPersons().forEach(persons ->
                        people.addAll(persons.getPersons()));
                }
            }
            case MEDIA_BOOK -> {
                if(id != 0) {
                    return this.appDatabase.bookDAO().getBookWithPersons(id).getPersons();
                } else {
                    this.appDatabase.bookDAO().getAllBooks().forEach(books ->
                        people.addAll(books.getPersons()));
                }
            }
            case MEDIA_GAME -> {
                if(id != 0) {
                    return this.appDatabase.gameDAO().getGameWithPersons(id).getPersons();
                } else {
                    this.appDatabase.gameDAO().getAllGamesWithPersons().forEach(persons ->
                        people.addAll(persons.getPersons()));
                }
            }
            case MEDIA_MOVIE -> {
                if (id != 0) {
                    return this.appDatabase.movieDAO().getMovieWithPersons(id).getPersons();
                } else {
                    this.appDatabase.movieDAO().getAllMoviesWithPersons().forEach(persons ->
                        people.addAll(persons.getPersons()));
                }
            }
            default -> {
                return this.appDatabase.personDAO().getAllPersons();
            }
        }
        return people;
    }

    public void deletePersons(String type, long id, Person... persons) {
        this.deletePersons(type, id);


        this.appDatabase.personDAO().deletePersons(persons);
    }

    public void deletePersons(String type, long id) {
        switch (type.toLowerCase()) {
            case MEDIA_ALBUM ->
                    this.appDatabase.albumDAO().getAlbumWithPersons(id).getPersons().forEach(person -> {
                        PersonAlbumCrossRef personAlbumCrossRef = new PersonAlbumCrossRef();
                        personAlbumCrossRef.setAlbumId(id);
                        personAlbumCrossRef.setPersonId(person.getId());
                        this.appDatabase.albumDAO().deleteAlbumWithPerson(personAlbumCrossRef);
                    });
            case MEDIA_SONG ->
                    this.appDatabase.songDAO().getSongWithPersons(id).getPersons().forEach(person -> {
                        PersonSongCrossRef personSongCrossRef = new PersonSongCrossRef();
                        personSongCrossRef.setSongId(id);
                        personSongCrossRef.setPersonId(person.getId());
                        this.appDatabase.songDAO().deleteSongWithPerson(personSongCrossRef);
                    });
            case MEDIA_BOOK ->
                    this.appDatabase.bookDAO().getBookWithPersons(id).getPersons().forEach(person -> {
                        PersonBookCrossRef personBookCrossRef = new PersonBookCrossRef();
                        personBookCrossRef.setBookId(id);
                        personBookCrossRef.setPersonId(person.getId());
                        this.appDatabase.bookDAO().deleteBookWithPerson(personBookCrossRef);
                    });
            case MEDIA_GAME ->
                    this.appDatabase.gameDAO().getGameWithPersons(id).getPersons().forEach(person -> {
                        PersonGameCrossRef personGameCrossRef = new PersonGameCrossRef();
                        personGameCrossRef.setGameId(id);
                        personGameCrossRef.setPersonId(person.getId());
                        this.appDatabase.gameDAO().deleteGameWithPerson(personGameCrossRef);
                    });
            case MEDIA_MOVIE ->
                    this.appDatabase.movieDAO().getMovieWithPersons(id).getPersons().forEach(company -> {
                        PersonMovieCrossRef personMovieCrossRef = new PersonMovieCrossRef();
                        personMovieCrossRef.setMovieId(id);
                        personMovieCrossRef.setPersonId(company.getId());
                        this.appDatabase.movieDAO().deleteMovieWithPerson(personMovieCrossRef);
                    });
        }
    }

    public void insertCompanies(long id, String type, Company... companies) {
        if(id != 0) {
            switch (type.toLowerCase()) {
                case MEDIA_ALBUM ->
                        this.appDatabase.albumDAO().getAlbumWithCompanies(id).getCompanies().forEach(company -> {
                            CompanyAlbumCrossRef companyAlbumCrossRef = new CompanyAlbumCrossRef();
                            companyAlbumCrossRef.setAlbumId(id);
                            companyAlbumCrossRef.setCompanyId(company.getId());
                            this.appDatabase.albumDAO().deleteAlbumWithCompany(companyAlbumCrossRef);
                        });
                case MEDIA_SONG ->
                        this.appDatabase.songDAO().getSongWithCompanies(id).getCompanies().forEach(tag -> {
                            CompanySongCrossRef companySongCrossRef = new CompanySongCrossRef();
                            companySongCrossRef.setSongId(id);
                            companySongCrossRef.setCompanyId(tag.getId());
                            this.appDatabase.songDAO().deleteSongWithCompany(companySongCrossRef);
                        });
                case MEDIA_BOOK ->
                        this.appDatabase.bookDAO().getBookWithTags(id).getTags().forEach(tag -> {
                            CompanyBookCrossRef companyBookCrossRef = new CompanyBookCrossRef();
                            companyBookCrossRef.setBookId(id);
                            companyBookCrossRef.setCompanyId(tag.getId());
                            this.appDatabase.bookDAO().deleteBookWithCompany(companyBookCrossRef);
                        });
                case MEDIA_GAME ->
                        this.appDatabase.gameDAO().getGameWithTags(id).getTags().forEach(tag -> {
                            TagGameCrossRef tagGameCrossRef = new TagGameCrossRef();
                            tagGameCrossRef.setGameId(id);
                            tagGameCrossRef.setTagId(tag.getId());
                            this.appDatabase.gameDAO().deleteGameWithTag(tagGameCrossRef);
                        });
                case MEDIA_MOVIE ->
                        this.appDatabase.movieDAO().getMovieWithTags(id).getTags().forEach(tag -> {
                            TagMovieCrossRef tagMovieCrossRef = new TagMovieCrossRef();
                            tagMovieCrossRef.setMovieId(id);
                            tagMovieCrossRef.setTagId(tag.getId());
                            this.appDatabase.movieDAO().deleteMovieWithTag(tagMovieCrossRef);
                        });
            }
        }

        for(Company company : companies) {
            try {
                LengthValidator companyFirstNameValidator = new LengthValidator(company.getTitle(), 2, false);
                if(companyFirstNameValidator.validate()) {
                    throw new LengthMinException(this.context, 2);
                }

                Company savedCompany = this.appDatabase.companyDAO().getCompany(company.getTitle());
                if(savedCompany != null) {
                    company.setId(savedCompany.getId());
                }

                if(company.getId() != 0) {
                    this.appDatabase.companyDAO().updateCompanies(company);
                } else {
                    company.setId(this.appDatabase.companyDAO().insertCompanies(company)[0]);
                }

                if(id != 0) {
                    switch (type.toLowerCase()) {
                        case MEDIA_ALBUM -> {
                            CompanyAlbumCrossRef companyAlbumCrossRef = new CompanyAlbumCrossRef();
                            companyAlbumCrossRef.setAlbumId(id);
                            companyAlbumCrossRef.setCompanyId(company.getId());
                            this.appDatabase.albumDAO().insertAlbumWithCompany(companyAlbumCrossRef);
                        }
                        case MEDIA_SONG -> {
                            CompanySongCrossRef companySongCrossRef = new CompanySongCrossRef();
                            companySongCrossRef.setSongId(id);
                            companySongCrossRef.setCompanyId(company.getId());
                            this.appDatabase.songDAO().insertSongWithCompany(companySongCrossRef);
                        }
                        case MEDIA_BOOK -> {
                            CompanyBookCrossRef companyBookCrossRef = new CompanyBookCrossRef();
                            companyBookCrossRef.setBookId(id);
                            companyBookCrossRef.setCompanyId(company.getId());
                            this.appDatabase.bookDAO().insertBookWithCompany(companyBookCrossRef);
                        }
                        case MEDIA_GAME -> {
                            CompanyGameCrossRef companyGameCrossRef = new CompanyGameCrossRef();
                            companyGameCrossRef.setGameId(id);
                            companyGameCrossRef.setCompanyId(company.getId());
                            this.appDatabase.gameDAO().insertGameWithCompany(companyGameCrossRef);
                        }
                        case MEDIA_MOVIE -> {
                            CompanyMovieCrossRef companyMovieCrossRef = new CompanyMovieCrossRef();
                            companyMovieCrossRef.setMovieId(id);
                            companyMovieCrossRef.setCompanyId(company.getId());
                            this.appDatabase.movieDAO().insertMovieWithCompany(companyMovieCrossRef);
                        }
                    }
                }
            } catch (Exception ex) {
                this.messages.append(ex.getMessage()).append("\n");
            }
        }
    }

    public List<Company> getCompanies(long id, String type) {
        List<Company> people = new LinkedList<>();
        switch (type.toLowerCase()) {
            case MEDIA_ALBUM -> {
                if(id != 0) {
                    return this.appDatabase.albumDAO().getAlbumWithCompanies(id).getCompanies();
                } else {
                    this.appDatabase.albumDAO().getAllAlbumsWithCompanies().forEach(companies ->
                        people.addAll(companies.getCompanies()));
                }
            }
            case MEDIA_SONG -> {
                if(id != 0) {
                    return this.appDatabase.songDAO().getSongWithCompanies(id).getCompanies();
                } else {
                    this.appDatabase.songDAO().getAllSongsWithCompanies().forEach(companies ->
                        people.addAll(companies.getCompanies()));
                }
            }
            case MEDIA_BOOK -> {
                if(id != 0) {
                    return this.appDatabase.bookDAO().getBookWithCompanies(id).getCompanies();
                } else {
                    this.appDatabase.bookDAO().getAllBooks().forEach(books ->
                        people.addAll(books.getCompanies()));
                }
            }
            case MEDIA_GAME -> {
                if(id != 0) {
                    return this.appDatabase.gameDAO().getGameWithCompanies(id).getCompanies();
                } else {
                    this.appDatabase.gameDAO().getAllGamesWithCompanies().forEach(companies ->
                        people.addAll(companies.getCompanies()));
                }
            }
            case MEDIA_MOVIE -> {
                if (id != 0) {
                    return this.appDatabase.movieDAO().getMovieWithCompanies(id).getCompanies();
                } else {
                    this.appDatabase.movieDAO().getAllMoviesWithCompanies().forEach(companies ->
                        people.addAll(companies.getCompanies()));
                }
            }
            default -> {
                return this.appDatabase.companyDAO().getAllCompanies();
            }
        }
        return people;
    }

    public void deleteCompanies(String type, long id, Company... companies) {
        this.deleteCompanies(type, id);


        this.appDatabase.companyDAO().deleteCompanies(companies);
    }

    public void deleteCompanies(String type, long id) {
        switch (type.toLowerCase()) {
            case MEDIA_ALBUM ->
                this.appDatabase.albumDAO().getAlbumWithCompanies(id).getCompanies().forEach(company -> {
                    CompanyAlbumCrossRef companyAlbumCrossRef = new CompanyAlbumCrossRef();
                    companyAlbumCrossRef.setAlbumId(id);
                    companyAlbumCrossRef.setCompanyId(company.getId());
                    this.appDatabase.albumDAO().deleteAlbumWithCompany(companyAlbumCrossRef);
                });
            case MEDIA_SONG ->
                this.appDatabase.songDAO().getSongWithCompanies(id).getCompanies().forEach(company -> {
                    CompanySongCrossRef companySongCrossRef = new CompanySongCrossRef();
                    companySongCrossRef.setSongId(id);
                    companySongCrossRef.setCompanyId(company.getId());
                    this.appDatabase.songDAO().deleteSongWithCompany(companySongCrossRef);
                });
            case MEDIA_BOOK ->
                this.appDatabase.bookDAO().getBookWithCompanies(id).getCompanies().forEach(company -> {
                    CompanyBookCrossRef companyBookCrossRef = new CompanyBookCrossRef();
                    companyBookCrossRef.setBookId(id);
                    companyBookCrossRef.setCompanyId(company.getId());
                    this.appDatabase.bookDAO().deleteBookWithCompany(companyBookCrossRef);
                });
            case MEDIA_GAME ->
                this.appDatabase.gameDAO().getGameWithCompanies(id).getCompanies().forEach(company -> {
                    CompanyGameCrossRef companyGameCrossRef = new CompanyGameCrossRef();
                    companyGameCrossRef.setGameId(id);
                    companyGameCrossRef.setCompanyId(company.getId());
                    this.appDatabase.gameDAO().deleteGameWithCompany(companyGameCrossRef);
                });
            case MEDIA_MOVIE ->
                this.appDatabase.movieDAO().getMovieWithCompanies(id).getCompanies().forEach(company -> {
                    CompanyMovieCrossRef companyMovieCrossRef = new CompanyMovieCrossRef();
                    companyMovieCrossRef.setMovieId(id);
                    companyMovieCrossRef.setCompanyId(company.getId());
                    this.appDatabase.movieDAO().deleteMovieWithCompany(companyMovieCrossRef);
                });
        }
    }
}