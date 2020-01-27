package de.domjos.myarchivelibrary.database;


import android.content.Context;
import android.text.TextUtils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.model.media.music.Song;

public class Database extends SQLiteOpenHelper {
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
        if(content != null) {
            for(String query : content.split(";")) {
                db.execSQL(query.trim());
            }
        }
    }

    public void insertOrUpdateAlbum(Album album) {
        SQLiteStatement statement = this.getStatement(album, Arrays.asList("type", "numberOfDisks"));
        this.insertOrUpdateBaseMediaObject(statement, album);
        statement.bindString(9, album.getType().name());
        statement.bindLong(10, album.getNumberOfDisks());

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

    public List<Album> getAlbums(String where) throws Exception {
        List<Album> albums = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM albums" + where, null);
        while (cursor.moveToNext()) {
            Album album = new Album();
            this.getMediaObjectFromCursor(cursor, album, "albums");
            String type = cursor.getString(cursor.getColumnIndex("type"));
            if(type != null) {
                if(!type.trim().isEmpty()) {
                    album.setType(Album.Type.valueOf(type));
                }
            }
            album.setNumberOfDisks(cursor.getInt(cursor.getColumnIndex("numberOfDisks")));
            album.setSongs(this.getSongs("album=" + album.getId()));
            albums.add(album);
        }
        cursor.close();
        return albums;
    }

    public void insertOrUpdateMovie(Movie movie) {
        SQLiteStatement statement = this.getStatement(movie, Arrays.asList("type", "length", "path"));
        this.insertOrUpdateBaseMediaObject(statement, movie);
        statement.bindString(9, movie.getType().name());
        statement.bindDouble(10, movie.getLength());
        statement.bindString(11, movie.getPath());

        if(movie.getId()==0) {
            movie.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(movie, movie.getTable());
    }

    public List<Movie> getMovies(String where) throws Exception {
        List<Movie> movies = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM movies" + where, null);
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            this.getMediaObjectFromCursor(cursor, movie, "movies");
            String type = cursor.getString(cursor.getColumnIndex("type"));
            if(type != null) {
                if(!type.trim().isEmpty()) {
                    movie.setType(Movie.Type.valueOf(type));
                }
            }
            movie.setLength(cursor.getDouble(cursor.getColumnIndex("length")));
            movie.setPath(cursor.getString(cursor.getColumnIndex("path")));
            movies.add(movie);
        }
        cursor.close();
        return movies;
    }

    public void insertOrUpdateBook(Book book) {
        SQLiteStatement statement = this.getStatement(book, Arrays.asList("type", "numberOfPages", "path", "edition", "topics"));
        this.insertOrUpdateBaseMediaObject(statement, book);
        if(book.getType()!=null) {
            statement.bindString(9, book.getType().name());
        } else {
            statement.bindNull(9);
        }
        statement.bindDouble(10, book.getNumberOfPages());
        statement.bindString(11, book.getPath());
        statement.bindString(12, book.getEdition());
        statement.bindString(13, TextUtils.join("\n", book.getTopics()));

        if(book.getId() == 0) {
            book.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(book, book.getTable());
    }

    public List<Book> getBooks(String where) throws Exception {
        List<Book> books = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM books" + where, null);
        while (cursor.moveToNext()) {
            Book book = new Book();
            this.getMediaObjectFromCursor(cursor, book, "books");
            String type = cursor.getString(cursor.getColumnIndex("type"));
            if(type != null) {
                if(!type.trim().isEmpty()) {
                    book.setType(Book.Type.valueOf(type));
                }
            }
            book.setNumberOfPages(cursor.getInt(cursor.getColumnIndex("numberOfPages")));
            book.setPath(cursor.getString(cursor.getColumnIndex("path")));
            book.setEdition(cursor.getString(cursor.getColumnIndex("edition")));
            book.setTopics(Arrays.asList(cursor.getString(cursor.getColumnIndex("topics")).split("\n")));
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public void insertOrUpdateGame(Game game) {
        SQLiteStatement statement = this.getStatement(game, Arrays.asList("type", "length"));
        this.insertOrUpdateBaseMediaObject(statement, game);
        statement.bindString(9, game.getType().name());
        statement.bindDouble(10, game.getLength());

        if(game.getId() == 0) {
            game.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(game, game.getTable());
    }

    public List<Game> getGames(String where) throws Exception {
        List<Game> games = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM games" + where, null);
        while (cursor.moveToNext()) {
            Game game = new Game();
            this.getMediaObjectFromCursor(cursor, game, "games");
            String type = cursor.getString(cursor.getColumnIndex("type"));
            if(type != null) {
                if(!type.trim().isEmpty()) {
                    game.setType(Game.Type.valueOf(type));
                }
            }
            game.setLength(cursor.getDouble(cursor.getColumnIndex("length")));
            games.add(game);
        }
        cursor.close();
        return games;
    }

    public void insertOrUpdateLibraryObject(LibraryObject libraryObject, BaseMediaObject baseMediaObject) {
        SQLiteStatement sqLiteStatement = this.getBaseStatement(libraryObject, Arrays.asList("media", "type", "person", "numberOfDays", "numberOfWeeks", "deadLine", "returnedAt"));
        sqLiteStatement.bindLong(1, baseMediaObject.getId());
        if(baseMediaObject instanceof Book) {
            sqLiteStatement.bindString(2, "books");
        }
        if(baseMediaObject instanceof Movie) {
            sqLiteStatement.bindString(2, "movies");
        }
        if(baseMediaObject instanceof Album) {
            sqLiteStatement.bindString(2, "albums");
        }
        if(baseMediaObject instanceof Game) {
            sqLiteStatement.bindString(2, "games");
        }
        sqLiteStatement.bindLong(3, this.insertOrUpdatePerson(libraryObject.getPerson(), "", 0));
        sqLiteStatement.bindLong(4, libraryObject.getNumberOfDays());
        sqLiteStatement.bindLong(5, libraryObject.getNumberOfWeeks());
        if(libraryObject.getDeadLine() != null) {
            sqLiteStatement.bindString(6, Objects.requireNonNull(Converter.convertDateToString(libraryObject.getDeadLine(), "yyyy-MM-dd")));
        } else {
            sqLiteStatement.bindNull(6);
        }
        if(libraryObject.getReturned() != null) {
            sqLiteStatement.bindString(7, Converter.convertDateToString(libraryObject.getReturned(), "yyyy-MM-dd"));
        } else {
            sqLiteStatement.bindNull(7);
        }
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }

    private List<LibraryObject> getLibraryObjects(String where) throws Exception {
        List<LibraryObject> libraryObjects = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }

        List<Person> persons = this.getPersons("", 0);
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM library" + where, null);
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
            String deadline = cursor.getString(cursor.getColumnIndex("deadLine"));
            if(deadline!=null) {
                if(!deadline.isEmpty()) {
                    libraryObject.setDeadLine(Converter.convertStringToDate(deadline, "yyyy-MM-dd"));
                }
            }
            String returnedAt = cursor.getString(cursor.getColumnIndex("returnedAt"));
            if(returnedAt!=null) {
                if(!returnedAt.isEmpty()) {
                    libraryObject.setReturned(Converter.convertStringToDate(returnedAt, "yyyy-MM-dd"));
                }
            }
            libraryObjects.add(libraryObject);
        }
        cursor.close();
        return libraryObjects;
    }

    public void insertOrUpdateMediaList(MediaList mediaList) {
        SQLiteStatement sqLiteStatement = this.getBaseStatement(mediaList, Arrays.asList("title", "deadLine", "description"));
        sqLiteStatement.bindString(1, mediaList.getTitle());
        if(mediaList.getDeadLine() != null) {
            sqLiteStatement.bindString(2, Converter.convertDateToString(mediaList.getDeadLine(), "yyyy-MM-dd"));
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
                sqLiteStatement.bindString(3, "books");
            }
            if(baseMediaObject instanceof Movie) {
                sqLiteStatement.bindString(3, "movies");
            }
            if(baseMediaObject instanceof Album) {
                sqLiteStatement.bindString(3, "albums");
            }
            if(baseMediaObject instanceof Game) {
                sqLiteStatement.bindString(3, "games");
            }
            sqLiteStatement.executeInsert();
            sqLiteStatement.close();
        }
    }

    public List<MediaList> getMediaLists(String where) throws Exception {
        List<MediaList> mediaLists = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM lists" + where, null);
        while (cursor.moveToNext()) {
            MediaList mediaList = new MediaList();
            mediaList.setId(cursor.getLong(cursor.getColumnIndex("id")));
            mediaList.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            mediaList.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            String deadLine = cursor.getString(cursor.getColumnIndex("deadLine"));
            if(deadLine != null) {
                if( !deadLine.isEmpty() ) {
                    mediaList.setDeadLine(Converter.convertStringToDate(deadLine, "yyyy-MM-dd"));
                }
            }
            mediaList.setBaseMediaObjects(this.getObjects("lists", mediaList.getId()));
            mediaLists.add(mediaList);
        }
        cursor.close();
        return mediaLists;
    }

    public List<BaseMediaObject> getObjects(String table, long id) throws Exception {
        String booksWhere = "id IN (", moviesWhere = "id IN (", gamesWhere = "id IN (", albumsWhere = "id IN (";

        switch (table) {
            case "tags":
            case "persons":
            case "companies":
                booksWhere += this.getWhere("books", table, id);
                moviesWhere += this.getWhere("movies", table, id);
                gamesWhere += this.getWhere("games", table, id);
                albumsWhere += this.getWhere("albums", table, id);
                break;
            case "categories":
                booksWhere += this.getCategoryWhere("books", id);
                moviesWhere += this.getCategoryWhere("movies", id);
                gamesWhere += this.getCategoryWhere("games", id);
                albumsWhere += this.getCategoryWhere("albums", id);
            case "lists":
                booksWhere += this.getListWhere("books", id);
                moviesWhere += this.getListWhere("movies", id);
                gamesWhere += this.getListWhere("games", id);
                albumsWhere += this.getListWhere("albums", id);
                break;
        }

        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        baseMediaObjects.addAll(this.getBooks(booksWhere));
        baseMediaObjects.addAll(this.getMovies(moviesWhere));
        baseMediaObjects.addAll(this.getGames(gamesWhere));
        baseMediaObjects.addAll(this.getAlbums(albumsWhere));
        return baseMediaObjects;
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
            statement.bindLong(3, id);
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
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_%s WHERE %s=%s", foreignTable, table, table, baseDescriptionObject.getId()));
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
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s" + where, table), null);
            while (cursor.moveToNext()) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
                baseDescriptionObject.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                baseDescriptionObject.setDescription(cursor.getString(cursor.getColumnIndex("description")));
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
                    baseDescriptionObject.setTitle(tmp.getString(tmp.getColumnIndex("title")));
                    baseDescriptionObject.setDescription(tmp.getString(tmp.getColumnIndex("description")));
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
            String dt = Converter.convertDateToString(person.getBirthDate(), "yyyy-MM-dd");
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
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_persons WHERE persons=%s", foreignTable, person.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_persons(persons, %s) VALUES(?, ?)", foreignTable, foreignTable));
            statement.bindLong(1, person.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
        return person.getId();
    }

    public List<Person> getPersons(String foreignTable, long id) throws Exception {
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
                    if(!dt.isEmpty()) {
                        person.setBirthDate(Converter.convertStringToDate(dt, "yyyy-MM-dd"));
                    }
                }
                person.setImage(cursor.getBlob(cursor.getColumnIndex("image")));
                person.setDescription(cursor.getString(cursor.getColumnIndex("description")));
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
                        if(!dt.isEmpty()) {
                            person.setBirthDate(Converter.convertStringToDate(dt, "yyyy-MM-dd"));
                        }
                    }
                    person.setImage(tmp.getBlob(tmp.getColumnIndex("image")));
                    person.setDescription(tmp.getString(tmp.getColumnIndex("description")));
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
            statement.bindString(2, Converter.convertDateToString(company.getFoundation(), "yyyy-MM-dd"));
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
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_companies WHERE companies=%s", foreignTable, company.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_companies(companies, %s) VALUES(?, ?)", foreignTable, foreignTable));
            statement.bindLong(1, company.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
    }

    public List<Company> getCompanies(String foreignTable, long id) throws Exception {
        List<Company> companies = new LinkedList<>();
        if(foreignTable.trim().isEmpty()) {
            Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM companies", null);
            while (cursor.moveToNext()) {
                Company company = new Company();
                company.setId(cursor.getLong(cursor.getColumnIndex("id")));
                company.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                String dt = cursor.getString(cursor.getColumnIndex("foundation"));
                if(dt!=null) {
                    if(!dt.isEmpty()) {
                        company.setFoundation(Converter.convertStringToDate(dt, "yyyy-MM-dd"));
                    }
                }
                company.setCover(cursor.getBlob(cursor.getColumnIndex("cover")));
                company.setDescription(cursor.getString(cursor.getColumnIndex("description")));
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
                    company.setTitle(tmp.getString(tmp.getColumnIndex("title")));
                    String dt = tmp.getString(tmp.getColumnIndex("foundation"));
                    if(dt!=null) {
                        if(!dt.isEmpty()) {
                            company.setFoundation(Converter.convertStringToDate(dt, "yyyy-MM-dd"));
                        }
                    }
                    company.setCover(tmp.getBlob(tmp.getColumnIndex("cover")));
                    company.setDescription(tmp.getString(tmp.getColumnIndex("description")));
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
        SQLiteStatement statement = this.getStatement(song, Arrays.asList("length", "path", "album"));
        this.insertOrUpdateBaseMediaObject(statement, song);
        statement.bindDouble(9, song.getLength());
        statement.bindString(10, song.getPath());
        statement.bindLong(11, id);

        if(song.getId() == 0) {
            song.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(song, song.getTable());
    }

    private List<Song> getSongs(String where) throws Exception {
        List<Song> songs = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM songs" + where, null);
        while (cursor.moveToNext()) {
            Song song = new Song();
            this.getMediaObjectFromCursor(cursor, song, "songs");
            song.setLength(cursor.getDouble(cursor.getColumnIndex("length")));
            song.setPath(cursor.getString(cursor.getColumnIndex("path")));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    private SQLiteStatement getStatement(DatabaseObject databaseObject, List<String> columns) {
        List<String> baseColumns = Arrays.asList("title", "originalTitle", "releaseDate", "code", "price", "category", "cover", "description");
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

    private void insertOrUpdateBaseMediaObject(SQLiteStatement sqLiteStatement, BaseMediaObject baseMediaObject) {
        sqLiteStatement.bindString(1, baseMediaObject.getTitle());
        sqLiteStatement.bindString(2, baseMediaObject.getOriginalTitle());
        if(baseMediaObject.getReleaseDate()!=null) {
            sqLiteStatement.bindString(3, Objects.requireNonNull(Converter.convertDateToString(baseMediaObject.getReleaseDate(), "yyyy-MM-dd")));
        } else {
            sqLiteStatement.bindNull(3);
        }
        sqLiteStatement.bindString(4, baseMediaObject.getCode());
        sqLiteStatement.bindDouble(5, baseMediaObject.getPrice());
        if(baseMediaObject.getCategory()!=null) {
            sqLiteStatement.bindLong(6, this.insertOrUpdateBaseObject(baseMediaObject.getCategory(), "categories", "", 0));
        } else {
            sqLiteStatement.bindLong(6, 0);
        }
        if(baseMediaObject.getCover() != null) {
            sqLiteStatement.bindBlob(7, baseMediaObject.getCover());
        } else {
            sqLiteStatement.bindNull(7);
        }
        sqLiteStatement.bindString(8, baseMediaObject.getDescription());
    }

    private void saveForeignTables(BaseMediaObject baseMediaObject, String table) {
        for(BaseDescriptionObject tag : baseMediaObject.getTags()) {
            this.insertOrUpdateBaseObject(tag, "tags", table, baseMediaObject.getId());
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

    private void getMediaObjectFromCursor(Cursor cursor, BaseMediaObject baseMediaObject, String table) throws Exception {
        baseMediaObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
        baseMediaObject.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        baseMediaObject.setOriginalTitle(cursor.getString(cursor.getColumnIndex("originalTitle")));
        String dt = cursor.getString(cursor.getColumnIndex("releaseDate"));
        if(dt != null) {
            if(!dt.isEmpty()) {
                baseMediaObject.setReleaseDate(Converter.convertStringToDate(dt, "yyyy-MM-dd"));
            }
        }
        baseMediaObject.setCode(cursor.getString(cursor.getColumnIndex("code")));
        baseMediaObject.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
        int category = cursor.getInt(cursor.getColumnIndex("category"));
        if(category != 0) {
            baseMediaObject.setCategory(this.getBaseObjects("categories", "", 0, "id=" + category).get(0));
        }
        baseMediaObject.setCover(cursor.getBlob(cursor.getColumnIndex("cover")));
        baseMediaObject.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        baseMediaObject.setTags(this.getBaseObjects("tags", table, baseMediaObject.getId(), ""));
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
        Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT %s FROM %s_%s WHERE id=?", table, table, foreignTable), new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(cursor.getColumnIndex(table)));
        }
        cursor.close();
        return TextUtils.join(", ", idList) + ")";
    }

    private SQLiteDatabase getWritableDatabase() {
        return this.getWritableDatabase(this.password);
    }

    private SQLiteDatabase getReadableDatabase() {
        return this.getReadableDatabase(this.password);
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
        }
        return text.toString();
    }
}
