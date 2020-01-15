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
import java.util.Objects;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.books.Magazine;
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
        this.insertOrUpdateBaseMediaObject(statement, album, album.getTable());
        statement.bindString(9, album.getType().name());
        statement.bindLong(10, album.getNumberOfDisks());
        statement.execute();
        statement.close();

        for(Song song : album.getSongs()) {
            this.insertOrUpdateSong(song, album.getId());
        }
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
        this.insertOrUpdateBaseMediaObject(statement, movie, movie.getTable());
        statement.bindString(9, movie.getType().name());
        statement.bindDouble(10, movie.getLength());
        statement.bindString(11, movie.getPath());
        statement.execute();
        statement.close();
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
        SQLiteStatement statement = this.getStatement(book, Arrays.asList("type", "numberOfPages", "path"));
        this.insertOrUpdateBaseMediaObject(statement, book, book.getTable());
        if(book.getType()!=null) {
            statement.bindString(9, book.getType().name());
        } else {
            statement.bindNull(9);
        }
        statement.bindDouble(10, book.getNumberOfPages());
        statement.bindString(11, book.getPath());
        statement.execute();
        statement.close();
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
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public void insertOrUpdateMagazine(Magazine magazine) {
        SQLiteStatement statement = this.getStatement(magazine, Arrays.asList("type", "numberOfPages", "path", "editions", "topics"));
        this.insertOrUpdateBaseMediaObject(statement, magazine, magazine.getTable());
        statement.bindString(9, magazine.getType().name());
        statement.bindDouble(10, magazine.getNumberOfPages());
        statement.bindString(11, magazine.getPath());
        statement.bindString(12, magazine.getEdition());
        statement.bindString(13, TextUtils.join("\n", magazine.getTopics()));
        statement.execute();
        statement.close();
    }

    public List<Magazine> getMagazines(String where) throws Exception {
        List<Magazine> magazines = new LinkedList<>();
        if(!where.trim().isEmpty()) {
            where = " WHERE " + where;
        }
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM magazines" + where, null);
        while (cursor.moveToNext()) {
            Magazine magazine = new Magazine();
            this.getMediaObjectFromCursor(cursor, magazine, "magazines");
            String type = cursor.getString(cursor.getColumnIndex("type"));
            if(type != null) {
                if(!type.trim().isEmpty()) {
                    magazine.setType(Book.Type.valueOf(type));
                }
            }
            magazine.setNumberOfPages(cursor.getInt(cursor.getColumnIndex("numberOfPages")));
            magazine.setPath(cursor.getString(cursor.getColumnIndex("path")));
            magazine.setEdition(cursor.getString(cursor.getColumnIndex("edition")));
            String topics = cursor.getString(cursor.getColumnIndex("topics"));
            for(String line : topics.split("\n")) {
                magazine.getTopics().add(line.trim());
            }
            magazines.add(magazine);
        }
        cursor.close();
        return magazines;
    }

    public void insertOrUpdateGame(Game game) {
        SQLiteStatement statement = this.getStatement(game, Arrays.asList("type", "length"));
        this.insertOrUpdateBaseMediaObject(statement, game, game.getTable());
        statement.bindString(9, game.getType().name());
        statement.bindDouble(10, game.getLength());
        statement.execute();
        statement.close();
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

    public void deleteItem(DatabaseObject databaseObject) {
        this.getWritableDatabase().execSQL(String.format("DELETE FROM %s WHERE id=%s", databaseObject.getTable(), databaseObject.getId()));
    }

    private void insertOrUpdateSong(Song song, long id) {
        SQLiteStatement statement = this.getStatement(song, Arrays.asList("length", "path", "album"));
        this.insertOrUpdateBaseMediaObject(statement, song, song.getTable());
        statement.bindDouble(9, song.getLength());
        statement.bindString(10, song.getPath());
        statement.bindLong(11, id);
        statement.execute();
        statement.close();
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
        String[] allQuestionMarks = new String[baseColumns.size() + columns.size()];

        int i = 0;
        for(String column : baseColumns) {
            allColumns[i] = column;
            allQuestionMarks[i] = "?";
            i++;
        }
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

    private void insertOrUpdateBaseMediaObject(SQLiteStatement sqLiteStatement, BaseMediaObject baseMediaObject, String table) {
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
        for(BaseDescriptionObject tag : baseMediaObject.getTags()) {
            this.insertOrUpdateBaseObject(tag, "tags", table, baseMediaObject.getId());
        }
        for(Person person : baseMediaObject.getPersons()) {
            this.insertOrUpdatePerson(person, table, baseMediaObject.getId());
        }
        for(Company company : baseMediaObject.getCompanies()) {
            this.insertOrUpdateCompany(company, table, baseMediaObject.getId());
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
    }

    private long insertOrUpdateBaseObject(BaseDescriptionObject baseDescriptionObject, String table, String foreignTable, long id) {
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
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_%s WHERE %s=%s", foreignTable, table, foreignTable, id));
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

    private void insertOrUpdatePerson(Person person, String foreignTable, long id) {
        try {
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
            statement.bindLong(6, id);
        }
        statement.bindString(1, person.getFirstName());
        statement.bindString(2, person.getLastName());
        if(person.getBirthDate() != null) {
            statement.bindString(3, Objects.requireNonNull(Converter.convertDateToString(person.getBirthDate(), "yyyy-MM-dd")));
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

        this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_persons WHERE %s=%s", foreignTable, foreignTable, id));
        statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_persons(persons, %s) VALUES(?, ?)", foreignTable, foreignTable));
        statement.bindLong(1, person.getId());
        statement.bindLong(2, id);
        statement.executeInsert();
        statement.close();
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
                String dt = cursor.getString(cursor.getColumnIndex("birthDate"));
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

    private void insertOrUpdateCompany(Company company, String foreignTable, long id) {
        try {
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
            statement.bindLong(5, id);
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

        this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_companies WHERE %s=%s", foreignTable, foreignTable, id));
        statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_companies(companies, %s) VALUES(?, ?)", foreignTable, foreignTable));
        statement.bindLong(1, company.getId());
        statement.bindLong(2, id);
        statement.executeInsert();
        statement.close();
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
