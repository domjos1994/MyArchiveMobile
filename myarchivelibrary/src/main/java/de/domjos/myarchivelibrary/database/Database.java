/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivelibrary.database;


import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.model.media.music.Song;

public class Database extends SQLiteOpenHelper {
    private final static String TYPE = "type";
    private final static String ALBUMS = "albums";
    private final static String PATH = "path";
    private final static String LENGTH = "length";
    private final static String MOVIES = "movies";
    private final static String BOOKS = "books";
    private final static String GAMES = "games";
    private final static String DEAD_LINE = "deadLine";
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static String DESCRIPTION = "description";
    private final static String TITLE = "title";
    private final static String COVER = "cover";
    private final static String TAGS = "tags";
    private final static String CATEGORIES = "categories";
    private final static String ID_FILTER = "id=";

    private SQLiteDatabase database;
    private Context context;
    private String password;

    public Database(Context context, String password) {
        super(context, context.getString(R.string.sqLite_name), null, Integer.parseInt(context.getString(R.string.sqLite_version)));

        this.context = context;
        this.password = password;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String content = Database.readRawTextFile(this.context, R.raw.init);
        if(content != null) {
            for(String query : content.split(";")) {
                if(!query.trim().isEmpty()) {
                    db.execSQL(query.trim());
                }
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);

        String content = Database.readRawTextFile(this.context, R.raw.update);
        this.updateDatabase(content, oldVersion, newVersion, db);
    }

    public List<BaseMediaObject> getObjectList(Map<DatabaseObject, String> content) {
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        for(Map.Entry<DatabaseObject, String> entry : content.entrySet()) {
            Cursor cursor = this.getReadableDatabase().rawQuery("SELECT id, title, cover FROM " + entry.getKey().getTable() + where(entry.getValue()), new String[]{});
            while (cursor.moveToNext()) {
                BaseMediaObject baseMediaObject = null;
                switch (entry.getKey().getTable()) {
                    case Database.BOOKS:
                        baseMediaObject = new Book();
                        break;
                    case Database.MOVIES:
                        baseMediaObject = new Movie();
                        break;
                    case Database.GAMES:
                        baseMediaObject = new Game();
                        break;
                    case "albums":
                        baseMediaObject = new Album();
                        break;
                }

                if(baseMediaObject != null) {
                    baseMediaObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
                    baseMediaObject.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    baseMediaObject.setCover(cursor.getBlob(cursor.getColumnIndex("cover")));
                    baseMediaObjects.add(baseMediaObject);
                }
            }
            cursor.close();
        }
        return baseMediaObjects;
    }

    public void insertOrUpdateAlbum(Album album) {
        SQLiteStatement statement = this.getStatement(album, Arrays.asList(Database.TYPE, "numberOfDisks", "last_heard"));
        int position = this.insertOrUpdateBaseMediaObject(statement, album);
        if(album.getType() != null) {
            statement.bindString(++position, album.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindLong(++position, album.getNumberOfDisks());
        if(album.getLastHeard() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(album.getLastHeard(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(album.getId() == 0) {
            album.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        for(Song song : album.getSongs()) {
            this.insertOrUpdateSong(song, album.getId());
        }
        this.saveForeignTables(album, album.getTable());
    }

    public List<Album> getAlbums(String where) throws ParseException {
        List<Album> albums = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM albums" + this.where(where), null);
        while (cursor.moveToNext()) {
            Album album = new Album();
            this.getMediaObjectFromCursor(cursor, album, Database.ALBUMS);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                album.setType(!type.trim().isEmpty() ? Album.Type.valueOf(type) : Album.Type.AudioCD);
            }
            album.setNumberOfDisks(cursor.getInt(cursor.getColumnIndex("numberOfDisks")));
            String dt = cursor.getString(cursor.getColumnIndex("last_heard"));
            if(dt != null && !dt.isEmpty()) {
                album.setLastHeard(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            album.setSongs(this.getSongs("album=" + album.getId()));
            albums.add(album);
        }
        cursor.close();
        return albums;
    }

    public void insertOrUpdateMovie(Movie movie) {
        SQLiteStatement statement = this.getStatement(movie, Arrays.asList(Database.TYPE, Database.LENGTH, Database.PATH, "last_seen"));
        int position = this.insertOrUpdateBaseMediaObject(statement, movie);
        if(movie.getType() != null) {
            statement.bindString(++position, movie.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindDouble(++position, movie.getLength());
        statement.bindString(++position, movie.getPath());
        if(movie.getLastSeen() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(movie.getLastSeen(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(movie.getId()==0) {
            movie.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(movie, movie.getTable());
    }

    public List<Movie> getMovies(String where) throws ParseException {
        List<Movie> movies = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM movies" + this.where(where), null);
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            this.getMediaObjectFromCursor(cursor, movie, Database.MOVIES);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                movie.setType(!type.trim().isEmpty() ? Movie.Type.valueOf(type) : Movie.Type.DVD);
            }
            movie.setLength(cursor.getDouble(cursor.getColumnIndex(Database.LENGTH)));
            movie.setPath(cursor.getString(cursor.getColumnIndex(Database.PATH)));
            String dt = cursor.getString(cursor.getColumnIndex("last_seen"));
            if(dt != null && !dt.isEmpty()) {
                movie.setLastSeen(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            movies.add(movie);
        }
        cursor.close();
        return movies;
    }

    public void insertOrUpdateBook(Book book) {
        SQLiteStatement statement = this.getStatement(book, Arrays.asList(Database.TYPE, "numberOfPages", Database.PATH, "edition", "topics", "last_read"));
        int position = this.insertOrUpdateBaseMediaObject(statement, book);
        if(book.getType()!=null) {
            statement.bindString(++position, book.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindDouble(++position, book.getNumberOfPages());
        statement.bindString(++position, book.getPath());
        statement.bindString(++position, book.getEdition());
        statement.bindString(++position, TextUtils.join("\n", book.getTopics()));
        if(book.getLastRead() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(book.getLastRead(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(book.getId() == 0) {
            book.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(book, book.getTable());
    }

    public List<Book> getBooks(String where) throws ParseException {
        List<Book> books = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM books" + this.where(where), null);
        while (cursor.moveToNext()) {
            Book book = new Book();
            this.getMediaObjectFromCursor(cursor, book, Database.BOOKS);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                book.setType(!type.trim().isEmpty() ? Book.Type.valueOf(type) : Book.Type.book);
            }
            book.setNumberOfPages(cursor.getInt(cursor.getColumnIndex("numberOfPages")));
            book.setPath(cursor.getString(cursor.getColumnIndex(Database.PATH)));
            book.setEdition(cursor.getString(cursor.getColumnIndex("edition")));
            book.setTopics(Arrays.asList(cursor.getString(cursor.getColumnIndex("topics")).split("\n")));
            String dt = cursor.getString(cursor.getColumnIndex("last_read"));
            if(dt != null && !dt.isEmpty()) {
                book.setLastRead(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public void insertOrUpdateGame(Game game) {
        SQLiteStatement statement = this.getStatement(game, Arrays.asList(Database.TYPE, Database.LENGTH, "last_played"));
        int position = this.insertOrUpdateBaseMediaObject(statement, game);
        if(game.getType() != null) {
            statement.bindString(++position, game.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindDouble(++position, game.getLength());
        if(game.getLastPlayed() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(game.getLastPlayed(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(game.getId() == 0) {
            game.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(game, game.getTable());
    }

    public List<Game> getGames(String where) throws ParseException {
        List<Game> games = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM games" + this.where(where), null);
        while (cursor.moveToNext()) {
            Game game = new Game();
            this.getMediaObjectFromCursor(cursor, game, Database.GAMES);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                game.setType(!type.trim().isEmpty() ? Game.Type.valueOf(type) : Game.Type.computer);
            }
            game.setLength(cursor.getDouble(cursor.getColumnIndex(Database.LENGTH)));
            String dt = cursor.getString(cursor.getColumnIndex("last_played"));
            if(dt != null && !dt.isEmpty()) {
                game.setLastPlayed(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            games.add(game);
        }
        cursor.close();
        return games;
    }

    public void insertOrUpdateLibraryObject(LibraryObject libraryObject, BaseMediaObject baseMediaObject) {
        SQLiteStatement sqLiteStatement = this.getBaseStatement(libraryObject, Arrays.asList("media", Database.TYPE, "person", "numberOfDays", "numberOfWeeks", Database.DEAD_LINE, "returnedAt"));
        sqLiteStatement.bindLong(1, baseMediaObject.getId());
        if(baseMediaObject instanceof Book) {
            sqLiteStatement.bindString(2, Database.BOOKS);
        }
        if(baseMediaObject instanceof Movie) {
            sqLiteStatement.bindString(2, Database.MOVIES);
        }
        if(baseMediaObject instanceof Album) {
            sqLiteStatement.bindString(2, Database.ALBUMS);
        }
        if(baseMediaObject instanceof Game) {
            sqLiteStatement.bindString(2, Database.GAMES);
        }
        sqLiteStatement.bindLong(3, this.insertOrUpdatePerson(libraryObject.getPerson(), "", 0));
        sqLiteStatement.bindLong(4, libraryObject.getNumberOfDays());
        sqLiteStatement.bindLong(5, libraryObject.getNumberOfWeeks());
        if(libraryObject.getDeadLine() != null) {
            sqLiteStatement.bindString(6, Objects.requireNonNull(ConvertHelper.convertDateToString(libraryObject.getDeadLine(), Database.DATE_FORMAT)));
        } else {
            sqLiteStatement.bindNull(6);
        }
        if(libraryObject.getReturned() != null) {
            sqLiteStatement.bindString(7, Objects.requireNonNull(ConvertHelper.convertDateToString(libraryObject.getReturned(), Database.DATE_FORMAT)));
        } else {
            sqLiteStatement.bindNull(7);
        }
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }

    public List<LibraryObject> getLibraryObjects(String where) throws ParseException {
        List<LibraryObject> libraryObjects = new LinkedList<>();
        List<Person> persons = this.getPersons("", 0);
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM library" + this.where(where), null);
        while (cursor.moveToNext()) {
            LibraryObject libraryObject = new LibraryObject();
            for(Person person : persons) {
                if(cursor.getLong(cursor.getColumnIndex("person"))==person.getId()) {
                    libraryObject.setPerson(person);
                    break;
                }
            }
            libraryObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
            libraryObject.setNumberOfDays(cursor.getInt(cursor.getColumnIndex("numberOfDays")));
            libraryObject.setNumberOfWeeks(cursor.getInt(cursor.getColumnIndex("numberOfWeeks")));
            String deadline = cursor.getString(cursor.getColumnIndex(Database.DEAD_LINE));
            if(deadline!=null) {
                libraryObject.setDeadLine(!deadline.isEmpty() ? ConvertHelper.convertStringToDate(deadline, Database.DATE_FORMAT) : null);
            }
            String returnedAt = cursor.getString(cursor.getColumnIndex("returnedAt"));
            if(returnedAt!=null) {
                libraryObject.setReturned(!returnedAt.isEmpty() ? ConvertHelper.convertStringToDate(returnedAt, Database.DATE_FORMAT) : null);
            }
            libraryObjects.add(libraryObject);
        }
        cursor.close();
        return libraryObjects;
    }

    public Map<BaseMediaObject, LibraryObject> getLendOutObjects(Person person) throws ParseException {
        Map<BaseMediaObject, LibraryObject> mp = new LinkedHashMap<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT media, type, id FROM library WHERE person=" + person.getId(), new String[]{});
        while (cursor.moveToNext()) {
            LibraryObject libraryObject = this.getLibraryObjects(Database.ID_FILTER + cursor.getLong(cursor.getColumnIndex("id"))).get(0);
            BaseMediaObject baseMediaObject = null;
            long id = cursor.getLong(cursor.getColumnIndex("media"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            switch (type.trim().toLowerCase()) {
                case Database.BOOKS:
                    baseMediaObject = this.getBooks(Database.ID_FILTER + id).get(0);
                    break;
                case Database.MOVIES:
                    baseMediaObject = this.getMovies(Database.ID_FILTER + id).get(0);
                    break;
                case "albums":
                    baseMediaObject = this.getAlbums(Database.ID_FILTER + id).get(0);
                    break;
                case Database.GAMES:
                    baseMediaObject = this.getGames(Database.ID_FILTER + id).get(0);
                    break;
            }
            mp.put(baseMediaObject, libraryObject);
        }
        cursor.close();
        return mp;
    }

    public void insertOrUpdateMediaList(MediaList mediaList) {
        SQLiteStatement sqLiteStatement = this.getBaseStatement(mediaList, Arrays.asList(Database.TITLE, Database.DEAD_LINE, Database.DESCRIPTION));
        sqLiteStatement.bindString(1, mediaList.getTitle());
        if(mediaList.getDeadLine() != null) {
            String content = ConvertHelper.convertDateToString(mediaList.getDeadLine(), Database.DATE_FORMAT);
            if(content == null) {
                sqLiteStatement.bindNull(2);
            } else {
                sqLiteStatement.bindString(2, content);
            }
        } else {
            sqLiteStatement.bindNull(2);
        }
        sqLiteStatement.bindString(3, mediaList.getDescription());

        if(mediaList.getId() != 0) {
            sqLiteStatement.execute();
        } else {
            mediaList.setId(sqLiteStatement.executeInsert());
        }
        sqLiteStatement.close();

        this.getWritableDatabase().execSQL("DELETE FROM media_lists WHERE list=?", new String[]{String.valueOf(mediaList.getId())});

        for(BaseMediaObject baseMediaObject : mediaList.getBaseMediaObjects()) {
            sqLiteStatement = this.getWritableDatabase().compileStatement("INSERT INTO media_lists(list, media, type) VALUES(?, ?, ?)");
            sqLiteStatement.bindLong(1, mediaList.getId());
            sqLiteStatement.bindLong(2, baseMediaObject.getId());
            if(baseMediaObject instanceof Book) {
                sqLiteStatement.bindString(3, Database.BOOKS);
            }
            if(baseMediaObject instanceof Movie) {
                sqLiteStatement.bindString(3, Database.MOVIES);
            }
            if(baseMediaObject instanceof Album) {
                sqLiteStatement.bindString(3, Database.ALBUMS);
            }
            if(baseMediaObject instanceof Game) {
                sqLiteStatement.bindString(3, Database.GAMES);
            }
            sqLiteStatement.executeInsert();
            sqLiteStatement.close();
        }
    }

    public List<MediaList> getMediaLists(String where) throws ParseException {
        List<MediaList> mediaLists = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM lists" + this.where(where), null);
        while (cursor.moveToNext()) {
            MediaList mediaList = new MediaList();
            mediaList.setId(cursor.getLong(cursor.getColumnIndex("id")));
            mediaList.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
            mediaList.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
            String deadLine = cursor.getString(cursor.getColumnIndex(Database.DEAD_LINE));
            if(deadLine != null) {
                mediaList.setDeadLine(!deadLine.isEmpty() ? ConvertHelper.convertStringToDate(deadLine, Database.DATE_FORMAT) : null);
            }
            mediaList.setBaseMediaObjects(this.getObjects("lists", mediaList.getId()));
            mediaLists.add(mediaList);
        }
        cursor.close();
        return mediaLists;
    }

    public void insertOrUpdateFilter(MediaFilter mediaFilter) {
        List<MediaFilter> mediaFilters = this.getFilters("title='" + mediaFilter.getTitle() + "'");
        if(mediaFilters!=null && !mediaFilters.isEmpty()) {
            mediaFilter.setId(mediaFilters.get(0).getId());
        }
        SQLiteStatement sqLiteStatement = this.getBaseStatement(mediaFilter, Arrays.asList(Database.TITLE, "search", Database.CATEGORIES, Database.TAGS, Database.BOOKS, Database.MOVIES, "music", Database.GAMES));
        sqLiteStatement.bindString(1, mediaFilter.getTitle());
        sqLiteStatement.bindString(2, mediaFilter.getSearch());
        sqLiteStatement.bindString(3, mediaFilter.getCategories());
        sqLiteStatement.bindString(4, mediaFilter.getTags());
        sqLiteStatement.bindLong(5, mediaFilter.isBooks() ? 1 : 0);
        sqLiteStatement.bindLong(6, mediaFilter.isMovies() ? 1 : 0);
        sqLiteStatement.bindLong(7, mediaFilter.isMusic() ? 1 : 0);
        sqLiteStatement.bindLong(8, mediaFilter.isGames() ? 1 : 0);
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }

    public List<MediaFilter> getFilters(String where) {
        List<MediaFilter> mediaFilters = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM filters" + this.where(where), null);
        while (cursor.moveToNext()) {
            MediaFilter mediaFilter = new MediaFilter();
            mediaFilter.setId(cursor.getLong(cursor.getColumnIndex("id")));
            mediaFilter.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
            mediaFilter.setSearch(cursor.getString(cursor.getColumnIndex("search")));
            mediaFilter.setCategories(cursor.getString(cursor.getColumnIndex(Database.CATEGORIES)));
            mediaFilter.setTags(cursor.getString(cursor.getColumnIndex(Database.TAGS)));
            mediaFilter.setBooks(cursor.getInt(cursor.getColumnIndex(Database.BOOKS)) == 1);
            mediaFilter.setMovies(cursor.getInt(cursor.getColumnIndex(Database.MOVIES)) == 1);
            mediaFilter.setMusic(cursor.getInt(cursor.getColumnIndex("music")) == 1);
            mediaFilter.setGames(cursor.getInt(cursor.getColumnIndex(Database.GAMES)) == 1);
            mediaFilters.add(mediaFilter);
        }
        cursor.close();
        return mediaFilters;
    }

    public List<BaseMediaObject> getObjects(String table, long id) {
        String start = "id IN (";
        String booksWhere = start, moviesWhere = start, gamesWhere = start, albumsWhere = start;

        switch (table) {
            case Database.TAGS:
            case "persons":
            case "companies":
                booksWhere += this.getWhere(Database.BOOKS, table, id);
                moviesWhere += this.getWhere(Database.MOVIES, table, id);
                gamesWhere += this.getWhere(Database.GAMES, table, id);
                albumsWhere += this.getWhere(Database.ALBUMS, table, id);
                break;
            case Database.CATEGORIES:
                booksWhere += this.getCategoryWhere(Database.BOOKS, id);
                moviesWhere += this.getCategoryWhere(Database.MOVIES, id);
                gamesWhere += this.getCategoryWhere(Database.GAMES, id);
                albumsWhere += this.getCategoryWhere(Database.ALBUMS, id);
                break;
            case "lists":
                booksWhere += this.getListWhere(Database.BOOKS, id);
                moviesWhere += this.getListWhere(Database.MOVIES, id);
                gamesWhere += this.getListWhere(Database.GAMES, id);
                albumsWhere += this.getListWhere(Database.ALBUMS, id);
                break;
        }

        Map<DatabaseObject, String> mp = new LinkedHashMap<>();
        mp.put(new Book(), booksWhere);
        mp.put(new Movie(), moviesWhere);
        mp.put(new Game(), gamesWhere);
        mp.put(new Album(), albumsWhere);
        return this.getObjectList(mp);
    }

    public void deleteItem(DatabaseObject databaseObject) {
        this.getWritableDatabase().execSQL(String.format("DELETE FROM %s WHERE id=%s", databaseObject.getTable(), databaseObject.getId()));
    }

    public long insertOrUpdateBaseObject(BaseDescriptionObject baseDescriptionObject, String table, String foreignTable, long id) {
        // return if object is empty
        if(baseDescriptionObject.getTitle().trim().isEmpty()) {
            return 0L;
        }

        // get id if object already exists
        for(BaseDescriptionObject tmp : this.getBaseObjects(table, "", id, "")) {
            if(tmp.getTitle().equals(baseDescriptionObject.getTitle())) {
                baseDescriptionObject.setId(tmp.getId());
                break;
            }
        }

        SQLiteStatement statement;
        if(baseDescriptionObject.getId() == 0) {
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s(title, description) VALUES(?, ?)", table));
        } else {
            statement = this.getWritableDatabase().compileStatement(String.format("UPDATE %s SET title=?, description=? WHERE id=?", table));
            statement.bindLong(3, baseDescriptionObject.getId());
        }
        statement.bindString(1, baseDescriptionObject.getTitle());
        statement.bindString(2, baseDescriptionObject.getDescription());

        if(baseDescriptionObject.getId() == 0) {
            baseDescriptionObject.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        if(!foreignTable.trim().isEmpty()) {
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_%s WHERE %s=%s AND %s=%s", foreignTable, table, foreignTable, id, table, baseDescriptionObject.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_%s(%s, %s) VALUES(?, ?)", foreignTable, table, table, foreignTable));
            statement.bindLong(1, baseDescriptionObject.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
        return baseDescriptionObject.getId();
    }

    public List<BaseDescriptionObject> getBaseObjects(String table, String foreignTable, long id, String where) {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        if(foreignTable.trim().isEmpty()) {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s" + this.where(where), table), null);
            while (cursor.moveToNext()) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
                baseDescriptionObject.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
                baseDescriptionObject.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            cursor.close();
        } else {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s_%s WHERE %s=%s", foreignTable, table, foreignTable, id), null);
            while (cursor.moveToNext()) {
                Cursor tmp = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE id=%s", table, cursor.getLong(cursor.getColumnIndex(table))), null);
                while (tmp.moveToNext()) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(tmp.getLong(tmp.getColumnIndex("id")));
                    baseDescriptionObject.setTitle(tmp.getString(tmp.getColumnIndex(Database.TITLE)));
                    baseDescriptionObject.setDescription(tmp.getString(tmp.getColumnIndex(Database.DESCRIPTION)));
                    baseDescriptionObjects.add(baseDescriptionObject);
                }
                tmp.close();
            }
            cursor.close();
        }

        return baseDescriptionObjects;
    }

    public long insertOrUpdatePerson(Person person, String foreignTable, long id) {
        try {
            if(person.getFirstName().trim().equals("") && person.getLastName().trim().equals("")) {
                return 0L;
            }

            for(Person tmp : this.getPersons("", 0)) {
                if(tmp.getFirstName().equals(person.getFirstName()) && tmp.getLastName().equals(person.getLastName())) {
                    person.setId(tmp.getId());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SQLiteStatement statement;
        if(person.getId() == 0) {
            statement = this.getWritableDatabase().compileStatement("INSERT INTO persons(firstName, lastName, birthDate, image, description) VALUES(?, ?, ?, ?, ?)");
        } else {
            statement = this.getWritableDatabase().compileStatement("UPDATE persons SET firstName=?, lastName=?, birthDate=?, image=?, description=? WHERE id=?");
            statement.bindLong(6, person.getId());
        }
        statement.bindString(1, person.getFirstName());
        statement.bindString(2, person.getLastName());
        if(person.getBirthDate() != null) {
            String dt = ConvertHelper.convertDateToString(person.getBirthDate(), Database.DATE_FORMAT);
            if(dt != null) {
                statement.bindString(3, dt);
            } else {
                statement.bindNull(3);
            }
        } else {
            statement.bindNull(3);
        }
        if(person.getImage()!=null) {
            statement.bindBlob(4, person.getImage());
        } else {
            statement.bindNull(4);
        }
        statement.bindString(5, person.getDescription());

        if(person.getId() == 0) {
            person.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        if(!foreignTable.isEmpty()) {
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_persons WHERE %s=%s AND persons=%s", foreignTable, foreignTable, id, person.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_persons(persons, %s) VALUES(?, ?)", foreignTable, foreignTable));
            statement.bindLong(1, person.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
        return person.getId();
    }

    public List<Person> getPersons(String foreignTable, long id) throws ParseException {
        List<Person> people = new LinkedList<>();
        if(foreignTable.trim().isEmpty()) {
            Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM persons", null);
            while (cursor.moveToNext()) {
                Person person = new Person();
                person.setId(cursor.getLong(cursor.getColumnIndex("id")));
                person.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
                person.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
                int index = cursor.getColumnIndex("birthDate");
                String dt = cursor.getString(index);
                if(dt!=null) {
                    person.setBirthDate(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                }
                person.setImage(cursor.getBlob(cursor.getColumnIndex("image")));
                person.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
                people.add(person);
            }
            cursor.close();
        } else {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s_persons WHERE %s=%s", foreignTable, foreignTable, id), null);
            while (cursor.moveToNext()) {
                Cursor tmp = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM persons WHERE id=%s", cursor.getLong(cursor.getColumnIndex("persons"))), null);
                while (tmp.moveToNext()) {
                    Person person = new Person();
                    person.setId(tmp.getLong(tmp.getColumnIndex("id")));
                    person.setFirstName(tmp.getString(tmp.getColumnIndex("firstName")));
                    person.setLastName(tmp.getString(tmp.getColumnIndex("lastName")));
                    String dt = tmp.getString(tmp.getColumnIndex("birthDate"));
                    if(dt!=null) {
                        person.setBirthDate(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                    }
                    person.setImage(tmp.getBlob(tmp.getColumnIndex("image")));
                    person.setDescription(tmp.getString(tmp.getColumnIndex(Database.DESCRIPTION)));
                    people.add(person);
                }
                tmp.close();
            }
            cursor.close();
        }

        return people;
    }

    public void insertOrUpdateCompany(Company company, String foreignTable, long id) {
        try {
            if(company.getTitle().trim().equals("")) {
                return;
            }

            for(Company tmp : this.getCompanies("", 0)) {
                if(tmp.getTitle().equals(company.getTitle())) {
                    company.setId(tmp.getId());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SQLiteStatement statement;
        if(company.getId() == 0) {
            statement = this.getWritableDatabase().compileStatement("INSERT INTO companies(title, foundation, cover, description) VALUES(?, ?, ?, ?)");
        } else {
            statement = this.getWritableDatabase().compileStatement("UPDATE companies SET title=?, foundation=?, cover=?, description=? WHERE id=?");
            statement.bindLong(5, company.getId());
        }
        statement.bindString(1, company.getTitle());
        if(company.getFoundation() != null) {
            statement.bindString(2, Objects.requireNonNull(ConvertHelper.convertDateToString(company.getFoundation(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(2);
        }
        if(company.getCover() != null) {
            statement.bindBlob(3, company.getCover());
        } else {
            statement.bindNull(3);
        }
        statement.bindString(4, company.getDescription());

        if(company.getId() == 0) {
            company.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        if(!foreignTable.isEmpty()) {
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_companies WHERE %s=%s AND companies=%s", foreignTable, foreignTable, id, company.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_companies(companies, %s) VALUES(?, ?)", foreignTable, foreignTable));
            statement.bindLong(1, company.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
    }

    public List<Company> getCompanies(String foreignTable, long id) throws ParseException {
        List<Company> companies = new LinkedList<>();
        if(foreignTable.trim().isEmpty()) {
            Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM companies", null);
            while (cursor.moveToNext()) {
                Company company = new Company();
                company.setId(cursor.getLong(cursor.getColumnIndex("id")));
                company.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
                String dt = cursor.getString(cursor.getColumnIndex("foundation"));
                if(dt!=null) {
                    company.setFoundation(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                }
                company.setCover(cursor.getBlob(cursor.getColumnIndex(Database.COVER)));
                company.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
                companies.add(company);
            }
            cursor.close();
        } else {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s_companies WHERE %s=%s", foreignTable, foreignTable, id), null);
            while (cursor.moveToNext()) {
                Cursor tmp = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM companies WHERE id=%s", cursor.getLong(cursor.getColumnIndex("companies"))), null);
                while (tmp.moveToNext()) {
                    Company company = new Company();
                    company.setId(tmp.getLong(tmp.getColumnIndex("id")));
                    company.setTitle(tmp.getString(tmp.getColumnIndex(Database.TITLE)));
                    String dt = tmp.getString(tmp.getColumnIndex("foundation"));
                    if(dt!=null) {
                        company.setFoundation(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                    }
                    company.setCover(tmp.getBlob(tmp.getColumnIndex(Database.COVER)));
                    company.setDescription(tmp.getString(tmp.getColumnIndex(Database.DESCRIPTION)));
                    companies.add(company);
                }
                tmp.close();
            }
            cursor.close();
        }

        return companies;
    }

    public void insertExampleData() {
        for(int i = 0; i<=10; i++) {
            Book book = new Book();
            book.setTitle("book " + i);
            this.insertOrUpdateBook((Book) this.setFields(book));
        }
        for(int i = 0; i<=10; i++) {
            Movie movie = new Movie();
            movie.setTitle("movie " + i);
            this.insertOrUpdateMovie((Movie) this.setFields(movie));
        }
        for(int i = 0; i<=10; i++) {
            Album album = new Album();
            album.setTitle("movie " + i);
            this.insertOrUpdateAlbum((Album) this.setFields(album));
        }
        for(int i = 0; i<=10; i++) {
            Game game = new Game();
            game.setTitle("game " + i);
            this.insertOrUpdateGame((Game) this.setFields(game));
        }
    }

    public void close() {
        if(this.database != null && this.database.isOpen()) {
            this.database.close();
        }
    }

    private BaseMediaObject setFields(BaseMediaObject baseMediaObject) {
        for(int j = 0; j<=3; j++) {
            Random generator = new Random();
            Company company = new Company();
            company.setTitle("Company "  + generator.nextInt(3));
            baseMediaObject.getCompanies().add(company);
        }

        for(int j = 0; j<=3; j++) {
            Random generator = new Random();
            Person person = new Person();
            person.setFirstName("Person");
            person.setLastName(String.valueOf(generator.nextInt(3)));
            baseMediaObject.getPersons().add(person);
        }

        for(int j = 0; j<=3; j++) {
            Random generator = new Random();
            BaseDescriptionObject tag = new BaseDescriptionObject();
            tag.setTitle("Tag "  + generator.nextInt(3));
            baseMediaObject.getTags().add(tag);
        }

        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        baseDescriptionObject.setTitle("Category");
        baseMediaObject.setCategory(baseDescriptionObject);
        return baseMediaObject;
    }

    private void insertOrUpdateSong(Song song, long id) {
        SQLiteStatement statement = this.getStatement(song, Arrays.asList(Database.LENGTH, Database.PATH, "album"));
        int position = this.insertOrUpdateBaseMediaObject(statement, song);
        statement.bindDouble(++position, song.getLength());
        statement.bindString(++position, song.getPath());
        statement.bindLong(++position, id);

        if(song.getId() == 0) {
            song.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(song, song.getTable());
    }

    private List<Song> getSongs(String where) throws ParseException {
        List<Song> songs = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM songs" + this.where(where), null);
        while (cursor.moveToNext()) {
            Song song = new Song();
            this.getMediaObjectFromCursor(cursor, song, "songs");
            song.setLength(cursor.getDouble(cursor.getColumnIndex(Database.LENGTH)));
            song.setPath(cursor.getString(cursor.getColumnIndex(Database.PATH)));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    private SQLiteStatement getStatement(DatabaseObject databaseObject, List<String> columns) {
        List<String> baseColumns = Arrays.asList(Database.TITLE, "originalTitle", "releaseDate", "code", "price", "category", Database.COVER, Database.DESCRIPTION, "rating_web", "rating_own", "rating_note");
        String[] allColumns = new String[baseColumns.size() + columns.size()];

        int i = 0;
        for(String column : baseColumns) {
            allColumns[i] = column;
            i++;
        }
        for(String column : columns) {
            allColumns[i] = column;
            i++;
        }

        return this.getBaseStatement(databaseObject, Arrays.asList(allColumns));
    }

    private SQLiteStatement getBaseStatement(DatabaseObject databaseObject, List<String> columns) {
        String[] allColumns = new String[columns.size()];
        String[] allQuestionMarks = new String[columns.size()];

        int i = 0;
        for(String column : columns) {
            allColumns[i] = column;
            allQuestionMarks[i] = "?";
            i++;
        }

        if(databaseObject.getId() == 0) {
            String columnString = TextUtils.join(", ", allColumns);
            String questionString = TextUtils.join(", ", allQuestionMarks);

            return this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s(%s) VALUES(%s)", databaseObject.getTable(), columnString, questionString));
        } else {
            String columnString = (TextUtils.join("=?, ", allColumns)) + "=?";
            return this.getWritableDatabase().compileStatement(String.format("UPDATE %s SET %s WHERE id=%s", databaseObject.getTable(), columnString, databaseObject.getId()));
        }
    }

    private int insertOrUpdateBaseMediaObject(SQLiteStatement sqLiteStatement, BaseMediaObject baseMediaObject) {
        sqLiteStatement.bindString(1, baseMediaObject.getTitle());
        if(baseMediaObject.getOriginalTitle() != null) {
            sqLiteStatement.bindString(2, baseMediaObject.getOriginalTitle());
        } else {
            sqLiteStatement.bindString(2, "");
        }
        if(baseMediaObject.getReleaseDate()!=null) {
            sqLiteStatement.bindString(3, Objects.requireNonNull(ConvertHelper.convertDateToString(baseMediaObject.getReleaseDate(), Database.DATE_FORMAT)));
        } else {
            sqLiteStatement.bindNull(3);
        }
        sqLiteStatement.bindString(4, baseMediaObject.getCode());
        sqLiteStatement.bindDouble(5, baseMediaObject.getPrice());
        if(baseMediaObject.getCategory()!=null) {
            sqLiteStatement.bindLong(6, this.insertOrUpdateBaseObject(baseMediaObject.getCategory(), Database.CATEGORIES, "", 0));
        } else {
            sqLiteStatement.bindLong(6, 0);
        }
        if(baseMediaObject.getCover() != null) {
            sqLiteStatement.bindBlob(7, baseMediaObject.getCover());
        } else {
            sqLiteStatement.bindNull(7);
        }
        if(baseMediaObject.getDescription() != null) {
            sqLiteStatement.bindString(8, baseMediaObject.getDescription());
        } else {
            sqLiteStatement.bindNull(8);
        }
        sqLiteStatement.bindDouble(9, baseMediaObject.getRatingWeb());
        sqLiteStatement.bindDouble(10, baseMediaObject.getRatingOwn());
        if(baseMediaObject.getRatingNote() != null) {
            sqLiteStatement.bindString(11, baseMediaObject.getRatingNote());
        } else {
            sqLiteStatement.bindNull(11);
        }
        return 11;
    }

    private void saveForeignTables(BaseMediaObject baseMediaObject, String table) {
        for(BaseDescriptionObject tag : baseMediaObject.getTags()) {
            this.insertOrUpdateBaseObject(tag, Database.TAGS, table, baseMediaObject.getId());
        }
        for(Person person : baseMediaObject.getPersons()) {
            this.insertOrUpdatePerson(person, table, baseMediaObject.getId());
        }
        for(Company company : baseMediaObject.getCompanies()) {
            this.insertOrUpdateCompany(company, table, baseMediaObject.getId());
        }
        for(LibraryObject libraryObject : baseMediaObject.getLibraryObjects()) {
            this.insertOrUpdateLibraryObject(libraryObject, baseMediaObject);
        }
    }

    private void getMediaObjectFromCursor(Cursor cursor, BaseMediaObject baseMediaObject, String table) throws ParseException {
        baseMediaObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
        baseMediaObject.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
        baseMediaObject.setOriginalTitle(cursor.getString(cursor.getColumnIndex("originalTitle")));
        String dt = cursor.getString(cursor.getColumnIndex("releaseDate"));
        if(dt != null) {
            baseMediaObject.setReleaseDate(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
        }
        baseMediaObject.setCode(cursor.getString(cursor.getColumnIndex("code")));
        baseMediaObject.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
        int category = cursor.getInt(cursor.getColumnIndex("category"));
        if(category != 0) {
            baseMediaObject.setCategory(this.getBaseObjects(Database.CATEGORIES, "", 0, Database.ID_FILTER + category).get(0));
        }
        baseMediaObject.setCover(cursor.getBlob(cursor.getColumnIndex(Database.COVER)));
        baseMediaObject.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
        baseMediaObject.setRatingOwn(cursor.getDouble(cursor.getColumnIndex("rating_own")));
        baseMediaObject.setRatingWeb(cursor.getDouble(cursor.getColumnIndex("rating_web")));
        baseMediaObject.setRatingNote(cursor.getString(cursor.getColumnIndex("rating_note")));

        baseMediaObject.setTags(this.getBaseObjects(Database.TAGS, table, baseMediaObject.getId(), ""));
        baseMediaObject.setPersons(this.getPersons(table, baseMediaObject.getId()));
        baseMediaObject.setCompanies(this.getCompanies(table, baseMediaObject.getId()));

        baseMediaObject.setLendOut(false);
        List<LibraryObject> libraryObjects = this.getLibraryObjects("media=" + baseMediaObject.getId() + " AND type='" + table + "'");
        for(LibraryObject libraryObject : libraryObjects) {
            if(libraryObject.getReturned() == null) {
                baseMediaObject.setLendOut(true);
                break;
            }
        }
        baseMediaObject.setLibraryObjects(libraryObjects);
    }

    private String getListWhere(String table, long id) {
        List<Long> idList = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT media FROM media_lists WHERE list=? AND type=?", new String[]{String.valueOf(id), table});
        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(cursor.getColumnIndex("media")));
        }
        cursor.close();
        return TextUtils.join(", ", idList) + ")";
    }

    private String getCategoryWhere(String table, long id) {
        List<Long> idList = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT id FROM %s WHERE category=?", table), new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(cursor.getColumnIndex("id")));
        }
        cursor.close();
        return TextUtils.join(", ", idList) + ")";
    }

    private String getWhere(String table, String foreignTable, long id) {
        List<Long> idList = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT %s FROM %s_%s WHERE %s=?", table, table, foreignTable, foreignTable), new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(cursor.getColumnIndex(table)));
        }
        cursor.close();
        return TextUtils.join(", ", idList) + ")";
    }

    private SQLiteDatabase getWritableDatabase() {
        if(this.database == null)  {
            this.database = this.getWritableDatabase(this.password);
        } else {
            if(this.database.isReadOnly()) {
                this.database.close();
                this.database = this.getWritableDatabase(this.password);
            }
            if(!this.database.isOpen()) {
                this.database = this.getReadableDatabase(this.password);
            }
        }
        return this.database;
    }

    private SQLiteDatabase getReadableDatabase() {
        if(this.database != null) {
            if(!this.database.isOpen()) {
                this.database = this.getReadableDatabase(this.password);
            }
        } else {
            this.database = this.getReadableDatabase(this.password);
        }
        return this.database;
    }

    public String copyDatabase() throws IOException {
        File sd = this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        if(sd != null && sd.canWrite()) {
            String currentDBPath = this.getWritableDatabase().getPath();
            String backupDBPath = new File(currentDBPath).getName();
            File currentDB = new File(currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                try (FileChannel src = new FileInputStream(currentDB).getChannel();
                     FileChannel dst = new FileOutputStream(backupDB).getChannel()) {
                    dst.transferFrom(src, 0, src.size());
                }
            }
        }
        return this.password;
    }

    public void copyDatabaseFromDownload() throws IOException {
        this.close();

        File sd = this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        if(sd != null && sd.canWrite()) {
            String currentDBPath = this.getWritableDatabase().getPath();
            String backupDBPath = new File(currentDBPath).getName();
            File currentDB = new File(currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                try (FileChannel src = new FileOutputStream(currentDB).getChannel();
                     FileChannel dst = new FileInputStream(backupDB).getChannel()) {

                    src.transferFrom(dst, 0, dst.size());
                }
            }
        }
    }

    private void updateDatabase(String content, int oldVersion, int newVersion, SQLiteDatabase database) {
        Map<Integer, String> queries = new LinkedHashMap<>();
        oldVersion++;
        for(int i = oldVersion; i<=newVersion; i++) {
            boolean start = false;
            StringBuilder versionQueries = new StringBuilder();
            for(String line : content.split("\n")) {
                if(!start && line.trim().equals("-- Version " + oldVersion)) {
                    start = true;
                    continue;
                }
                if(start) {
                    if(!line.startsWith("--")) {
                        versionQueries.append(line);
                    } else {
                        break;
                    }
                }
            }
            queries.put(i, versionQueries.toString().trim());
        }

        for(int i = oldVersion; i<=newVersion; i++) {
            String updateString = queries.get(i);
            if(updateString != null) {
                for(String updateQuery : updateString.split(";")) {
                    try {
                        database.execSQL(updateQuery);
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    private static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputReader);

        String line;
        StringBuilder text = new StringBuilder();
        try {
            while (( line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        } finally {
            try {
                bufferedReader.close();
                inputReader.close();
                inputStream.close();
            } catch (Exception ignored) {}
        }
        return text.toString();
    }

    private String where(String where) {
        if(!where.trim().isEmpty()) {
            return " WHERE " + where;
        }
        return "";
    }
}
